package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
    var selectedFilter by remember { mutableStateOf("All") }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filterOptions = listOf("All", "Video visit", "Dermatology", "General Medicine", "Pediatrics", "Cardiology", "Under ₹1,200")

    val filteredList = remember(doctors, searchQuery, selectedFilter) {
        doctors.filter { doc ->
            val matchesQuery = searchQuery.isBlank() ||
                    doc.name.contains(searchQuery, ignoreCase = true) ||
                    doc.specialty.contains(searchQuery, ignoreCase = true) ||
                    doc.hospitalAffiliation.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Video visit" -> true
                "Dermatology" -> doc.specialty.contains("Dermatolog", ignoreCase = true)
                "General Medicine" -> doc.specialty.contains("Medicine", ignoreCase = true)
                "Pediatrics" -> doc.specialty.contains("Pediatric", ignoreCase = true)
                "Cardiology" -> doc.specialty.contains("Cardio", ignoreCase = true)
                "Under ₹1,200" -> doc.consultationFee <= 1200.0
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
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Explore clinicians",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )
                Text(
                    text = "Find transparent fees, verified reviews, and prompt appointments.",
                    fontSize = 13.sp,
                    color = AvenMuted
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by doctor, specialty, or clinic...", fontSize = 14.sp, color = AvenMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AvenMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = AvenMuted)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.clickable { showFilterSheet = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvenInk)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Filter", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            }
                        }
                    }

                    items(filterOptions) { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AvenDeep else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenDeep else AvenLine),
                            modifier = Modifier.clickable { selectedFilter = filter }
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                fontSize = 12.sp,
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredList.size} doctors available",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AvenMuted
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
                                text = "Try broadening your filters or searching for another specialty.",
                                fontSize = 13.sp,
                                color = AvenMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedFilter = "All"
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
                            selected = selectedFilter == "Video visit",
                            onClick = { selectedFilter = "Video visit"; showFilterSheet = false },
                            label = { Text("Video visit") }
                        )
                        FilterChip(
                            selected = selectedFilter == "All",
                            onClick = { selectedFilter = "All"; showFilterSheet = false },
                            label = { Text("In person") }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Specialty", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AvenInk)
                    listOf("Dermatology", "General Medicine", "Pediatrics", "Cardiology").forEach { cat ->
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
