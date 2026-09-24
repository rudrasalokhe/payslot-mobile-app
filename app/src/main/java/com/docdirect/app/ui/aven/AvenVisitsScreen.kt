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
import androidx.compose.material.icons.automirrored.filled.Chat
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
    var viewingAppointmentDetail by remember { mutableStateOf<Appointment?>(null) }
    var managingAppointment by remember { mutableStateOf<Appointment?>(null) }
    var showRescheduleDialog by remember { mutableStateOf<Appointment?>(null) }
    var showCancelDialog by remember { mutableStateOf<Appointment?>(null) }
    var showRatingDialog by remember { mutableStateOf<Appointment?>(null) }
    var showDeviceCheckDialog by remember { mutableStateOf(false) }

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
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your appointments",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Your care, one visit at a time.",
                            fontSize = 13.sp,
                            color = AvenMuted
                        )
                    }
                    Button(
                        onClick = onBookNewVisit,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: Upcoming, Past, Cancelled
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("UPCOMING" to "Upcoming", "PAST" to "Past", "CANCELLED" to "Cancelled").forEach { (tabKey, tabLabel) ->
                        val isSelected = selectedTab == tabKey
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) AvenDeep else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenDeep else AvenLine),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tabKey }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tabLabel,
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
                .padding(horizontal = 24.dp),
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
                if (selectedTab == "UPCOMING") {
                    item {
                        Text(
                            text = "FRIDAY, 25 SEPTEMBER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenMuted,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                items(filteredList) { apt ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Doctor header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = apt.doctorName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenInk
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = apt.doctorSpecialty,
                                        fontSize = 13.sp,
                                        color = AvenMuted
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (apt.status == AppointmentStatus.UPCOMING) AvenMint else if (apt.status == AppointmentStatus.COMPLETED) AvenMint else AvenRose
                                ) {
                                    Text(
                                        text = if (apt.status == AppointmentStatus.UPCOMING) "Confirmed" else if (apt.status == AppointmentStatus.COMPLETED) "Completed" else "Cancelled",
                                        color = if (apt.status == AppointmentStatus.CANCELLED) AvenRed else AvenTeal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Video · ${apt.appointmentTime} IST",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AvenInk
                            )

                            if (apt.symptoms.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Reason: ${apt.symptoms}",
                                    fontSize = 12.sp,
                                    color = AvenMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action buttons
                            if (apt.status == AppointmentStatus.UPCOMING) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewingAppointmentDetail = apt },
                                        shape = RoundedCornerShape(14.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                                        modifier = Modifier.weight(1f).height(44.dp)
                                    ) {
                                        Text("View appointment", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                                    }

                                    Button(
                                        onClick = { onStartVideoCall(apt.id, apt.doctorName) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                                        modifier = Modifier.weight(1f).height(44.dp)
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp), tint = AvenWhite)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Join Video", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(
                                        onClick = { onOpenChat(apt.id) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp), tint = AvenMuted)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Chat", fontSize = 12.sp, color = AvenMuted)
                                    }

                                    TextButton(
                                        onClick = { managingAppointment = apt },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.MoreHoriz, contentDescription = null, modifier = Modifier.size(15.dp), tint = AvenMuted)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Manage", fontSize = 12.sp, color = AvenMuted)
                                    }
                                }
                            } else if (apt.status == AppointmentStatus.COMPLETED) {
                                Button(
                                    onClick = { showRatingDialog = apt },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AvenMint),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Rate consultation", color = AvenTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            } else if (apt.status == AppointmentStatus.CANCELLED) {
                                Surface(
                                    color = AvenRose,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AvenRed, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("₹1,249 refund requested • Processing to UPI", fontSize = 12.sp, color = AvenRed, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Reminder card for upcoming
                if (selectedTab == "UPCOMING") {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = AvenMint
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "We’ll remind you",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenDeep
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Join becomes available 10 minutes before your visit.",
                                    fontSize = 12.sp,
                                    color = AvenTeal
                                )
                            }
                        }
                    }

                    // Find another appointment card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onBookNewVisit() },
                            shape = RoundedCornerShape(18.dp),
                            color = AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Find another appointment",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenInk
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Explore doctors and specialties",
                                        fontSize = 12.sp,
                                        color = AvenMuted
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    // Appointment Details Dialog (Exact appointment-detail.svg)
    viewingAppointmentDetail?.let { apt ->
        AlertDialog(
            onDismissRequest = { viewingAppointmentDetail = null },
            title = null,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your next visit",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "${apt.appointmentDate} · ${apt.appointmentTime} IST",
                                fontSize = 13.sp,
                                color = AvenMuted
                            )
                        }
                        IconButton(
                            onClick = { viewingAppointmentDetail = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = AvenMuted)
                        }
                    }

                    // Doctor Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AvenMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(apt.doctorName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                Text("${apt.doctorSpecialty} · Video consultation", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }

                    // Status and Reference ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AvenMint
                        ) {
                            Text(
                                text = "Confirmed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = apt.transactionId.ifBlank { "AV-0925-1042" },
                            fontSize = 12.sp,
                            color = AvenMuted
                        )
                    }

                    Text("Before you join", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)

                    // Checklist cards
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AvenMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Visit details added", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AvenInk)
                                Text(if (apt.symptoms.isNotBlank()) "Reason: ${apt.symptoms}" else "Reason for visit is ready", fontSize = 11.sp, color = AvenMuted)
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AvenMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("2 records shared", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AvenInk)
                                Text("Manage access for this visit", fontSize = 11.sp, color = AvenMuted)
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AvenMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Payment complete", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AvenInk)
                                Text("₹${apt.feePaid.toInt()} · View invoice", fontSize = 11.sp, color = AvenMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Buttons
                    Button(
                        onClick = {
                            viewingAppointmentDetail = null
                            onStartVideoCall(apt.id, apt.doctorName)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(20.dp), tint = AvenWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Join Video Call", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }

                    OutlinedButton(
                        onClick = { showDeviceCheckDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Check your device", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewingAppointmentDetail = null
                                managingAppointment = apt
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Reschedule or cancel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenTeal)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    // Device check dialog
    if (showDeviceCheckDialog) {
        AlertDialog(
            onDismissRequest = { showDeviceCheckDialog = false },
            title = { Text("Device Check", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = AvenTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Camera ready (Front camera detected)", fontSize = 13.sp, color = AvenInk)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = AvenTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Microphone ready (Echo cancellation active)", fontSize = 13.sp, color = AvenInk)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = null, tint = AvenTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Network latency: 32ms (High-definition ready)", fontSize = 13.sp, color = AvenInk)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDeviceCheckDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Ready")
                }
            }
        )
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
