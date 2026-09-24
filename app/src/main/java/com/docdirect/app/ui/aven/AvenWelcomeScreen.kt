package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.theme.*

@Composable
fun AvenWelcomeScreen(
    onPatientEnter: () -> Unit,
    onClinicianEnter: () -> Unit,
    onOperationsEnter: () -> Unit
) {
    Scaffold(
        containerColor = AvenBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(40.dp))

                // Brand Pill
                Surface(
                    color = AvenMint,
                    shape = RoundedCornerShape(100.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AvenTeal)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AVEN HEALTH",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenDeep,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Headline
                Text(
                    text = "A little less waiting.\nA lot more living.",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk,
                    lineHeight = 44.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Your doctor, your time. All your care, together with transparent INR consultation fees, verified Indian clinicians, and high-definition video calls.",
                    fontSize = 15.sp,
                    color = AvenMuted,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Clinician Spotlight Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(AvenMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("MS", color = AvenTeal, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dr Mira Shah", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                                Text("₹1,200", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenTeal)
                            }
                            Text("Dermatologist • Bandra Skin Clinic", fontSize = 12.sp, color = AvenMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AvenAmber, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("4.9 (128 reviews) • Available today", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AvenAmber)
                            }
                        }
                    }
                }
            }

            // Bottom CTAs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPatientEnter,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Get Started as Patient", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onClinicianEnter,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Dr Shah Portal", fontSize = 13.sp, color = AvenInk, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onOperationsEnter,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Admin Ops", fontSize = 13.sp, color = AvenInk, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
