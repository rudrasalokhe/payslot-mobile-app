package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // Top Header: Location + Notification Bell
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenSearch() }
                    ) {
                        Text(
                            text = "MUMBAI • BANDRA WEST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenMuted,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Location",
                            tint = AvenMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Notification Bell with unread badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AvenWhite)
                            .border(1.dp, AvenLine, CircleShape)
                            .clickable { onTabSelected("you") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = AvenInk,
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 10.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(AvenRed)
                        )
                    }
                }
            }

            // Welcome Text
            item {
                Column {
                    val greetingName = patientViewModel.currentUserPreferredName.ifBlank {
                        userName.split(" ").firstOrNull() ?: "there"
                    }
                    Text(
                        text = "Good morning, $greetingName",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "How can we help you feel better?",
                        fontSize = 15.sp,
                        color = AvenMuted
                    )
                }
            }

            // Search Bar Field
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenSearch() },
                    color = AvenWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = AvenMuted,
                            modifier = Modifier.size(22.dp)
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

            // Hero Banner: "Your next step to feeling better."
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onDoctorSelected("doc_mira_shah") },
                    color = AvenDeep,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 3.dp
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Surface(
                            color = AvenLime,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "CARE THAT FITS YOUR DAY",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenDeep,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Your next step",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenWhite
                                )
                                Text(
                                    text = "to feeling better.",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenWhite
                                )
                            }

                            // Circular lime arrow button
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(AvenLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = AvenDeep,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Specialist Grid (4 Columns matching artboard)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Find your specialist",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "See all",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenTeal,
                            modifier = Modifier.clickable { onTabSelected("explore") }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val items = listOf(
                            Triple("General", Icons.Default.MedicalServices, "General"),
                            Triple("Skin", Icons.Default.Face, "Skin"),
                            Triple("Dental", Icons.Default.CleanHands, "Dental"),
                            Triple("Mental", Icons.Default.Psychology, "Mental")
                        )

                        items.forEach { (label, icon, _) ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(87.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onTabSelected("explore") },
                                color = AvenWhite,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                                shadowElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = AvenTeal,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AvenInk
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // "Your next appointment" Hero Card with Agora Video Call
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Your next appointment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )

                    if (nextAppointment != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onTabSelected("visits") },
                            color = AvenWhite,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val initials = nextAppointment.doctorName
                                        .replace("Dr ", "")
                                        .split(" ")
                                        .filter { it.isNotBlank() }
                                        .mapNotNull { it.firstOrNull()?.uppercase() }
                                        .take(2)
                                        .joinToString("")
                                        .ifBlank { "DR" }

                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFD8E7D6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initials,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = AvenTeal
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = nextAppointment.doctorName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = AvenInk
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${nextAppointment.appointmentDate} • ${nextAppointment.appointmentTime} IST",
                                            fontSize = 13.sp,
                                            color = AvenMuted
                                        )
                                    }

                                    Surface(
                                        color = AvenMint,
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = "Video visit",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AvenTeal
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // One-tap start Agora video call
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onStartVideoCall(nextAppointment.id, nextAppointment.doctorName) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                                        modifier = Modifier.weight(1.3f).height(46.dp)
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp), tint = AvenWhite)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Join Video Call", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                                    }

                                    OutlinedButton(
                                        onClick = { onOpenChat(nextAppointment.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                                        modifier = Modifier.weight(1f).height(46.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvenInk)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Chat", fontSize = 13.sp, color = AvenInk)
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onOpenSearch() },
                            color = AvenWhite,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AvenMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EventAvailable,
                                        contentDescription = null,
                                        tint = AvenTeal,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Room for better care.",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AvenInk
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "You don't have any upcoming visits.",
                                        fontSize = 12.sp,
                                        color = AvenMuted
                                    )
                                }

                                Text(
                                    text = "Book now",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenTeal
                                )
                            }
                        }
                    }
                }
            }

            // Top Clinicians
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
                        text = "Filters",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenTeal,
                        modifier = Modifier.clickable { onTabSelected("explore") }
                    )
                }
            }

            items(doctors) { doc ->
                AvenDoctorCard(
                    doctor = doc,
                    onBookClick = { onBookDoctor(doc.id) },
                    onProfileClick = { onDoctorSelected(doc.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
