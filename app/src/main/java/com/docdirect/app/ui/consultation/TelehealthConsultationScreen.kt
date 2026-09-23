package com.docdirect.app.ui.consultation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelehealthConsultationScreen(
    doctorName: String = "Dr. Julian Vance, MD",
    specialty: String = "Longevity & Preventive Medicine",
    onNavigateBack: () -> Unit,
    onOpenChat: () -> Unit
) {
    var callSeconds by remember { mutableStateOf(868) } // 14:28 initial
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    val timerFormatted = remember(callSeconds) {
        val mins = (callSeconds / 60).toString().padStart(2, '0')
        val secs = (callSeconds % 60).toString().padStart(2, '0')
        "$mins:$secs"
    }

    var isMicMuted by remember { mutableStateOf(false) }
    var isCamOff by remember { mutableStateOf(false) }
    var isOrderConfirmed by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showEndCallDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val audioPulseHeight by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "audio"
    )

    Scaffold(
        containerColor = AuraBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Consultation Call",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AuraOnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AuraOnSurface)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AuraPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraSurface.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            // Floating Call Controls Hub
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(24.dp),
                color = AuraInverseSurface.copy(alpha = 0.96f),
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Mic
                    IconButton(
                        onClick = {
                            isMicMuted = !isMicMuted
                            toastMessage = if (isMicMuted) "Microphone muted" else "Microphone unmuted"
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isMicMuted) AuraError else AuraInverseOnSurface.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute Mic",
                            tint = if (isMicMuted) Color.White else AuraInverseOnSurface
                        )
                    }

                    // Toggle Camera
                    IconButton(
                        onClick = {
                            isCamOff = !isCamOff
                            toastMessage = if (isCamOff) "Camera turned off" else "Camera turned on"
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isCamOff) AuraError else AuraInverseOnSurface.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = if (isCamOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Toggle Video",
                            tint = if (isCamOff) Color.White else AuraInverseOnSurface
                        )
                    }

                    // Share Vault
                    IconButton(
                        onClick = {
                            toastMessage = "Sharing Lab Diagnostics with $doctorName"
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AuraInverseOnSurface.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ScreenShare,
                            contentDescription = "Share Records",
                            tint = AuraInverseOnSurface
                        )
                    }

                    // Chat Button with badge
                    Box {
                        IconButton(
                            onClick = onOpenChat,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(AuraInverseOnSurface.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat",
                                tint = AuraInverseOnSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(AuraTertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("1", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // End Consultation
                    Button(
                        onClick = { showEndCallDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraError),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Leave", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Physician Call Status Header Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(AuraSecondary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = doctorName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = AuraOnSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = AuraPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = specialty,
                                    fontSize = 12.sp,
                                    color = AuraOnSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = AuraSurfaceContainerHighest,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Wifi, contentDescription = null, tint = AuraSecondary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("HD 1080p", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AuraOnSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AuraPrimary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = timerFormatted,
                                    color = AuraPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Video Stream Canvas & PiP
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AuraInverseSurface)
                ) {
                    // Video simulation feed
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(AuraPrimary.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = AuraInversePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = doctorName,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Live Clinical Consultation Stream",
                            color = AuraInverseOnSurface.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }

                    // Encryption Pill (Top Left)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        color = AuraInverseSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AuraSecondaryFixed, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("HIPAA E2EE", color = AuraInverseOnSurface, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Doctor Audio Indicator (Bottom Left)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        color = AuraInverseSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = AuraSecondaryFixed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Row(
                                modifier = Modifier.height(14.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box(modifier = Modifier.width(3.dp).height(audioPulseHeight.dp).background(AuraSecondaryFixed, CircleShape))
                                Box(modifier = Modifier.width(3.dp).height((audioPulseHeight * 0.7f + 2).dp).background(AuraSecondaryFixed, CircleShape))
                                Box(modifier = Modifier.width(3.dp).height((audioPulseHeight * 1.1f).dp).background(AuraSecondaryFixed, CircleShape))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Speaking", color = AuraInverseOnSurface, fontSize = 11.sp)
                        }
                    }

                    // PiP Self View (Top Right)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(width = 80.dp, height = 110.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AuraSurfaceContainerHigh)
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCamOff) {
                            Icon(Icons.Default.VideocamOff, contentDescription = null, tint = AuraOutline, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(36.dp))
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp),
                            color = AuraInverseSurface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "You",
                                color = AuraInverseOnSurface,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Live Biometrics Telemetry HUD (Apple Health Streaming)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = AuraError, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LIVE BIOMETRICS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AuraOnSurfaceVariant, letterSpacing = 0.5.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraSecondary))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Apple HealthKit Syncing", fontSize = 11.sp, color = AuraOnSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Heart Rate
                            TelemetryCard(
                                modifier = Modifier.weight(1f),
                                label = "Resting HR",
                                value = "62",
                                unit = "bpm",
                                status = "Optimal"
                            )
                            // SpO2
                            TelemetryCard(
                                modifier = Modifier.weight(1f),
                                label = "Pulse Ox",
                                value = "99",
                                unit = "%",
                                status = "SpO2 Normal"
                            )
                            // Blood Pressure
                            TelemetryCard(
                                modifier = Modifier.weight(1f),
                                label = "Vascular BP",
                                value = "118/76",
                                unit = "",
                                status = "Sys/Dia Norm"
                            )
                        }
                    }
                }
            }

            // Shared Clinical Consultation Sheet & Care Plan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Shared Clinical Care Plan", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AuraOnSurface)
                            }
                            Surface(
                                color = AuraSecondaryContainer,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "LIVE UPDATED",
                                    color = AuraOnSecondaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Real-time Physician Note Entry
                        Surface(
                            color = AuraSurfaceContainerLow,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(AuraPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.EditNote, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Physician Directive", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AuraOnSurface)
                                        Text("Just now", fontSize = 11.sp, color = AuraOnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Dr. Vance added: Rosuvastatin 5mg daily + Quest Advanced Cardiovascular Lipid Panel scheduled for Oct 14.",
                                        fontSize = 13.sp,
                                        color = AuraOnSurface,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        // Associated Lab & Drug Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Prescription Dispatch: Walgreens #4820", fontSize = 12.sp, color = AuraOnSurfaceVariant)
                            Text("Ready to route", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AuraSecondary)
                        }

                        Button(
                            onClick = {
                                isOrderConfirmed = true
                                toastMessage = "Orders confirmed and dispatched to Quest Diagnostics & Walgreens"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOrderConfirmed) AuraSecondary else AuraPrimary
                            )
                        ) {
                            Icon(
                                imageVector = if (isOrderConfirmed) Icons.Default.Verified else Icons.Default.TaskAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isOrderConfirmed) "Order Confirmed & Dispatched" else "Confirm Lab Requisition & Pharmacy Dispatch",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showEndCallDialog) {
        AlertDialog(
            onDismissRequest = { showEndCallDialog = false },
            title = { Text("Leave Consultation?") },
            text = { Text("Are you sure you want to exit your video consultation with $doctorName? Clinical summary will be saved to your dashboard.") },
            confirmButton = {
                Button(
                    onClick = {
                        showEndCallDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraError)
                ) {
                    Text("End Call")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndCallDialog = false }) {
                    Text("Stay in Call")
                }
            }
        )
    }
}

@Composable
private fun TelemetryCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    status: String
) {
    Surface(
        modifier = modifier,
        color = AuraSurfaceContainerLow,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 11.sp, color = AuraOnSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AuraOnSurface)
                if (unit.isNotBlank()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(unit, fontSize = 11.sp, color = AuraOnSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(AuraSecondary))
                Spacer(modifier = Modifier.width(4.dp))
                Text(status, fontSize = 10.sp, color = AuraSecondary, fontWeight = FontWeight.Medium)
            }
        }
    }
}
