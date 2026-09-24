package com.docdirect.app.ui.consultation

import android.Manifest
import android.content.pm.PackageManager
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.docdirect.app.data.agora.AgoraConfig
import com.docdirect.app.ui.theme.*
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import kotlinx.coroutines.delay

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
    val localUid = remember { AgoraConfig.getUserUid(localUserId) }

    var engine by remember { mutableStateOf<RtcEngine?>(null) }
    var isJoined by remember { mutableStateOf(false) }
    var remoteUid by remember { mutableIntStateOf(-1) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isCamOff by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var callDurationSeconds by remember { mutableIntStateOf(0) }
    var showEndCallDialog by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf("Connecting...") }
    var permissionsGranted by remember { mutableStateOf(false) }

    // Local and remote SurfaceViews
    var localSurfaceView by remember { mutableStateOf<SurfaceView?>(null) }
    var remoteSurfaceView by remember { mutableStateOf<SurfaceView?>(null) }

    // Check permissions
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

    // Initialize Agora engine when permissions are granted
    DisposableEffect(permissionsGranted) {
        if (!permissionsGranted) return@DisposableEffect onDispose { }

        val eventHandler = object : IRtcEngineEventHandler() {
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                isJoined = true
                connectionStatus = "Waiting for ${if (isDoctor) "patient" else "doctor"}..."
            }

            override fun onUserJoined(uid: Int, elapsed: Int) {
                remoteUid = uid
                connectionStatus = "Connected"
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                if (uid == remoteUid) {
                    remoteUid = -1
                    connectionStatus = "${if (isDoctor) "Patient" else "Doctor"} disconnected"
                }
            }

            override fun onConnectionStateChanged(state: Int, reason: Int) {
                when (state) {
                    Constants.CONNECTION_STATE_CONNECTING -> connectionStatus = "Connecting..."
                    Constants.CONNECTION_STATE_CONNECTED -> connectionStatus = if (remoteUid != -1) "Connected" else "Waiting for ${if (isDoctor) "patient" else "doctor"}..."
                    Constants.CONNECTION_STATE_RECONNECTING -> connectionStatus = "Reconnecting..."
                    Constants.CONNECTION_STATE_FAILED -> connectionStatus = "Connection failed"
                }
            }
        }

        try {
            val config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = AgoraConfig.APP_ID
                mEventHandler = eventHandler
            }
            val rtcEngine = RtcEngine.create(config)

            // Configure video
            rtcEngine.enableVideo()
            rtcEngine.setVideoEncoderConfiguration(
                VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
                )
            )

            // Setup local video preview
            val localView = SurfaceView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            localSurfaceView = localView
            rtcEngine.setupLocalVideo(VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, localUid))
            rtcEngine.startPreview()

            // Join channel
            val options = ChannelMediaOptions().apply {
                channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
                clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
                autoSubscribeAudio = true
                autoSubscribeVideo = true
            }
            rtcEngine.joinChannel(AgoraConfig.TOKEN, channelName, localUid, options)

            engine = rtcEngine
        } catch (e: Exception) {
            connectionStatus = "Error: ${e.message}"
        }

        onDispose {
            engine?.stopPreview()
            engine?.leaveChannel()
            RtcEngine.destroy()
            engine = null
        }
    }

    // Setup remote video when remote user joins
    LaunchedEffect(remoteUid) {
        if (remoteUid != -1 && engine != null) {
            val remoteView = SurfaceView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            remoteSurfaceView = remoteView
            engine?.setupRemoteVideo(VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid))
        } else {
            remoteSurfaceView = null
        }
    }

    // Call timer
    LaunchedEffect(remoteUid) {
        if (remoteUid != -1) {
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

    // Permission denied screen
    if (!permissionsGranted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A1628)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.VideocamOff,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera & Microphone access required",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please grant permissions to start the video call",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(requiredPermissions) },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
                ) {
                    Text("Grant Permissions")
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onCallEnded) {
                    Text("Go Back", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
        return
    }

    // Main Video Call UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1628))
    ) {
        // Remote Video (Full Screen)
        if (remoteSurfaceView != null && remoteUid != -1) {
            AndroidView(
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { frameLayout ->
                    frameLayout.removeAllViews()
                    remoteSurfaceView?.let { view ->
                        (view.parent as? ViewGroup)?.removeView(view)
                        frameLayout.addView(view)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Waiting state
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(AuraPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AuraSecondaryContainer,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = remoteUserName,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = connectionStatus,
                    color = AuraSecondaryContainer,
                    fontSize = 15.sp
                )
                if (connectionStatus.contains("Waiting")) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = AuraSecondaryContainer,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
        }

        // Top Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connection indicator + Timer
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (remoteUid != -1) Color(0xFF22C55E)
                                else if (isJoined) Color(0xFFF59E0B)
                                else Color(0xFFEF4444)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (remoteUid != -1) timerFormatted else connectionStatus,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Flip camera
            IconButton(
                onClick = {
                    engine?.switchCamera()
                    isFrontCamera = !isFrontCamera
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Switch Camera",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Local Video (PiP - bottom right)
        AnimatedVisibility(
            visible = !isCamOff && localSurfaceView != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 60.dp, end = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, AuraSecondaryContainer.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            FrameLayout(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        update = { frameLayout ->
                            frameLayout.removeAllViews()
                            localSurfaceView?.let { view ->
                                (view.parent as? ViewGroup)?.removeView(view)
                                frameLayout.addView(view)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    // Name label
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "You",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Remote user name overlay (when connected)
        if (remoteUid != -1) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 110.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AuraPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = remoteUserName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isDoctor) "Patient" else "Doctor",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Bottom Call Controls
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1A2332).copy(alpha = 0.95f),
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute Mic
                IconButton(
                    onClick = {
                        isMicMuted = !isMicMuted
                        engine?.muteLocalAudioStream(isMicMuted)
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isMicMuted) AuraError else Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Toggle Mic",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Toggle Camera
                IconButton(
                    onClick = {
                        isCamOff = !isCamOff
                        engine?.muteLocalVideoStream(isCamOff)
                        if (isCamOff) engine?.stopPreview() else engine?.startPreview()
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isCamOff) AuraError else Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = if (isCamOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Toggle Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Switch Camera
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

                // End Call
                Button(
                    onClick = { showEndCallDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraError),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Default.CallEnd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // End Call confirmation dialog
    if (showEndCallDialog) {
        AlertDialog(
            onDismissRequest = { showEndCallDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CallEnd, contentDescription = null, tint = AuraError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("End Consultation Call?")
                }
            },
            text = {
                Text("Are you sure you want to end the video consultation with $remoteUserName?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEndCallDialog = false
                        engine?.leaveChannel()
                        onCallEnded()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraError)
                ) {
                    Text("End Call")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndCallDialog = false }) {
                    Text("Continue Call")
                }
            }
        )
    }
}
