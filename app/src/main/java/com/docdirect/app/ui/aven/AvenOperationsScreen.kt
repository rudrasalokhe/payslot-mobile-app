package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.docdirect.app.ui.theme.*

@Composable
fun AvenOperationsScreen(
    onNavigateBack: () -> Unit
) {
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
                        text = "Operations & Audit",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )

                    Surface(color = AvenLavender, shape = RoundedCornerShape(100.dp)) {
                        Text(
                            text = "ADMIN OPS",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenBlue
                        )
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

            // Operations Overview Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = AvenWhite,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Verified Clinicians", fontSize = 12.sp, color = AvenMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("4 / 4", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            Text("100% compliant", fontSize = 11.sp, color = AvenTeal)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = AvenWhite,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Disputes / Refunds", fontSize = 12.sp, color = AvenMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("1 Active", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            Text("Auto-reconciled", fontSize = 11.sp, color = AvenMuted)
                        }
                    }
                }
            }

            // Clinician Verification Queue
            item {
                Text("Clinician Verifications", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = AvenWhite,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Dr Mira Shah", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                Text("MCI License: 48921-DERM • Bandra Skin Clinic", fontSize = 12.sp, color = AvenMuted)
                            }
                            Surface(color = AvenMint, shape = RoundedCornerShape(100.dp)) {
                                Text("VERIFIED", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                            }
                        }
                        Divider(color = AvenLine.copy(alpha = 0.5f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Dr Kabir Rao", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                Text("MCI License: 31045-MED • Apollo Medical", fontSize = 12.sp, color = AvenMuted)
                            }
                            Surface(color = AvenMint, shape = RoundedCornerShape(100.dp)) {
                                Text("VERIFIED", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                            }
                        }
                    }
                }
            }

            // Audit Log
            item {
                Text("System Audit Log", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = AvenWhite,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(
                            Triple("25 Sep, 3:30 PM", "Appointment Confirmed", "AV-INV-0925-1042 • Aarav Mehta with Dr Mira Shah (₹1,249 via UPI)"),
                            Triple("24 Sep, 10:45 AM", "Consent Granted", "Aarav Mehta shared Blood-test-report.pdf with Dr Mira Shah"),
                            Triple("23 Sep, 02:15 PM", "Doctor Profile Published", "Dr Mira Shah approved for Telehealth Video Consultations"),
                            Triple("22 Sep, 11:00 AM", "Refund Processed", "AV-REF-0922-4412 • ₹1,249 credited to original payment source")
                        ).forEach { (time, title, desc) ->
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                    Text(time, fontSize = 11.sp, color = AvenMuted)
                                }
                                Text(desc, fontSize = 12.sp, color = AvenMuted)
                            }
                            Divider(color = AvenLine.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
