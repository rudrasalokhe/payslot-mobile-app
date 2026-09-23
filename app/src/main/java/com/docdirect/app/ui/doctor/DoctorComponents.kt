package com.docdirect.app.ui.doctor

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.AppointmentStatus
import com.docdirect.app.ui.theme.TealPrimary

@Composable
fun DoctorAppointmentCard(
    appointment: Appointment,
    onOpenChat: () -> Unit,
    onIssuePrescription: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = appointment.patientName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Slot: ${appointment.appointmentDate} @ ${appointment.appointmentTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = when (appointment.status) {
                        AppointmentStatus.UPCOMING -> Color(0xFF0284C7).copy(alpha = 0.15f)
                        AppointmentStatus.IN_PROGRESS -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                        AppointmentStatus.COMPLETED -> Color(0xFF10B981).copy(alpha = 0.15f)
                        AppointmentStatus.CANCELLED -> Color(0xFFEF4444).copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = appointment.status.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (appointment.status) {
                            AppointmentStatus.UPCOMING -> Color(0xFF0369A1)
                            AppointmentStatus.IN_PROGRESS -> Color(0xFFB45309)
                            AppointmentStatus.COMPLETED -> Color(0xFF047857)
                            AppointmentStatus.CANCELLED -> Color(0xFFB91C1C)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Reported Symptoms:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = appointment.symptoms,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (appointment.prescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prescription Issued",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat Online")
                }

                OutlinedButton(
                    onClick = onIssuePrescription,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Prescription")
                }
            }
        }
    }
}

@Composable
fun PrescriptionDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onSubmit: (prescriptionText: String) -> Unit
) {
    var text by remember { mutableStateOf(appointment.prescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = TealPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Issue Prescription for ${appointment.patientName}")
            }
        },
        text = {
            Column {
                Text(
                    text = "Write digital prescription details, dosage instructions, and medical advice:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("e.g. Amoxicillin 500mg - 1 capsule twice daily after meals for 7 days.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Issue & Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
