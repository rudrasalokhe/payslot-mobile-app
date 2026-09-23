package com.docdirect.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.docdirect.app.data.model.ChatMessage
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.ui.doctor.PrescriptionDialog
import com.docdirect.app.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineChatScreen(
    appointmentId: String,
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val appointment by viewModel.getAppointment(appointmentId).collectAsState()
    val messages by viewModel.getMessagesForAppointment(appointmentId).collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var messageText by remember { mutableStateOf("") }
    var showPrescriptionModal by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val senderName = if (currentRole == UserRole.DOCTOR) {
        appointment?.doctorName ?: "Doctor"
    } else {
        appointment?.patientName ?: "Patient"
    }
    val senderId = if (currentRole == UserRole.DOCTOR) appointment?.doctorId ?: "doc_1" else appointment?.patientId ?: "pat_1"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentRole == UserRole.DOCTOR) Icons.Default.Person else Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentRole == UserRole.DOCTOR) (appointment?.patientName ?: "Patient") else (appointment?.doctorName ?: "Doctor"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentRole == UserRole.DOCTOR) "Online Consultation" else appointment?.doctorSpecialty ?: "Medical Consultation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentRole == UserRole.DOCTOR && appointment != null) {
                        IconButton(onClick = { showPrescriptionModal = true }) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Write Prescription", tint = TealPrimary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Patient Symptoms Banner
            appointment?.let { apt ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Symptoms: ${apt.symptoms}",
                            style = MaterialTheme.typography.bodySmall,
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(messages) { msg ->
                    val isMe = msg.senderRole == currentRole
                    ChatMessageBubble(message = msg, isMe = isMe)
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Chat Input Bar
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type message or consultation notes...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(
                                    appointmentId = appointmentId,
                                    senderId = senderId,
                                    senderName = senderName,
                                    role = currentRole,
                                    text = messageText
                                )
                                messageText = ""
                            }
                        },
                        containerColor = TealPrimary,
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }

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

@Composable
fun ChatMessageBubble(message: ChatMessage, isMe: Boolean) {
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeString = remember(message.timestamp) { dateFormat.format(Date(message.timestamp)) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = "${message.senderName} • $timeString",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Surface(
            color = when {
                message.isPrescription -> Color(0xFFE0F2FE)
                isMe -> TealPrimary
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = when {
                message.isPrescription -> Color(0xFF0369A1)
                isMe -> Color.White
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            shadowElevation = if (message.isPrescription) 2.dp else 0.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (message.isPrescription) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFICIAL DIGITAL PRESCRIPTION",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = message.messageText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
