package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.docdirect.app.ui.theme.*

@Composable
fun AvenProfileScreen(
    onTabSelected: (String) -> Unit,
    onNavigateToClinicianWorkspace: () -> Unit,
    onNavigateToOperations: () -> Unit,
    onSignOut: () -> Unit
) {
    var showFamilyModal by remember { mutableStateOf(false) }
    var showInvoiceModal by remember { mutableStateOf(false) }
    var showDeviceCheckModal by remember { mutableStateOf(false) }
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

            // Quick Preferences Section
            item {
                Text("Care Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column {
                        ProfileMenuRow(
                            title = "Family profiles",
                            subtitle = "Nisha Mehta (Parent)",
                            icon = Icons.Default.Groups,
                            onClick = { showFamilyModal = true }
                        )
                        Divider(color = AvenLine.copy(alpha = 0.6f))
                        ProfileMenuRow(
                            title = "Payments & receipts",
                            subtitle = "Invoice AV-INV-0925-1042 (₹1,249)",
                            icon = Icons.Default.ReceiptLong,
                            onClick = { showInvoiceModal = true }
                        )
                        Divider(color = AvenLine.copy(alpha = 0.6f))
                        ProfileMenuRow(
                            title = "Device test (Video & Audio)",
                            subtitle = "Check camera and microphone before visits",
                            icon = Icons.Default.Videocam,
                            onClick = { showDeviceCheckModal = true }
                        )
                        Divider(color = AvenLine.copy(alpha = 0.6f))
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
            title = { Text("Family Profiles", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Book appointments on behalf of someone you care for:", fontSize = 13.sp, color = AvenMuted)
                    Surface(color = AvenMint, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(AvenTeal), contentAlignment = Alignment.Center) {
                                Text("NM", color = AvenWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Nisha Mehta", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                Text("Parent • 56 years • Female", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
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

    // Invoice Modal
    if (showInvoiceModal) {
        AlertDialog(
            onDismissRequest = { showInvoiceModal = false },
            title = { Text("Receipt AV-INV-0925-1042", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Patient: Aarav Mehta", fontSize = 13.sp, color = AvenInk)
                    Text("Clinician: Dr Mira Shah (Dermatology)", fontSize = 13.sp, color = AvenInk)
                    Text("Date: 25 September 2026", fontSize = 13.sp, color = AvenMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Doctor Consultation", color = AvenMuted, fontSize = 13.sp)
                        Text("₹1,200", fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Platform Fee & GST", color = AvenMuted, fontSize = 13.sp)
                        Text("₹49", fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 13.sp)
                    }
                    Divider(color = AvenLine)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Paid", fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 15.sp)
                        Text("₹1,249", fontWeight = FontWeight.Bold, color = AvenTeal, fontSize = 16.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showInvoiceModal = false }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Download Receipt")
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
