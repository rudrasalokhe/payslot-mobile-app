package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.AppointmentStatus
import com.docdirect.app.ui.doctor.DoctorViewModel
import com.docdirect.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AvenClinicianWorkspaceScreen(
    doctorViewModel: DoctorViewModel,
    onNavigateBack: () -> Unit,
    onStartVideoCall: (appointmentId: String, patientName: String) -> Unit,
    onOpenChat: (appointmentId: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val doctor by doctorViewModel.doctorProfile.collectAsState()
    val appointments by doctorViewModel.appointments.collectAsState()

    var isAvailable by remember { mutableStateOf(true) }
    var prescriptionText by remember { mutableStateOf("Tretinoin 0.025% cream - Apply thin layer at night. Ceramide moisturizer twice daily.") }
    var clinicalNotes by remember { mutableStateOf("Patient presents with mild erythema on cheek area. Skin barrier compromise secondary to over-exfoliation. Advised gentle barrier-repair protocol.") }
    var showSuccessToast by remember { mutableStateOf(false) }

    val activeAppointment = appointments.firstOrNull { it.status == AppointmentStatus.UPCOMING }
        ?: Appointment(
            id = "apt_aven_0925",
            patientId = "user_aarav_mehta",
            patientName = "Aarav Mehta",
            doctorId = "doc_mira_shah",
            doctorName = "Dr Mira Shah",
            doctorSpecialty = "Dermatologist",
            appointmentDate = "25 Sep 2026",
            appointmentTime = "3:30 PM IST",
            symptoms = "Follow-up for a skin concern. Discussing rash progression and barrier restoration.",
            feePaid = 1249.0,
            status = AppointmentStatus.UPCOMING
        )

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AvenWhite)
                            .border(1.dp, AvenLine, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AvenInk, modifier = Modifier.size(18.dp))
                    }

                    Text(
                        text = "Clinician Workspace",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )

                    Surface(
                        color = if (isAvailable) AvenMint else AvenRose,
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier.clickable { isAvailable = !isAvailable }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isAvailable) AvenTeal else AvenRed))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAvailable) "ONLINE" else "BUSY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) AvenTeal else AvenRed
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Welcome Dr Shah
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = AvenDeep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(AvenLime),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("MS", color = AvenDeep, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Dr Mira Shah", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                            Text("Dermatologist • Bandra Skin Clinic", fontSize = 13.sp, color = AvenLime)
                            Text("MCI License: 48921-DERM • Verified", fontSize = 11.sp, color = AvenWhite.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // Next Patient Up for Consultation
            item {
                Text("Next Scheduled Consultation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(AvenMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("AM", color = AvenTeal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(activeAppointment.patientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                    Text("${activeAppointment.appointmentDate} at ${activeAppointment.appointmentTime}", fontSize = 12.sp, color = AvenMuted)
                                }
                            }
                            AvenStatusBadge(status = activeAppointment.status)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(color = AvenSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Reported Concerns:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenMuted)
                                Text(activeAppointment.symptoms, fontSize = 13.sp, color = AvenInk)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("1 Document shared: Blood-test-report.pdf", fontSize = 12.sp, color = AvenTeal, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Live Call Action
                        Button(
                            onClick = { onStartVideoCall(activeAppointment.id, activeAppointment.patientName) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(20.dp), tint = AvenWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Video Consultation", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { onOpenChat(activeAppointment.id) },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvenInk)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chat with Patient", color = AvenInk, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Clinical Notes & Prescription Pad
            item {
                Text("Clinical Workspace & Notes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Draft Consultation Notes (Private until signed):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        OutlinedTextField(
                            value = clinicalNotes,
                            onValueChange = { clinicalNotes = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text("Digital Prescription & Instructions:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        OutlinedTextField(
                            value = prescriptionText,
                            onValueChange = { prescriptionText = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    doctorViewModel.issuePrescription(activeAppointment.id, prescriptionText)
                                    showSuccessToast = true
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign & Issue Digital Prescription", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        if (showSuccessToast) {
                            Surface(color = AvenMint, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Prescription digitally signed & delivered to Aarav Mehta's account.",
                                    modifier = Modifier.padding(10.dp),
                                    fontSize = 12.sp,
                                    color = AvenTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
