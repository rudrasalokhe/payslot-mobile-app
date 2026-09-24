package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.AppointmentStatus
import com.docdirect.app.data.model.DoctorProfile
import com.docdirect.app.ui.patient.PatientViewModel
import com.docdirect.app.ui.theme.*

@Composable
fun AvenHomeScreen(
    patientViewModel: PatientViewModel,
    userName: String = "Aarav Mehta",
    onTabSelected: (String) -> Unit,
    onDoctorSelected: (doctorId: String) -> Unit,
    onBookDoctor: (doctorId: String) -> Unit,
    onStartVideoCall: (appointmentId: String, doctorName: String) -> Unit,
    onOpenChat: (appointmentId: String) -> Unit,
    onOpenSearch: () -> Unit
) {
    val doctors by patientViewModel.doctors.collectAsState()
    val appointments by patientViewModel.appointments.collectAsState()

    val nextAppointment = appointments.firstOrNull { it.status == AppointmentStatus.UPCOMING }
        ?: Appointment(
            id = "apt_aven_0925",
            patientId = "user_aarav_mehta",
            patientName = userName,
            doctorId = "doc_mira_shah",
            doctorName = "Dr Mira Shah",
            doctorSpecialty = "Dermatologist",
            appointmentDate = "25 Sep 2026",
            appointmentTime = "3:30 PM IST",
            symptoms = "Follow-up for skin concern. Skin barrier restoration.",
            feePaid = 1249.0,
            status = AppointmentStatus.UPCOMING,
            transactionId = "AV-INV-0925-1042"
        )

    val specialties = listOf(
        "General Medicine" to Icons.Default.MedicalServices,
        "Dermatology" to Icons.Default.Face,
        "Dental Care" to Icons.Default.CleanHands,
        "Pediatrics" to Icons.Default.ChildCare,
        "Cardiology" to Icons.Default.Favorite,
        "Orthopedics" to Icons.Default.Accessibility
    )

    Scaffold(
        containerColor = AvenBg,
        bottomBar = {
            AvenBottomNav(
                selectedTab = "home",
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            // Brand Top Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(AvenLime)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AVEN",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Good morning, ${userName.split(" ").firstOrNull() ?: "Aarav"}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AvenMint)
                            .border(1.dp, AvenLine, CircleShape)
                            .clickable { onTabSelected("you") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AvenTeal
                        )
                    }
                }
            }

            // Search Bar
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenSearch() },
                    color = AvenWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = AvenMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Doctor, specialty, or clinic",
                            fontSize = 15.sp,
                            color = AvenMuted
                        )
                    }
                }
            }

            // Next Step Hero Card: "Your next step to feeling better"
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AvenDeep,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = AvenLime,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "YOUR NEXT STEP",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenDeep,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Text(
                                text = "${nextAppointment.appointmentDate} • ${nextAppointment.appointmentTime}",
                                fontSize = 12.sp,
                                color = AvenWhite.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = nextAppointment.doctorName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenWhite
                        )
                        Text(
                            text = "${nextAppointment.doctorSpecialty} • Bandra Skin Clinic",
                            fontSize = 14.sp,
                            color = AvenLime,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Video Call Action Hub (Agora integration)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onStartVideoCall(nextAppointment.id, nextAppointment.doctorName) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AvenLime,
                                    contentColor = AvenDeep
                                ),
                                modifier = Modifier.weight(1.3f).height(48.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp), tint = AvenDeep)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Join Video Call", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvenDeep)
                            }

                            OutlinedButton(
                                onClick = { onOpenChat(nextAppointment.id) },
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AvenWhite.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AvenWhite),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvenWhite)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chat", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenWhite)
                            }
                        }
                    }
                }
            }

            // Specialties Carousel
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Explore specialties",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "See all",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AvenTeal,
                            modifier = Modifier.clickable { onTabSelected("explore") }
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(specialties) { (name, icon) ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = AvenWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                                modifier = Modifier.clickable { onTabSelected("explore") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(AvenMint),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AvenInk
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // "Doctors for you" Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Doctors for you",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                    Text(
                        text = "Compare",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AvenTeal,
                        modifier = Modifier.clickable { onTabSelected("explore") }
                    )
                }
            }

            if (doctors.isEmpty()) {
                item {
                    CircularProgressIndicator(color = AvenTeal, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(doctors) { doc ->
                    AvenDoctorCard(
                        doctor = doc,
                        onBookClick = { onBookDoctor(doc.id) },
                        onProfileClick = { onDoctorSelected(doc.id) }
                    )
                }
            }

            // Brand Quote Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AvenMint,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "A little less waiting. A lot more living.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your doctor, your time. Transparent INR fees, verified Indian clinicians, and instant digital care.",
                            fontSize = 13.sp,
                            color = AvenMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
