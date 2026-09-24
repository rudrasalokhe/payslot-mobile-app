package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.docdirect.app.data.model.DoctorProfile
import com.docdirect.app.ui.patient.PatientViewModel
import com.docdirect.app.ui.theme.*

@Composable
fun AvenDoctorProfileScreen(
    doctorId: String,
    patientViewModel: PatientViewModel,
    onNavigateBack: () -> Unit,
    onProceedToBook: (doctorId: String) -> Unit
) {
    val doctors by patientViewModel.doctors.collectAsState()
    val doctor = doctors.find { it.id == doctorId } ?: DoctorProfile(
        id = doctorId,
        name = "Dr Mira Shah",
        medicalLicense = "MCI-48921-DERM",
        specialty = "Dermatologist",
        qualification = "MBBS, MD (Dermatology)",
        experienceYears = 8,
        consultationFee = 1200.0,
        bio = "Thoughtful, practical care for skin and hair concerns, with time to understand what matters to you.",
        rating = 4.9,
        reviewCount = 128,
        isAvailable = true,
        hospitalAffiliation = "Bandra Skin Clinic, Bandra West · English, Hindi"
    )

    var isBookmarked by remember { mutableStateOf(false) }
    val reviews by patientViewModel.getReviewsForDoctor(doctor.id).collectAsState(initial = emptyList())

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AvenInk,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { isBookmarked = !isBookmarked },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AvenWhite)
                        .border(1.dp, AvenLine, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) AvenTeal else AvenInk,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AvenWhite,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine.copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Video & clinic visits available",
                        fontSize = 12.sp,
                        color = AvenMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onProceedToBook(doctor.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Text("View appointment times", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Doctor Hero Box
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Portrait Box
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFFD8E7D6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = doctor.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = doctor.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = AvenTeal, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = doctor.specialty,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = AvenMuted
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = AvenLine.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(20.dp))

                        // 3 Exact Columns from artboard
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(text = "${doctor.experienceYears} years", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text(text = "Experience", fontSize = 12.sp, color = AvenMuted)
                            }
                            Box(modifier = Modifier.width(1.dp).height(34.dp).background(AvenLine))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AvenAmber, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = "${doctor.rating}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                }
                                Text(text = "${doctor.reviewCount} reviews", fontSize = 12.sp, color = AvenMuted)
                            }
                            Box(modifier = Modifier.width(1.dp).height(34.dp).background(AvenLine))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(text = "₹${doctor.consultationFee.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text(text = "Visit fee", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }
            }

            // About Section
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "A little about Dr Shah",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Text(
                                text = "Reviews",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = doctor.bio,
                            fontSize = 14.sp,
                            color = AvenMuted,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Clinic Row
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AvenSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bandra Skin Clinic",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Text(
                                text = "Bandra West · English, Hindi",
                                fontSize = 13.sp,
                                color = AvenMuted
                            )
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Reviews List
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Patient experiences",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        if (reviews.isNotEmpty()) {
                            reviews.forEachIndexed { index, rev ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(rev.patientName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                        Text("★".repeat(rev.rating), color = AvenAmber, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "“${rev.reviewText}”",
                                        fontSize = 13.sp,
                                        color = AvenMuted,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${rev.visitType} • ${rev.date}", fontSize = 11.sp, color = AvenLine)
                                }
                                if (index < reviews.size - 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = AvenLine.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        } else {
                            Text("No reviews yet for this clinician.", fontSize = 13.sp, color = AvenMuted)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
