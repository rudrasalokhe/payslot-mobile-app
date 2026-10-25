package com.docdirect.app.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.docdirect.app.data.model.ChatMessage
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.ui.doctor.PrescriptionDialog
import com.docdirect.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineChatScreen(
    appointmentId: String,
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    // 1. Connect real-time Firebase sync for this appointment conversation
    DisposableEffect(appointmentId) {
        viewModel.startChatSync(appointmentId)
        onDispose {
            viewModel.stopChatSync(appointmentId)
        }
    }

    val appointmentFlow = remember(appointmentId) { viewModel.getAppointment(appointmentId) }
    val appointment by appointmentFlow.collectAsState()

    val messagesFlow = remember(appointmentId) { viewModel.getMessagesForAppointment(appointmentId) }
    val messages by messagesFlow.collectAsState()

    val currentRole by viewModel.currentRole.collectAsState()

    val currentUserId = remember { viewModel.getCurrentUserId() }
    val currentUserName = remember { viewModel.getCurrentUserName() }
    val activeRole = currentRole

    var messageText by remember { mutableStateOf("") }
    var showPrescriptionModal by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    var isInitialScrollDone by remember(appointmentId) { mutableStateOf(false) }

    // Auto-scroll to newest message smoothly without initial flickers
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            val targetIndex = messages.size + 1
            if (!isInitialScrollDone) {
                listState.scrollToItem(targetIndex)
                isInitialScrollDone = true
            } else {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    val senderName = remember(currentUserName, activeRole, appointment) {
        currentUserName.ifBlank {
            if (activeRole == UserRole.DOCTOR) appointment?.doctorName ?: "Dr. Mira Shah"
            else appointment?.patientName ?: "Patient"
        }
    }

    val senderId = remember(currentUserId, activeRole, appointment) {
        currentUserId.ifBlank {
            if (activeRole == UserRole.DOCTOR) appointment?.doctorId ?: "doc_mira_shah"
            else appointment?.patientId ?: "user_patient"
        }
    }

    val peerDisplayName = remember(activeRole, appointment) {
        if (activeRole == UserRole.DOCTOR) {
            appointment?.patientName?.ifBlank { "Patient" } ?: "Patient"
        } else {
            appointment?.doctorName?.ifBlank { "Dr. Mira Shah" } ?: "Dr. Mira Shah"
        }
    }

    val peerSubtitle = remember(activeRole, appointment) {
        if (activeRole == UserRole.DOCTOR) {
            "Patient Consultation · Active"
        } else {
            appointment?.doctorSpecialty ?: "Dermatology Specialist"
        }
    }

    val peerInitials = remember(peerDisplayName) {
        peerDisplayName.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
            .ifBlank { "DR" }
    }

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Surface(
                color = AvenWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Surface(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(12.dp),
                        color = AvenBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = AvenInk,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Peer Avatar with Green Online Dot
                    Box(modifier = Modifier.size(42.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(AvenMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = peerInitials,
                                color = AvenTeal,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Live indicator
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                                .border(2.dp, AvenWhite, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Peer Name & Status
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = peerDisplayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = peerSubtitle,
                                fontSize = 11.sp,
                                color = AvenMuted,
                                maxLines = 1
                            )
                        }
                    }

                    // Doctor action: Write Prescription
                    if (activeRole == UserRole.DOCTOR) {
                        Surface(
                            onClick = { showPrescriptionModal = true },
                            shape = RoundedCornerShape(10.dp),
                            color = AvenMint,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = "Prescription", tint = AvenTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rx", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Consultation Info Banner (if symptoms available)
            if (appointment != null && appointment!!.symptoms.isNotBlank()) {
                Surface(
                    color = AvenMint.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MedicalInformation,
                            contentDescription = null,
                            tint = AvenTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chief Complaint: ${appointment!!.symptoms}",
                            fontSize = 12.sp,
                            color = AvenTeal,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2
                        )
                    }
                }
            }

            // Message History Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "top_spacer") { Spacer(modifier = Modifier.height(6.dp)) }

                item(key = "encryption_banner") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "End-to-End Encrypted Consultation",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenInk
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Messages are synchronized live between doctor and patient.",
                                    fontSize = 11.sp,
                                    color = AvenMuted
                                )
                            }
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    val isMe = (msg.senderId.isNotBlank() && msg.senderId == currentUserId) || (msg.senderRole == activeRole)
                    ChatMessageBubble(message = msg, isMe = isMe)
                }

                item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(10.dp)) }
            }

            // Quick Reply Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val suggestions = if (activeRole == UserRole.DOCTOR) {
                    listOf("How are your symptoms now?", "Please take medicines as prescribed.", "Schedule a follow-up in 3 days.")
                } else {
                    listOf("Hello Doctor 👋", "I am feeling better today.", "What time should I take the medicines?")
                }

                items(suggestions) { chip ->
                    Surface(
                        onClick = {
                            viewModel.sendMessage(
                                appointmentId = appointmentId,
                                senderId = senderId,
                                senderName = senderName,
                                role = activeRole,
                                text = chip
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Text(
                            text = chip,
                            fontSize = 12.sp,
                            color = AvenInk,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Chat Input Bar
            Surface(
                color = AvenWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type a consultation message...", fontSize = 14.sp, color = AvenMuted) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AvenBg,
                            unfocusedContainerColor = AvenBg,
                            focusedBorderColor = AvenTeal,
                            unfocusedBorderColor = AvenLine
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    Surface(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(
                                    appointmentId = appointmentId,
                                    senderId = senderId,
                                    senderName = senderName,
                                    role = activeRole,
                                    text = messageText.trim()
                                )
                                messageText = ""
                            }
                        },
                        shape = CircleShape,
                        color = if (messageText.isNotBlank()) AvenTeal else AvenTeal.copy(alpha = 0.4f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send",
                                tint = AvenWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Prescription Dialog
    if (showPrescriptionModal && appointment != null) {
        PrescriptionDialog(
            appointment = appointment!!,
            onDismiss = { showPrescriptionModal = false },
            onSubmit = { text ->
                viewModel.issuePrescription(appointmentId, text)
                showPrescriptionModal = false
            }
        )
    }
}

private val chatTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

@Composable
fun ChatMessageBubble(message: ChatMessage, isMe: Boolean) {
    val timeString = remember(message.timestamp) {
        synchronized(chatTimeFormatter) {
            chatTimeFormatter.format(Date(message.timestamp))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isMe) "You • $timeString" else "${message.senderName} • $timeString",
                fontSize = 11.sp,
                color = AvenMuted
            )
        }

        Surface(
            color = when {
                message.isPrescription -> AvenMint
                isMe -> AvenTeal
                else -> AvenWhite
            },
            contentColor = when {
                message.isPrescription -> AvenTeal
                isMe -> AvenWhite
                else -> AvenInk
            },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            border = if (!isMe && !message.isPrescription) androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
            else if (message.isPrescription) androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.3f))
            else null,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (message.isPrescription) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = AvenTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFICIAL DIGITAL PRESCRIPTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenTeal,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = message.messageText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
