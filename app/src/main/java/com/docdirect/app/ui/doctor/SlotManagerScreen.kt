package com.docdirect.app.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.docdirect.app.data.model.TimeSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotManagerScreen(
    viewModel: DoctorViewModel,
    onNavigateBack: () -> Unit
) {
    val doctor by viewModel.doctorProfile.collectAsState()

    var feeInput by remember { mutableStateOf(doctor.consultationFee.toString()) }
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedTime by remember { mutableStateOf("10:00 AM") }

    var specialty by remember { mutableStateOf(doctor.specialty) }
    var qualification by remember { mutableStateOf(doctor.qualification) }
    var bio by remember { mutableStateOf(doctor.bio) }
    var hospital by remember { mutableStateOf(doctor.hospitalAffiliation) }

    var isEditingProfile by remember { mutableStateOf(false) }

    val dateOptions = listOf("Today", "Tomorrow", "Day After Tomorrow")
    val timeOptions = listOf("09:00 AM", "10:30 AM", "12:00 PM", "02:00 PM", "04:30 PM", "06:00 PM")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Slots & Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Consultation Fee Manager Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Consultation Fee Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = feeInput,
                                onValueChange = { feeInput = it },
                                label = { Text("Fee ($)") },
                                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    feeInput.toDoubleOrNull()?.let { viewModel.updateFee(it) }
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Update Fee")
                            }
                        }
                    }
                }
            }

            // Create New Availability Slot Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MoreTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Consultation Time Slot",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Date:", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            dateOptions.forEach { dateStr ->
                                FilterChip(
                                    selected = selectedDate == dateStr,
                                    onClick = { selectedDate = dateStr },
                                    label = { Text(dateStr) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Time:", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            timeOptions.take(3).forEach { timeStr ->
                                FilterChip(
                                    selected = selectedTime == timeStr,
                                    onClick = { selectedTime = timeStr },
                                    label = { Text(timeStr) }
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            timeOptions.drop(3).forEach { timeStr ->
                                FilterChip(
                                    selected = selectedTime == timeStr,
                                    onClick = { selectedTime = timeStr },
                                    label = { Text(timeStr) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.addSlot(selectedDate, selectedTime) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Availability Slot")
                        }
                    }
                }
            }

            // Existing Slots Section
            item {
                Text(
                    text = "Current Active Slots (${doctor.availableSlots.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (doctor.availableSlots.isEmpty()) {
                item {
                    Text(
                        text = "No active slots. Add slots above so patients can book appointments.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(doctor.availableSlots) { slot ->
                    TimeSlotItemRow(
                        slot = slot,
                        onDelete = { viewModel.deleteSlot(slot.id) }
                    )
                }
            }

            // Doctor Profile Editor Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Doctor Bio & Specialization",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { isEditingProfile = !isEditingProfile }) {
                                Icon(
                                    imageVector = if (isEditingProfile) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = "Edit Profile"
                                )
                            }
                        }

                        if (isEditingProfile) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = specialty,
                                onValueChange = { specialty = it },
                                label = { Text("Specialty") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = qualification,
                                onValueChange = { qualification = it },
                                label = { Text("Qualifications") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = hospital,
                                onValueChange = { hospital = it },
                                label = { Text("Hospital / Clinic Affiliation") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("About Doctor (Bio)") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.updateProfile(specialty, qualification, bio, hospital)
                                    isEditingProfile = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Profile Changes")
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Specialty: ${doctor.specialty}", fontWeight = FontWeight.SemiBold)
                            Text("Qualifications: ${doctor.qualification}", style = MaterialTheme.typography.bodyMedium)
                            Text("Hospital: ${doctor.hospitalAffiliation}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Bio: ${doctor.bio}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun TimeSlotItemRow(
    slot: TimeSlot,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (slot.isBooked) Color(0xFFF1F5F9) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (slot.isBooked) Color.Gray else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${slot.date} • ${slot.time}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (slot.isBooked) "Booked by Patient" else "Available for Booking",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (slot.isBooked) Color(0xFF0284C7) else Color(0xFF10B981)
                    )
                }
            }

            if (!slot.isBooked) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Slot",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
