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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.patient.PatientViewModel
import com.docdirect.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AvenProfileScreen(
    patientViewModel: PatientViewModel,
    onTabSelected: (String) -> Unit,
    onNavigateToClinicianWorkspace: () -> Unit,
    onNavigateToOperations: () -> Unit,
    onSignOut: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val familyMembers by patientViewModel.familyMembers.collectAsState()
    val invoices by patientViewModel.invoices.collectAsState()
    val notifications by patientViewModel.notifications.collectAsState()

    var showFamilyModal by remember { mutableStateOf(false) }
    var showAddFamilyModal by remember { mutableStateOf(false) }
    var showInvoiceModal by remember { mutableStateOf(false) }
    var showDeviceCheckModal by remember { mutableStateOf(false) }
    var showNotificationsModal by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    var showSavedDoctorsModal by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var showSupportModal by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your space.",
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk,
                        letterSpacing = (-0.7).sp
                    )

                    IconButton(
                        onClick = { showNotificationsModal = true },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AvenWhite)
                            .border(1.dp, AvenLine, CircleShape)
                    ) {
                        BadgedBox(
                            badge = {
                                if (notifications.any { !it.isRead }) {
                                    Badge(containerColor = AvenRed)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = AvenInk, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        },
        bottomBar = {
            AvenBottomNav(
                selectedTab = "you",
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // User Identity Card (Exact profile.svg)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = AvenDeep,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AvenLime),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AM", color = AvenDeep, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text("Aarav Mehta", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("aarav@example.com", fontSize = 12.sp, color = Color(0xFFC2D8CD))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Edit profile →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AvenLime)
                        }
                    }
                }
            }

            // 5 Menu Cards (Exact profile.svg 76dp height cards)
            item {
                ProfileCardItem(
                    title = "Family profiles",
                    subtitle = "Book for the people you care for",
                    icon = Icons.Default.Groups,
                    onClick = { showFamilyModal = true }
                )
            }

            item {
                ProfileCardItem(
                    title = "Payments & invoices",
                    subtitle = "Saved methods and visit receipts",
                    icon = Icons.Default.CreditCard,
                    onClick = { showInvoiceModal = true }
                )
            }

            item {
                ProfileCardItem(
                    title = "Saved doctors",
                    subtitle = "Your care shortlist",
                    icon = Icons.Default.FavoriteBorder,
                    onClick = { showSavedDoctorsModal = true }
                )
            }

            item {
                ProfileCardItem(
                    title = "Settings & privacy",
                    subtitle = "Notifications, access and security",
                    icon = Icons.Default.Settings,
                    onClick = { showSettingsModal = true }
                )
            }

            item {
                ProfileCardItem(
                    title = "Help & support",
                    subtitle = "We’re here when you need us",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    onClick = { showSupportModal = true }
                )
            }

            item {
                ProfileCardItem(
                    title = "Device test (Video & Audio)",
                    subtitle = "Check camera and microphone before visits",
                    icon = Icons.Default.Videocam,
                    onClick = { showDeviceCheckModal = true }
                )
            }

            // Clinical & Operations Workspace Switchers
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Role & Workspaces", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AvenMint,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToClinicianWorkspace() }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Clinician Workspace", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenDeep)
                                Text("Schedule, patient workspace & digital prescriptions for Dr Mira Shah", fontSize = 12.sp, color = AvenMuted)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AvenLavender,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenBlue.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToOperations() }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AvenBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Operations & Admin Overview", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                                Text("Clinician verification queue, audit logs & refund processing", fontSize = 12.sp, color = AvenMuted)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AvenBlue, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Sign Out
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AvenRose,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenRed.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSignOutDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = AvenRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Log out of Aven", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenRed)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    // Family Profiles Modal
    if (showFamilyModal) {
        AlertDialog(
            onDismissRequest = { showFamilyModal = false },
            title = { Text("Family Profiles (${familyMembers.size})", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Book appointments on behalf of someone you care for:", fontSize = 13.sp, color = AvenMuted)

                    familyMembers.forEach { member ->
                        Surface(color = AvenMint, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(AvenTeal), contentAlignment = Alignment.Center) {
                                    Text(member.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""), color = AvenWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                    Text("${member.relationship} • ${member.age} years • ${member.gender}", fontSize = 12.sp, color = AvenMuted)
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            showFamilyModal = false
                            showAddFamilyModal = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add Family Member")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showFamilyModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Done")
                }
            }
        )
    }

    // Add Family Member Modal (Saves to DB!)
    if (showAddFamilyModal) {
        var memberName by remember { mutableStateOf("") }
        var memberRel by remember { mutableStateOf("Parent") }
        var memberAge by remember { mutableStateOf("56") }

        AlertDialog(
            onDismissRequest = { showAddFamilyModal = false },
            title = { Text("Add Family Member", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = memberRel,
                        onValueChange = { memberRel = it },
                        label = { Text("Relationship (e.g. Parent, Child, Spouse)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = memberAge,
                        onValueChange = { memberAge = it },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memberName.isNotBlank()) {
                            coroutineScope.launch {
                                patientViewModel.addFamilyMember(
                                    name = memberName,
                                    relationship = memberRel,
                                    age = memberAge.toIntOrNull() ?: 30,
                                    gender = "Unspecified",
                                    dob = "01 Jan 1990"
                                )
                                showAddFamilyModal = false
                                showFamilyModal = true
                            }
                        }
                    },
                    enabled = memberName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                ) {
                    Text("Save to DB")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFamilyModal = false }) {
                    Text("Cancel", color = AvenMuted)
                }
            }
        )
    }

    // Invoice Modal
    if (showInvoiceModal) {
        AlertDialog(
            onDismissRequest = { showInvoiceModal = false },
            title = { Text("Receipts & Invoices (${invoices.size})", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 350.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(invoices) { inv ->
                        Surface(
                            color = AvenWhite,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(inv.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                    Surface(color = AvenMint, shape = RoundedCornerShape(100.dp)) {
                                        Text(inv.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                                    }
                                }
                                Text("Doctor: ${inv.doctorName} (${inv.specialty})", fontSize = 12.sp, color = AvenMuted)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Paid: ₹${inv.totalAmount.toInt()} via ${inv.paymentMethod}", fontSize = 12.sp, color = AvenTeal, fontWeight = FontWeight.SemiBold)
                                    Text(inv.date, fontSize = 11.sp, color = AvenMuted)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showInvoiceModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Close")
                }
            }
        )
    }

    // Notifications Feed Modal
    if (showNotificationsModal) {
        AlertDialog(
            onDismissRequest = { showNotificationsModal = false },
            title = { Text("Notifications", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 350.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notifications) { notif ->
                        Surface(
                            color = if (notif.isRead) AvenWhite else AvenMint.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        patientViewModel.markNotificationRead(notif.id)
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                    Text(notif.timestamp, fontSize = 11.sp, color = AvenMuted)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(notif.subtitle, fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showNotificationsModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Done")
                }
            }
        )
    }

    // Device Check Modal
    if (showDeviceCheckModal) {
        AlertDialog(
            onDismissRequest = { showDeviceCheckModal = false },
            title = { Text("Device Readiness Check", fontWeight = FontWeight.Bold, color = AvenInk) },
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
                        Text("Microphone ready (Echo cancellation on)", fontSize = 13.sp, color = AvenInk)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = null, tint = AvenTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Network latency: 34ms (High-definition ready)", fontSize = 13.sp, color = AvenInk)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDeviceCheckModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Close")
                }
            }
        )
    }

    // Saved Doctors Modal
    if (showSavedDoctorsModal) {
        AlertDialog(
            onDismissRequest = { showSavedDoctorsModal = false },
            title = { Text("Saved Doctors", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = RoundedCornerShape(14.dp), color = AvenMint, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AvenTeal), contentAlignment = Alignment.Center) {
                                Text("MS", color = AvenWhite, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Dr Mira Shah", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                Text("Dermatologist • Bandra Skin Clinic • 4.9 ★", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }

                    Surface(shape = RoundedCornerShape(14.dp), color = AvenWhite, border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AvenSoft), contentAlignment = Alignment.Center) {
                                Text("KR", color = AvenInk, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Dr Kabir Rao", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                Text("Cardiologist • Lilavati Hospital • 4.8 ★", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSavedDoctorsModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Done")
                }
            }
        )
    }

    // Settings Modal
    if (showSettingsModal) {
        AlertDialog(
            onDismissRequest = { showSettingsModal = false },
            title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• In-app notifications: Enabled", fontSize = 13.sp, color = AvenInk)
                    Text("• SMS appointment reminders: Active (+91 98765 43210)", fontSize = 13.sp, color = AvenInk)
                    Text("• Record encryption: AES-256 enabled locally", fontSize = 13.sp, color = AvenTeal)
                    Text("• Two-factor authentication: Active", fontSize = 13.sp, color = AvenInk)
                }
            },
            confirmButton = {
                Button(onClick = { showSettingsModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Close")
                }
            }
        )
    }

    // Support Modal
    if (showSupportModal) {
        AlertDialog(
            onDismissRequest = { showSupportModal = false },
            title = { Text("Help & Support", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("We're here when you need us, 24/7 across India.", fontSize = 13.sp, color = AvenMuted)
                    Surface(shape = RoundedCornerShape(12.dp), color = AvenMint, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AvenTeal)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Toll-free Care Desk: 1800-209-AVEN", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenDeep)
                        }
                    }
                    Text("Email: care@avenhealth.in • Mumbai, MH", fontSize = 12.sp, color = AvenMuted)
                }
            },
            confirmButton = {
                Button(onClick = { showSupportModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Got it")
                }
            }
        )
    }

    // Sign out dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Log out of Aven?", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = { Text("You can sign back in whenever you need. Your appointment history and records remain securely saved.", fontSize = 13.sp, color = AvenMuted) },
            confirmButton = {
                Button(onClick = {
                    showSignOutDialog = false
                    onSignOut()
                }, colors = ButtonDefaults.buttonColors(containerColor = AvenRed)) {
                    Text("Log out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = AvenInk)
                }
            }
        )
    }
}

@Composable
fun ProfileCardItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .clickable { onClick() },
        color = AvenWhite,
        shape = RoundedCornerShape(17.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(AvenMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = AvenMuted)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(20.dp))
        }
    }
}
