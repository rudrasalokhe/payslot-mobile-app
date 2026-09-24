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
                    Column {
                        Text(
                            text = "You",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "Account settings, family profiles & preferences.",
                            fontSize = 13.sp,
                            color = AvenMuted
                        )
                    }

                    IconButton(
                        onClick = { showNotificationsModal = true },
                        modifier = Modifier
                            .size(40.dp)
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
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = AvenInk, modifier = Modifier.size(18.dp))
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // User Identity Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(AvenDeep),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AM", color = AvenLime, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text("Aarav Mehta", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            Text("+91 98765 43210 • Born 18 Mar 1998", fontSize = 12.sp, color = AvenMuted)
                            Text("aarav@example.com", fontSize = 12.sp, color = AvenTeal)
                        }
                    }
                }
            }

            // Clinical & Operations Workspace Switchers
            item {
                Text("Role & Workspaces", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))
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

            // Quick Preferences Section (Backed by SQLite DB)
            item {
                Text("Care Settings & Records", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column {
                        ProfileMenuRow(
                            title = "Family profiles",
                            subtitle = "${familyMembers.size} member(s) registered in database",
                            icon = Icons.Default.Groups,
                            onClick = { showFamilyModal = true }
                        )
                        HorizontalDivider(color = AvenLine.copy(alpha = 0.6f))
                        ProfileMenuRow(
                            title = "Payments & receipts",
                            subtitle = "${invoices.size} invoice(s) • Total ₹${invoices.sumOf { it.totalAmount }.toInt()}",
                            icon = Icons.Default.ReceiptLong,
                            onClick = { showInvoiceModal = true }
                        )
                        HorizontalDivider(color = AvenLine.copy(alpha = 0.6f))
                        ProfileMenuRow(
                            title = "Device test (Video & Audio)",
                            subtitle = "Check camera and microphone before visits",
                            icon = Icons.Default.Videocam,
                            onClick = { showDeviceCheckModal = true }
                        )
                        HorizontalDivider(color = AvenLine.copy(alpha = 0.6f))
                        ProfileMenuRow(
                            title = "Active sessions",
                            subtitle = "This phone (Android) • Chrome on Windows",
                            icon = Icons.Default.Devices,
                            onClick = {}
                        )
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
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AvenSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AvenInk)
            Text(subtitle, fontSize = 12.sp, color = AvenMuted)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AvenLine, modifier = Modifier.size(16.dp))
    }
}
