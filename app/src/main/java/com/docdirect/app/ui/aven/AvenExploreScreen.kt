package com.docdirect.app.ui.aven

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
import com.docdirect.app.ui.patient.PatientViewModel
import com.docdirect.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvenExploreScreen(
    patientViewModel: PatientViewModel,
    onTabSelected: (String) -> Unit,
    onDoctorSelected: (doctorId: String) -> Unit,
    onBookDoctor: (doctorId: String) -> Unit
) {
    val doctors by patientViewModel.doctors.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All care") }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filterOptions = listOf("All care", "Video visits", "In person", "Dermatology", "General", "Pediatrics")

    val filteredList = remember(doctors, searchQuery, selectedFilter) {
        doctors.filter { doc ->
            val matchesQuery = searchQuery.isBlank() ||
                    doc.name.contains(searchQuery, ignoreCase = true) ||
                    doc.specialty.contains(searchQuery, ignoreCase = true) ||
                    doc.hospitalAffiliation.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "All care" -> true
                "Video visits" -> true
                "In person" -> true
                "Dermatology" -> doc.specialty.contains("Dermatolog", ignoreCase = true)
                "General" -> doc.specialty.contains("Medicine", ignoreCase = true)
                "Pediatrics" -> doc.specialty.contains("Pediatric", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Exact Headline from artboard
                Text(
                    text = "Find your kind",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk,
                    lineHeight = 36.sp
                )
                Text(
                    text = "of care.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Search Bar
                Surface(
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search doctors and specialties", fontSize = 14.sp, color = AvenMuted) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = AvenMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Pills Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(filterOptions) { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) AvenDeep else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenDeep else AvenLine),
                            modifier = Modifier.clickable { selectedFilter = filter }
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AvenLime else AvenInk
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AvenBottomNav(
                selectedTab = "explore",
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        modifier = Modifier.clickable { showFilterSheet = true }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AvenWhite,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No matches just yet.",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try broadening your search or resetting filters.",
                                fontSize = 13.sp,
                                color = AvenMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedFilter = "All care"
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                            ) {
                                Text("Clear filters")
                            }
                        }
                    }
                }
            } else {
                items(filteredList) { doctor ->
                    AvenDoctorCard(
                        doctor = doctor,
                        onBookClick = { onBookDoctor(doctor.id) },
                        onProfileClick = { onDoctorSelected(doctor.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showFilterSheet) {
        AlertDialog(
            onDismissRequest = { showFilterSheet = false },
            title = { Text("Filter Clinicians", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Visit Modality", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AvenInk)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedFilter == "Video visits",
                            onClick = { selectedFilter = "Video visits"; showFilterSheet = false },
                            label = { Text("Video visit") }
                        )
                        FilterChip(
                            selected = selectedFilter == "In person",
                            onClick = { selectedFilter = "In person"; showFilterSheet = false },
                            label = { Text("In person") }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Specialty", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AvenInk)
                    listOf("Dermatology", "General", "Pediatrics").forEach { cat ->
                        TextButton(
                            onClick = { selectedFilter = cat; showFilterSheet = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(cat, color = AvenTeal, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterSheet = false }) {
                    Text("Close", color = AvenTeal)
                }
            }
        )
    }
}
