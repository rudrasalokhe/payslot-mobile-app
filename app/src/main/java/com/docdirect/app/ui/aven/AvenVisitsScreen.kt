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
import kotlinx.coroutines.launch

@Composable
fun AvenVisitsScreen(
    patientViewModel: PatientViewModel,
    onTabSelected: (String) -> Unit,
    onStartVideoCall: (appointmentId: String, doctorName: String) -> Unit,
    onOpenChat: (appointmentId: String) -> Unit,
    onBookNewVisit: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val appointments by patientViewModel.appointments.collectAsState()

    var selectedTab by remember { mutableStateOf("UPCOMING") }
    var managingAppointment by remember { mutableStateOf<Appointment?>(null) }
    var showRescheduleDialog by remember { mutableStateOf<Appointment?>(null) }
    var showCancelDialog by remember { mutableStateOf<Appointment?>(null) }
    var showRatingDialog by remember { mutableStateOf<Appointment?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val filteredList = remember(appointments, selectedTab) {
        when (selectedTab) {
            "UPCOMING" -> appointments.filter { it.status == AppointmentStatus.UPCOMING }
            "PAST" -> appointments.filter { it.status == AppointmentStatus.COMPLETED }
            "CANCELLED" -> appointments.filter { it.status == AppointmentStatus.CANCELLED }
            else -> appointments
        }
    }

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
                    Text(
                        text = "Appointments",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                    Button(
                        onClick = onBookNewVisit,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book Visit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: Upcoming, Past, Cancelled
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("UPCOMING", "PAST", "CANCELLED").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AvenDeep else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenDeep else AvenLine),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tab }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AvenLime else AvenInk
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            AvenBottomNav(
                selectedTab = "visits",
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            if (filteredList.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AvenWhite,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Room for better care.",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (selectedTab == "UPCOMING") "You don't have any upcoming appointments scheduled." else "No ${selectedTab.lowercase()} appointments recorded.",
                                fontSize = 13.sp,
                                color = AvenMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onBookNewVisit,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                            ) {
                                Text("Find a doctor")
                            }
                        }
                    }
                }
            } else {
                items(filteredList) { apt ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = apt.doctorName,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenInk
                                    )
                                    Text(
                                        text = "${apt.doctorSpecialty} • ${apt.appointmentDate} at ${apt.appointmentTime}",
                                        fontSize = 12.sp,
                                        color = AvenMuted
                                    )
                                }
                                AvenStatusBadge(status = apt.status)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Symptoms / Reason
                            Surface(
                                color = AvenSoft,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Reason for consultation:", fontSize = 11.sp, color = AvenMuted, fontWeight = FontWeight.SemiBold)
                                    Text(apt.symptoms.ifBlank { "Routine check-up" }, fontSize = 13.sp, color = AvenInk)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Paid: ₹${apt.feePaid.toInt()} • Txn: ${apt.transactionId.ifBlank { "AV-INV-0925-1042" }}",
                                        fontSize = 11.sp,
                                        color = AvenMuted
                                    )
                                }
                            }

                            // Prescription status if present
                            if (apt.prescription.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = AvenMint,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Prescription: ${apt.prescription}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Actions
                            if (apt.status == AppointmentStatus.UPCOMING) {
                                // Live Agora Video Call Button
                                Button(
                                    onClick = { onStartVideoCall(apt.id, apt.doctorName) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(20.dp), tint = AvenWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Join Video Call", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onOpenChat(apt.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvenInk)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Chat", fontSize = 13.sp, color = AvenInk)
                                    }

                                    OutlinedButton(
                                        onClick = { managingAppointment = apt },
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.MoreHoriz, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvenInk)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Manage", fontSize = 13.sp, color = AvenInk)
                                    }
                                }
                            } else if (apt.status == AppointmentStatus.COMPLETED) {
                                Button(
                                    onClick = { showRatingDialog = apt },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AvenMint),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Rate consultation", color = AvenTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            } else if (apt.status == AppointmentStatus.CANCELLED) {
                                Surface(
                                    color = AvenRose,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AvenRed, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("₹1,249 refund requested • Processing to UPI", fontSize = 12.sp, color = AvenRed, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    // Manage Appointment Dialog (Reschedule or Cancel)
    managingAppointment?.let { apt ->
        AlertDialog(
            onDismissRequest = { managingAppointment = null },
            title = { Text("Manage Appointment", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Visit with ${apt.doctorName} on ${apt.appointmentDate} at ${apt.appointmentTime}.", fontSize = 13.sp, color = AvenMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            managingAppointment = null
                            showRescheduleDialog = apt
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reschedule to another time")
                    }

                    OutlinedButton(
                        onClick = {
                            managingAppointment = null
                            showCancelDialog = apt
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel visit & request refund", color = AvenRed)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { managingAppointment = null }) {
                    Text("Close", color = AvenMuted)
                }
            }
        )
    }

    // Reschedule Dialog (moves to 26 Sep 2026, 3:30 PM as per brief)
    showRescheduleDialog?.let { apt ->
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = null },
            title = { Text("Reschedule appointment", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column {
                    Text("Current time: ${apt.appointmentDate} at ${apt.appointmentTime}", fontSize = 13.sp, color = AvenMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select new time:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AvenMint,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = AvenTeal)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Saturday, 26 September • 3:30 PM IST", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            patientViewModel.rescheduleAppointment(apt.id, "26 Sep 2026", "3:30 PM IST")
                            showRescheduleDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                ) {
                    Text("Confirm new time")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = null }) {
                    Text("Cancel", color = AvenMuted)
                }
            }
        )
    }

    // Cancel Dialog (Requests ₹1,249 refund)
    showCancelDialog?.let { apt ->
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Cancel appointment?", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column {
                    Text("Are you sure you want to cancel your consultation with ${apt.doctorName}?", fontSize = 14.sp, color = AvenInk)
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = AvenMint,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Full refund of ₹${apt.feePaid.toInt()} will be credited back to your original payment method within 2–3 business days.",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 12.sp,
                            color = AvenTeal
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            patientViewModel.cancelAppointment(apt.id)
                            showCancelDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenRed)
                ) {
                    Text("Cancel & Refund")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) {
                    Text("Keep visit", color = AvenInk)
                }
            }
        )
    }

    // Rating Dialog
    showRatingDialog?.let { apt ->
        var rating by remember { mutableIntStateOf(5) }
        var reviewText by remember { mutableStateOf("I felt listened to and had time to ask my questions.") }

        AlertDialog(
            onDismissRequest = { showRatingDialog = null },
            title = { Text("How did it feel?", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column {
                    Text("Your feedback after your visit with ${apt.doctorName}.", fontSize = 13.sp, color = AvenMuted)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { rating = star }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= rating) AvenAmber else AvenLine,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            patientViewModel.addReview(
                                doctorId = apt.doctorId,
                                patientName = apt.patientName,
                                rating = rating,
                                text = reviewText
                            )
                            showRatingDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                ) {
                    Text("Submit feedback")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = null }) {
                    Text("Skip", color = AvenMuted)
                }
            }
        )
    }
}
