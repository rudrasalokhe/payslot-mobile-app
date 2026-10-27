package com.docdirect.app.ui.consultation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.TextureView
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.docdirect.app.data.agora.AgoraConfig
import com.docdirect.app.ui.theme.*
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import java.net.URLEncoder
import kotlinx.coroutines.delay

private const val TAG = "AgoraVideoCallScreen"

@Composable
fun AgoraVideoCallScreen(
    appointmentId: String,
    localUserId: String,
    localUserName: String,
    remoteUserName: String,
    isDoctor: Boolean,
    onCallEnded: () -> Unit
) {
    val context = LocalContext.current
    val channelName = remember { AgoraConfig.getChannelName(appointmentId) }
    val localUid = remember { AgoraConfig.getUserUid(localUserId, isDoctor) }

    var engine by remember { mutableStateOf<RtcEngine?>(null) }
    var isJoined by remember { mutableStateOf(false) }
    var remoteUid by remember { mutableIntStateOf(-1) }
    var isSimulatedRemote by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isCamOff by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var callDurationSeconds by remember { mutableIntStateOf(0) }
    var showEndCallDialog by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf("Connecting to room...") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var permissionsGranted by remember { mutableStateOf(false) }
    var useWebRtcRoom by remember { mutableStateOf(true) }

    LaunchedEffect(useWebRtcRoom) {
        if (useWebRtcRoom) {
            try {
                engine?.stopPreview()
                engine?.leaveChannel()
            } catch (e: Exception) {
                Log.w(TAG, "Error pausing Agora engine for WebRTC", e)
            }
        }
    }

    // Required permissions for camera and microphone
    val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.all { it.value }
    }

    LaunchedEffect(Unit) {
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            permissionsGranted = true
        } else {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    // Initialize and join Agora RTC Engine only if user explicitly toggles away from free WebRTC
    DisposableEffect(permissionsGranted, useWebRtcRoom) {
        if (!permissionsGranted || useWebRtcRoom) return@DisposableEffect onDispose { }

        var rtcEngine: RtcEngine? = null

        val eventHandler = object : IRtcEngineEventHandler() {
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                Log.i(TAG, "Joined channel $channel with uid $uid in ${elapsed}ms")
                isJoined = true
                errorMessage = null
                connectionStatus = "In Room · Waiting for ${if (isDoctor) "patient" else "doctor"}..."
            }

            override fun onUserJoined(uid: Int, elapsed: Int) {
                Log.i(TAG, "Remote user joined: $uid")
                remoteUid = uid
                isSimulatedRemote = false
                connectionStatus = "Connected"
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                Log.i(TAG, "Remote user went offline: $uid, reason=$reason")
                if (uid == remoteUid) {
                    remoteUid = -1
                    connectionStatus = "${if (isDoctor) "Patient" else "Doctor"} disconnected"
                }
            }

            override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
                Log.i(TAG, "First remote video decoded for uid $uid ($width x $height)")
                remoteUid = uid
                isSimulatedRemote = false
                connectionStatus = "Connected"
            }

            override fun onConnectionStateChanged(state: Int, reason: Int) {
                Log.i(TAG, "Connection state changed: state=$state, reason=$reason")
                when (state) {
                    Constants.CONNECTION_STATE_CONNECTING -> connectionStatus = "Connecting..."
                    Constants.CONNECTION_STATE_CONNECTED -> {
                        connectionStatus = if (remoteUid != -1 || isSimulatedRemote) "Connected"
                        else "In Room · Waiting for ${if (isDoctor) "patient" else "doctor"}..."
                    }
                    Constants.CONNECTION_STATE_RECONNECTING -> connectionStatus = "Reconnecting..."
                    Constants.CONNECTION_STATE_FAILED -> {
                        connectionStatus = "Connection failed"
                        if (reason == Constants.CONNECTION_CHANGED_INVALID_TOKEN ||
                            reason == Constants.CONNECTION_CHANGED_TOKEN_EXPIRED
                        ) {
                            errorMessage = "Agora Token required. Switch project to 'Testing mode: APP ID' in console.agora.io or use Test Simulation."
                        }
                    }
                }
            }

            override fun onError(err: Int) {
                Log.e(TAG, "Agora RTC Error: $err")
                when (err) {
                    Constants.ERR_TOKEN_EXPIRED, Constants.ERR_INVALID_TOKEN -> {
                        errorMessage = "Agora Token rejected by server ($err). You can tap 'Enter Consultation Room' below to proceed directly, or disable Primary Certificate in Agora Console for tokenless mode."
                        connectionStatus = "Authentication note ($err)"
                    }
                    Constants.ERR_INVALID_APP_ID -> {
                        errorMessage = "Invalid Agora App ID ($err). Check AgoraConfig.APP_ID."
                        connectionStatus = "Invalid App ID"
                    }
                    Constants.ERR_INVALID_CHANNEL_NAME -> {
                        errorMessage = "Invalid channel name ($err)."
                        connectionStatus = "Invalid channel name"
                    }
                    else -> {
                        if (err != 0) {
                            connectionStatus = "Notice: Code $err"
                        }
                    }
                }
            }

            override fun onTokenPrivilegeWillExpire(token: String?) {
                Log.w(TAG, "Agora token will expire, renewing dynamic token...")
                val freshToken = AgoraConfig.generateRtcToken(channelName, localUid)
                if (freshToken.isNotBlank()) {
                    rtcEngine?.renewToken(freshToken)
                }
            }
        }

        try {
            val config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = AgoraConfig.APP_ID
                mEventHandler = eventHandler
                mChannelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            }
            rtcEngine = RtcEngine.create(config)

            // Setup Video & Audio
            rtcEngine.enableVideo()
            rtcEngine.enableAudio()
            rtcEngine.setEnableSpeakerphone(true)
            rtcEngine.setDefaultAudioRoutetoSpeakerphone(true)

            rtcEngine.setVideoEncoderConfiguration(
                VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
                )
            )

            // Start hardware preview
            rtcEngine.startPreview()

            // Channel Media Options (Essential for Agora 4.x track publication)
            val options = ChannelMediaOptions().apply {
                channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
                clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
                autoSubscribeAudio = true
                autoSubscribeVideo = true
                publishCameraTrack = true
                publishMicrophoneTrack = true
            }

            val rtcToken = AgoraConfig.generateRtcToken(channelName, localUid)
            val joinResult = rtcEngine.joinChannel(rtcToken.ifBlank { null }, channelName, localUid, options)
            Log.i(TAG, "rtcEngine.joinChannel returned code $joinResult for channel $channelName with uid $localUid")

            engine = rtcEngine
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Agora RTC Engine", e)
            errorMessage = e.message ?: "Failed to initialize video engine"
            connectionStatus = "Initialization failed"
        }

        onDispose {
            try {
                rtcEngine?.stopPreview()
                rtcEngine?.leaveChannel()
                RtcEngine.destroy()
            } catch (e: Exception) {
                Log.w(TAG, "Error cleaning up Agora engine", e)
            }
            engine = null
        }
    }

    // Call timer runs when connected (either via live Agora peer or test simulation)
    val isCallActive = remoteUid != -1 || isSimulatedRemote
    LaunchedEffect(isCallActive) {
        if (isCallActive) {
            while (true) {
                delay(1000)
                callDurationSeconds++
            }
        }
    }

    val timerFormatted = remember(callDurationSeconds) {
        val mins = (callDurationSeconds / 60).toString().padStart(2, '0')
        val secs = (callDurationSeconds % 60).toString().padStart(2, '0')
        "$mins:$secs"
    }

    // Permission Denied Screen
    if (!permissionsGranted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AvenDeep),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                color = AvenWhite,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(AvenMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = AvenTeal,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Camera & Microphone Access",
                        color = AvenInk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Payslot requires camera and microphone permissions for telehealth video consultations.",
                        color = AvenMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { permissionLauncher.launch(requiredPermissions) },
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Grant Permissions", color = AvenWhite, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onCallEnded) {
                        Text("Return to Visits", color = AvenMuted)
                    }
                }
            }
        }
        return
    }

    // Main Video Call Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07191B)) // Deep dark teal/slate video backdrop
    ) {
        if (useWebRtcRoom) {
            // Free WebRTC Room (Jitsi Meet Powered via Open Community Node - 100% Free, Zero Login, Zero Moderator Barrier)
            val webRtcUrl = remember(channelName, localUserName) {
                val encodedUser = try { URLEncoder.encode(localUserName, "UTF-8") } catch (e: Exception) { localUserName }
                "https://meet.ffmuc.net/$channelName#userInfo.displayName=\"$encodedUser\"&config.disableDeepLinking=true&config.prejoinPageEnabled=false&config.startWithAudioMuted=false&config.startWithVideoMuted=false"
            }

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.allowFileAccess = true
                        settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
                        webChromeClient = object : WebChromeClient() {
                            override fun onPermissionRequest(request: PermissionRequest) {
                                request.grant(request.resources)
                            }
                        }
                        loadUrl(webRtcUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Top Status Bar Overlay with "Open in Chrome" & "End Call"
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AvenDeep.copy(alpha = 0.90f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AvenLime)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Free WebRTC Live · $remoteUserName",
                            color = AvenWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Encrypted Peer Room: $channelName",
                            color = AvenMuted,
                            fontSize = 10.sp
                        )
                    }
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webRtcUrl))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Chrome", tint = AvenLime)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = onCallEnded,
                        colors = ButtonDefaults.buttonColors(containerColor = AvenRed),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Exit", color = AvenWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
        // ================= 1. REMOTE VIDEO FEED =================
        if (remoteUid != -1 && engine != null) {
            // Live Agora Remote Peer Video
            AndroidView(
                factory = { ctx ->
                    TextureView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        engine?.setupRemoteVideo(VideoCanvas(this, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (isSimulatedRemote) {
            // Simulated Connected State (Clinical Demo / Single Device Test)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AvenDeep),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(AvenMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = remoteUserName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            color = AvenTeal,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = remoteUserName,
                        color = AvenWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDoctor) "Patient Connected · Video & Audio Active" else "Dr. $remoteUserName · Live Consultation",
                        color = AvenLime,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Waiting Room / Connecting State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(AvenMint.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AvenLime,
                        modifier = Modifier.size(52.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = remoteUserName,
                    color = AvenWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = connectionStatus,
                    color = AvenLime,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))
                CircularProgressIndicator(
                    color = AvenLime,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )

                // Error message banner if any
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        color = AvenWhite.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = AvenLime, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Notice", color = AvenLime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(errorMessage ?: "", color = AvenWhite.copy(alpha = 0.9f), fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }

                // 1-Tap Switch to 100% Free WebRTC Video Call (No Tokens / 100% Free)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        useWebRtcRoom = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenMint)
                ) {
                    Icon(Icons.Default.VideoCall, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch to Free WebRTC Video Call (No Tokens)", color = AvenTeal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Interactive local simulation button
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        isSimulatedRemote = true
                        connectionStatus = "Connected"
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLime.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AvenLime, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simulate Connected Room", color = AvenLime, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // ================= 2. LOCAL CAMERA PREVIEW (PiP Overlay) =================
        AnimatedVisibility(
            visible = !isCamOff && engine != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 64.dp, end = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .width(115.dp)
                    .height(155.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Black,
                shadowElevation = 10.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, AvenLime.copy(alpha = 0.7f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            TextureView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                engine?.setupLocalVideo(VideoCanvas(this, VideoCanvas.RENDER_MODE_HIDDEN, localUid))
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // "You" Tag
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = "You",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = AvenWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ================= 3. TOP STATUS BAR =================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status pill with live pulsing indicator & timer
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.55f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCallActive) Color(0xFF22C55E)
                                else if (isJoined) Color(0xFFF59E0B)
                                else Color(0xFFEF4444)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCallActive) timerFormatted else connectionStatus,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Top action buttons: Speakerphone & Camera Flip
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Speakerphone Toggle
                IconButton(
                    onClick = {
                        isSpeakerOn = !isSpeakerOn
                        engine?.setEnableSpeakerphone(isSpeakerOn)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        contentDescription = "Speakerphone",
                        tint = if (isSpeakerOn) AvenLime else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Flip Camera
                IconButton(
                    onClick = {
                        engine?.switchCamera()
                        isFrontCamera = !isFrontCamera
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Switch Camera",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Remote Participant Name Banner (when active)
        if (isCallActive) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 100.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.55f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AvenTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AvenWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = remoteUserName,
                            color = AvenWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isDoctor) "Patient · Secure Encrypted Stream" else "Doctor · HD Telehealth Visit",
                            color = AvenLime,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // ================= 4. BOTTOM CALL CONTROLS =================
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0C2426).copy(alpha = 0.95f),
            shadowElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Toggle
                IconButton(
                    onClick = {
                        isMicMuted = !isMicMuted
                        engine?.muteLocalAudioStream(isMicMuted)
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isMicMuted) AvenRed else Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Toggle Mic",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Camera Toggle
                IconButton(
                    onClick = {
                        isCamOff = !isCamOff
                        engine?.muteLocalVideoStream(isCamOff)
                        if (isCamOff) engine?.stopPreview() else engine?.startPreview()
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isCamOff) AvenRed else Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = if (isCamOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Toggle Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Flip Camera
                IconButton(
                    onClick = {
                        engine?.switchCamera()
                        isFrontCamera = !isFrontCamera
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraFront,
                        contentDescription = "Flip Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // End Call Button
                Button(
                    onClick = { showEndCallDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenRed),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Default.CallEnd,
                        contentDescription = null,
                        tint = AvenWhite,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End",
                        color = AvenWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        }
    }

    // End Call Confirmation Dialog
    if (showEndCallDialog) {
        AlertDialog(
            onDismissRequest = { showEndCallDialog = false },
            containerColor = AvenWhite,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CallEnd, contentDescription = null, tint = AvenRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("End Consultation?", fontWeight = FontWeight.Bold, color = AvenInk)
                }
            },
            text = {
                Text(
                    "Are you sure you want to end this video visit with $remoteUserName?",
                    fontSize = 14.sp,
                    color = AvenMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEndCallDialog = false
                        try {
                            engine?.leaveChannel()
                        } catch (e: Exception) {
                            Log.w(TAG, "Error leaving channel", e)
                        }
                        onCallEnded()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("End Call", color = AvenWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndCallDialog = false }) {
                    Text("Continue Call", color = AvenTeal, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
