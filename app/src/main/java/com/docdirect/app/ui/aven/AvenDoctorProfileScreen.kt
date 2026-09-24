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
        bio = "Specializes in clinical and aesthetic dermatology, acne protocols, skin barrier restoration, and longevity care at Bandra Skin Clinic.",
        rating = 4.9,
        reviewCount = 128,
        isAvailable = true,
        hospitalAffiliation = "Bandra Skin Clinic, Bandra West, Mumbai"
    )

    var selectedMode by remember { mutableStateOf("video") }
    val reviews by patientViewModel.getReviewsForDoctor(doctor.id).collectAsState(initial = emptyList())

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
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

                Text(
                    text = "Clinician profile",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )

                IconButton(
                    onClick = { /* Share */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AvenWhite)
                        .border(1.dp, AvenLine, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = AvenInk,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total consultation fee",
                            fontSize = 11.sp,
                            color = AvenMuted
                        )
                        Text(
                            text = "₹${(doctor.consultationFee + 49).toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "incl. ₹49 platform fee",
                            fontSize = 10.sp,
                            color = AvenMuted
                        )
                    }

                    Button(
                        onClick = { onProceedToBook(doctor.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier
                            .height(54.dp)
                            .width(200.dp)
                    ) {
                        Text("Book appointment", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
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
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Profile Card Header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AvenWhite,
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AvenMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = doctor.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = doctor.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${doctor.specialty} • ${doctor.qualification}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AvenTeal
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Verified credential chip
                        Surface(
                            color = AvenMint,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Verified Clinician • ${doctor.medicalLicense}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenTeal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Divider(color = AvenLine.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(18.dp))

                        // Stats Grid (Experience, Rating, Reviews)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${doctor.experienceYears} Years", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text(text = "Experience", fontSize = 12.sp, color = AvenMuted)
                            }
                            Box(modifier = Modifier.width(1.dp).height(32.dp).background(AvenLine))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AvenAmber, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = "${doctor.rating}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                }
                                Text(text = "Rating", fontSize = 12.sp, color = AvenMuted)
                            }
                            Box(modifier = Modifier.width(1.dp).height(32.dp).background(AvenLine))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${doctor.reviewCount}+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text(text = "Reviews", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }
            }

            // Clinic Location
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AvenWhite,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Clinic details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AvenMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Bandra Skin Clinic",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenInk
                                )
                                Text(
                                    text = doctor.hospitalAffiliation.ifBlank { "12 Hill Road, Bandra West, Mumbai" },
                                    fontSize = 13.sp,
                                    color = AvenMuted
                                )
                                Text(
                                    text = "Mon–Sat: 10:00 AM – 7:00 PM IST",
                                    fontSize = 12.sp,
                                    color = AvenMuted
                                )
                            }
                        }
                    }
                }
            }

            // Choose Modality
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AvenWhite,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Choose visit type",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Video Option
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedMode == "video") AvenMint else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedMode == "video") AvenTeal else AvenLine
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMode = "video" }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Video consultation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                        Text("30 mins • Instant online follow-up", fontSize = 12.sp, color = AvenMuted)
                                    }
                                }
                                Text("₹${doctor.consultationFee.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // In-Person Option
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedMode == "clinic") AvenMint else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedMode == "clinic") AvenTeal else AvenLine
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMode = "clinic" }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Apartment, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("In-person clinic visit", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                        Text("Bandra Skin Clinic • Comprehensive physical check", fontSize = 12.sp, color = AvenMuted)
                                    }
                                }
                                Text("₹${(doctor.consultationFee + 300).toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                            }
                        }
                    }
                }
            }

            // About & Bio
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AvenWhite,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "About",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = doctor.bio,
                            fontSize = 14.sp,
                            color = AvenMuted,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Patient Reviews Section
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AvenWhite,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Patient experiences",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Surface(
                                color = AvenSand,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AvenAmber, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${doctor.rating} / 5",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenAmber
                                    )
                                }
                            }
                        }

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

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
