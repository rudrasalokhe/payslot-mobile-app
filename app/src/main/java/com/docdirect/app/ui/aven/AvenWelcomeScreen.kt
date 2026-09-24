package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MedicalServices
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
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit,
    onExploreFirst: () -> Unit,
    onClinicianEnter: () -> Unit = {},
    onOperationsEnter: () -> Unit = {}
) {
    Scaffold(
        containerColor = AvenBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Dark Teal Top Section (#0B3937)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenDeep)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Column {
                    // Logo row: [A] aven
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AvenLime,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "A",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = AvenDeep
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "aven",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenWhite,
                            letterSpacing = (-0.7).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Pill: CARE, ON YOUR TERMS
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = AvenLime
                    ) {
                        Text(
                            text = "CARE, ON YOUR TERMS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Big Headline
                    Text(
                        text = "A little less waiting.\nA lot more living.",
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenWhite,
                        lineHeight = 38.sp,
                        letterSpacing = (-0.7).sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Your doctor, your time.\nAll your care, together.",
                        fontSize = 16.sp,
                        color = Color(0xFFCCDCD5),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Floating Doctor Card and Lime Accent Circle
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AvenWhite,
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth(0.82f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFFD8E7D6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("MS", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AvenTeal)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Dr Mira Shah",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AvenInk
                                        )
                                        Text(
                                            text = "Dermatologist",
                                            fontSize = 12.sp,
                                            color = AvenMuted
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    shape = RoundedCornerShape(15.dp),
                                    color = AvenMint,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Tomorrow · 3:30 PM",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AvenTeal
                                        )
                                    }
                                }
                            }
                        }

                        // Floating Lime Checkmark Circle
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(AvenLime),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = AvenDeep,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Light Bottom Section (#F5F7F3)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "Good care starts here.",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Find the right doctor. Book with clarity.",
                    fontSize = 14.sp,
                    color = AvenMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary CTA: Create an account
                Button(
                    onClick = onCreateAccount,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Create an account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary CTA: I already have an account
                OutlinedButton(
                    onClick = onLogin,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = AvenWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "I already have an account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Tertiary link: Explore doctors first
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExploreFirst() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Explore doctors first",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenTeal
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Fast links for Clinician and Admin portals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Clinician portal",
                        fontSize = 11.sp,
                        color = AvenMuted,
                        modifier = Modifier.clickable { onClinicianEnter() }
                    )
                    Text("  •  ", fontSize = 11.sp, color = AvenLine)
                    Text(
                        text = "Operations desk",
                        fontSize = 11.sp,
                        color = AvenMuted,
                        modifier = Modifier.clickable { onOperationsEnter() }
                    )
                }
            }
        }
    }
}
