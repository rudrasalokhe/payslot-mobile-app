package com.docdirect.app.ui.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.docdirect.app.ui.components.AuraBottomNavBar
import com.docdirect.app.ui.theme.*

data class ClinicianUIData(
    val id: String,
    val name: String,
    val specialty: String,
    val school: String,
    val rating: String,
    val reviews: String,
    val tags: List<String>,
    val nextSlot: String,
    val slotBadge: String,
    val isVideo: Boolean,
    val feeCopay: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CliniciansDirectoryScreen(
    patientViewModel: PatientViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToTelehealthCall: (doctorName: String) -> Unit,
    onOpenChat: () -> Unit,
    onSelectDoctor: (doctorId: String) -> Unit
) {
    var selectedNavTab by remember { mutableStateOf("clinicians") }
    var selectedSpecialty by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var bookedToast by remember { mutableStateOf<Pair<String, String>?>(null) }

    val dbDoctors by patientViewModel.doctors.collectAsState()

    val specialties = listOf("All", "Primary Care", "Cardiology", "Endocrinology", "Integrative Health", "Neurology")

    // Default clinicians from designer specification
    val designerClinicians = remember {
        listOf(
            ClinicianUIData(
                id = "doc_vance",
                name = "Dr. Julian Vance, MD",
                specialty = "Preventive Medicine & Longevity",
                school = "Harvard Medical School",
                rating = "4.90",
                reviews = "380+",
                tags = listOf("Lipidology", "Metabolic Health", "DNA Biomarkers"),
                nextSlot = "Today, 2:30 PM (Video)",
                slotBadge = "Available",
                isVideo = true,
                feeCopay = "$0 Copay"
            ),
            ClinicianUIData(
                id = "doc_lin",
                name = "Dr. Sarah Lin, MD, PhD",
                specialty = "Endocrinology & Metabolism",
                school = "Stanford Health Alumni",
                rating = "4.96",
                reviews = "420+",
                tags = listOf("Glucose Monitoring", "Thyroid Optimization", "CGM Data Review"),
                nextSlot = "Tomorrow, 9:00 AM (In-Clinic)",
                slotBadge = "Fast Track",
                isVideo = false,
                feeCopay = "$0 Copay"
            ),
            ClinicianUIData(
                id = "doc_marcus",
                name = "Dr. Marcus Vance, MD",
                specialty = "Interventional Cardiology",
                school = "Johns Hopkins Medicine",
                rating = "4.98",
                reviews = "510+",
                tags = listOf("Coronary Calcium (CAC)", "VO2 Max Optimization", "Arrhythmia"),
                nextSlot = "Friday, 11:15 AM (Video)",
                slotBadge = "Next 48h",
                isVideo = true,
                feeCopay = "$0 Copay"
            )
        )
    }

    // Combine designer clinicians with any registered doctors in Room DB
    val allClinicians = remember(dbDoctors, designerClinicians) {
        val convertedDbDoctors = dbDoctors.map { doc ->
            ClinicianUIData(
                id = doc.id,
                name = doc.name,
                specialty = doc.specialty,
                school = doc.qualification.ifBlank { doc.hospitalAffiliation },
                rating = "%.2f".format(doc.rating),
                reviews = "${doc.reviewCount}+",
                tags = listOf(doc.specialty, "Biomarkers", "Physician"),
                nextSlot = "Today, 4:00 PM (Video)",
                slotBadge = if (doc.isAvailable) "Available" else "Busy",
                isVideo = true,
                feeCopay = "\$${doc.consultationFee.toInt()} Copay"
            )
        }
        designerClinicians + convertedDbDoctors
    }

    val filteredClinicians = remember(allClinicians, selectedSpecialty, searchQuery) {
        allClinicians.filter { clinician ->
            val matchesSpecialty = selectedSpecialty == "All" ||
                    clinician.specialty.contains(selectedSpecialty, ignoreCase = true) ||
                    clinician.tags.any { it.contains(selectedSpecialty, ignoreCase = true) }
            val matchesSearch = searchQuery.isBlank() ||
                    clinician.name.contains(searchQuery, ignoreCase = true) ||
                    clinician.specialty.contains(searchQuery, ignoreCase = true) ||
                    clinician.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchesSpecialty && matchesSearch
        }
    }

    Scaffold(
        containerColor = AuraBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = AuraPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AURA HEALTH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraOnSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Clinicians",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraOnSurface
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenChat) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = AuraOnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraSurface.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            AuraBottomNavBar(
                selectedTab = selectedNavTab,
                onTabSelected = { tab ->
                    selectedNavTab = tab
                    if (tab == "home") {
                        onNavigateToHome()
                    } else if (tab == "care_chat") {
                        onOpenChat()
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Search Bar & Specialty Filter Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by specialty, doctor, or symptom...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = AuraOnSurfaceVariant, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Tune, contentDescription = "Filter", tint = AuraOnSurfaceVariant, modifier = Modifier.size(20.dp))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AuraBorderSubtle, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = AuraSurfaceContainerLowest,
                            focusedContainerColor = AuraSurfaceContainerLowest
                        ),
                        singleLine = true
                    )

                    // Specialty Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(specialties) { specialtyName ->
                            val isSelected = selectedSpecialty == specialtyName
                            Surface(
                                color = if (isSelected) AuraPrimary else AuraSurfaceContainerLowest,
                                shape = RoundedCornerShape(100.dp),
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle) else null,
                                modifier = Modifier.clickable { selectedSpecialty = specialtyName }
                            ) {
                                Text(
                                    text = specialtyName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else AuraOnSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Urgent / Same-Day Access Urgent Care Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSecondaryContainer.copy(alpha = 0.85f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(AuraSurfaceContainerLowest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = AuraSecondary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Need care today?", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AuraOnSecondaryContainer)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = AuraSurfaceContainerLowest,
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = "~8 min wait",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AuraSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Immediate virtual walk-in clinic for non-emergency acute illness, triage & urgent refills.",
                                    fontSize = 12.sp,
                                    color = AuraOnSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraSecondary))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("3 Clinicians Active Now", fontSize = 11.sp, color = AuraOnSecondaryContainer, fontWeight = FontWeight.Medium)
                            }

                            Button(
                                onClick = {
                                    onNavigateToTelehealthCall("Urgent Care Triage Clinician")
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AuraSecondary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Enter Waiting Room", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Section Header: Available Specialists
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Available Specialists", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AuraOnSurface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("• ${filteredClinicians.size} Verified", fontSize = 12.sp, color = AuraOnSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Next 48 hrs", fontSize = 12.sp, color = AuraOnSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Clinician Cards List
            items(filteredClinicians) { clinician ->
                ClinicianCard(
                    clinician = clinician,
                    onBookClick = {
                        bookedToast = clinician.name to clinician.nextSlot
                    }
                )
            }

            // Assurance & Safety Callout Footer
            item {
                Surface(
                    color = AuraSurfaceContainerLow,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("HIPAA-Compliant & Board-Certified", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AuraOnSurface)
                            Text(
                                text = "All video consults are encrypted end-to-end. Records sync directly to your biometric dashboard.",
                                fontSize = 11.sp,
                                color = AuraOnSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Booking Confirmation Dialog
    bookedToast?.let { (docName, slotTime) ->
        AlertDialog(
            onDismissRequest = { bookedToast = null },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AuraSecondary, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Appointment Confirmed", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text("$docName • $slotTime", fontSize = 13.sp, color = AuraOnSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Your clinical preparation notes and video consultation link have been generated.", fontSize = 12.sp, color = AuraOnSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        bookedToast = null
                        onNavigateToTelehealthCall(docName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
                ) {
                    Text("Start Video Consultation")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookedToast = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun ClinicianCard(
    clinician: ClinicianUIData,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile & Ratings Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuraPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(36.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AuraSecondary)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(clinician.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AuraOnSurface)
                            Text(clinician.specialty, fontSize = 12.sp, color = AuraPrimary, fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Save", tint = AuraOutline, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = AuraOutline, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(clinician.school, fontSize = 11.sp, color = AuraOnSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AuraSurfaceContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(clinician.rating, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AuraOnSurface)
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("(${clinician.reviews} reviews)", fontSize = 11.sp, color = AuraOnSurfaceVariant)
                    }
                }
            }

            // Sub-specialty tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                clinician.tags.forEach { tag ->
                    Surface(
                        color = AuraSurfaceContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraOnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Slot Timing Pill & Insurance Notice
            Surface(
                color = AuraSurfaceContainerLow,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (clinician.isVideo) Icons.Default.Videocam else Icons.Default.Apartment,
                            contentDescription = null,
                            tint = AuraPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(clinician.nextSlot, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AuraPrimary)
                    }
                    Text(clinician.slotBadge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AuraSecondary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AuraSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aura Membership In-Network", fontSize = 11.sp, color = AuraOnSurfaceVariant)
                }
                Text(clinician.feeCopay, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AuraOnSurface)
            }

            // Book Button
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
            ) {
                Text("Book 30-min Visit", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(15.dp))
            }
        }
    }
}
