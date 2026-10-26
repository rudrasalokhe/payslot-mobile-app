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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.LazyRow
import com.docdirect.app.data.local.entity.NotificationEntity
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
    val coroutineScope = rememberCoroutineScope()
    val doctors by patientViewModel.doctors.collectAsState()
    val appointments by patientViewModel.appointments.collectAsState()
    val notifications by patientViewModel.notifications.collectAsState(initial = emptyList())
    val unreadCount = remember(notifications) { notifications.count { !it.isRead } }
    var showNotificationHub by remember { mutableStateOf(false) }

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

                    // Enhanced Notification Center Hub Bell with Live Unread Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AvenWhite)
                            .border(1.dp, AvenLine, CircleShape)
                            .clickable { showNotificationHub = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification Center Hub",
                            tint = AvenInk,
                            modifier = Modifier.size(20.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                                    .clip(CircleShape)
                                    .background(AvenRed)
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Paid ₹${nextAppointment.feePaid.toInt()} • Confirmed",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF22C55E)
                                            )
                                        }
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

    if (showNotificationHub) {
        AvenNotificationCenterDialog(
            notifications = notifications,
            onDismiss = { showNotificationHub = false },
            onMarkRead = { id ->
                coroutineScope.launch {
                    patientViewModel.markNotificationRead(id)
                }
            },
            onMarkAllRead = {
                coroutineScope.launch {
                    patientViewModel.markAllNotificationsRead()
                }
            },
            onNavigateAction = { deepLink ->
                showNotificationHub = false
                when {
                    deepLink.contains("visit") -> onTabSelected("visits")
                    deepLink.contains("record") -> onTabSelected("records")
                    deepLink.contains("profile") || deepLink.contains("invoice") || deepLink.contains("bill") -> onTabSelected("you")
                    deepLink.startsWith("chat/") -> {
                        val aptId = deepLink.substringAfter("chat/")
                        onOpenChat(aptId)
                    }
                    deepLink.startsWith("video_call/") -> {
                        val parts = deepLink.substringAfter("video_call/").split("/")
                        val aptId = parts.firstOrNull() ?: ""
                        val docName = parts.getOrNull(1)?.substringBefore("?") ?: "Doctor"
                        onStartVideoCall(aptId, docName)
                    }
                    else -> onTabSelected("visits")
                }
            }
        )
    }
}

@Composable
fun AvenNotificationCenterDialog(
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onNavigateAction: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filterTabs = listOf("All", "Alerts", "Appointments", "Receipts")

    val filteredList = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            "Alerts" -> notifications.filter { 
                it.title.contains("call", ignoreCase = true) || 
                it.title.contains("remind", ignoreCase = true) ||
                it.title.contains("message", ignoreCase = true) ||
                it.subtitle.contains("minute", ignoreCase = true) ||
                it.subtitle.contains("chat", ignoreCase = true)
            }
            "Appointments" -> notifications.filter {
                it.title.contains("appointment", ignoreCase = true) ||
                it.title.contains("booking", ignoreCase = true) ||
                it.subtitle.contains("consultation", ignoreCase = true) ||
                it.subtitle.contains("visit", ignoreCase = true)
            }
            "Receipts" -> notifications.filter {
                it.title.contains("receipt", ignoreCase = true) ||
                it.title.contains("invoice", ignoreCase = true) ||
                it.subtitle.contains("paid", ignoreCase = true) ||
                it.subtitle.contains("₹", ignoreCase = true) ||
                it.subtitle.contains("INR", ignoreCase = true) ||
                it.subtitle.contains("INV-", ignoreCase = true)
            }
            else -> notifications
        }
    }

    val unreadTotal = notifications.count { !it.isRead }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = AvenWhite,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AvenMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Notification Center", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                if (unreadTotal > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = AvenRed,
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = "$unreadTotal new",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text("Real-time updates, visits & billing receipts", fontSize = 11.sp, color = AvenMuted)
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AvenSoft)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AvenInk, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions bar & filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filterTabs) { tab ->
                            val isSelected = tab == selectedFilter
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) AvenTeal else AvenSoft,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenTeal else AvenLine),
                                modifier = Modifier.clickable { selectedFilter = tab }
                            ) {
                                Text(
                                    text = tab,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AvenWhite else AvenInk,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    if (unreadTotal > 0) {
                        TextButton(
                            onClick = onMarkAllRead,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark All Read", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = AvenLine)
                Spacer(modifier = Modifier.height(8.dp))

                // Notification list
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No notifications under '$selectedFilter'", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            Text("You're all caught up with your schedule & care.", fontSize = 12.sp, color = AvenMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList) { notif ->
                            val isCallAlert = notif.title.contains("call", ignoreCase = true) || notif.title.contains("visit", ignoreCase = true) || notif.title.contains("remind", ignoreCase = true)
                            val isReceipt = notif.title.contains("receipt", ignoreCase = true) || notif.subtitle.contains("₹") || notif.subtitle.contains("INV-")
                            val isRx = notif.title.contains("prescription", ignoreCase = true) || notif.subtitle.contains("Rx") || notif.subtitle.contains("report", ignoreCase = true)
                            val isChat = notif.title.contains("message", ignoreCase = true) || notif.deepLink.startsWith("chat/")

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (notif.isRead) AvenWhite else AvenSoft,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (notif.isRead) AvenLine else AvenTeal.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onMarkRead(notif.id)
                                        if (notif.deepLink.isNotBlank()) {
                                            onNavigateAction(notif.deepLink)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    val iconBg = when {
                                        isReceipt -> Color(0xFFE8F5E9)
                                        isCallAlert -> Color(0xFFFFF3E0)
                                        isRx -> Color(0xFFE0F2F1)
                                        isChat -> Color(0xFFEDE7F6)
                                        else -> AvenMint
                                    }
                                    val iconTint = when {
                                        isReceipt -> Color(0xFF2E7D32)
                                        isCallAlert -> Color(0xFFE65100)
                                        isRx -> Color(0xFF00796B)
                                        isChat -> Color(0xFF512DA8)
                                        else -> AvenTeal
                                    }
                                    val iconVector = when {
                                        isReceipt -> Icons.Default.ReceiptLong
                                        isCallAlert -> Icons.Default.Videocam
                                        isRx -> Icons.Default.Description
                                        isChat -> Icons.Default.Chat
                                        else -> Icons.Default.CalendarToday
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(iconBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(iconVector, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = notif.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AvenInk,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (!notif.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(AvenTeal)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = notif.subtitle,
                                            fontSize = 12.sp,
                                            color = AvenInk.copy(alpha = 0.85f),
                                            lineHeight = 16.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = notif.timestamp,
                                                fontSize = 10.sp,
                                                color = AvenMuted
                                            )

                                            if (notif.deepLink.isNotBlank()) {
                                                Text(
                                                    text = "Tap to open →",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AvenTeal
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
