package com.docdirect.app.ui.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.docdirect.app.data.model.TimeSlot
import com.docdirect.app.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctorId: String,
    viewModel: PatientViewModel,
    onNavigateBack: () -> Unit,
    onProceedToPayment: (slotId: String, symptoms: String) -> Unit
) {
    val doctor = remember(doctorId) { viewModel.getDoctorById(doctorId) }
    val availableSlots by viewModel.getSlotsForDoctor(doctorId).collectAsState(initial = emptyList())

    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var symptomsText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    if (doctor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Doctor profile not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(doctor.name) },
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
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Doctor Hero Info Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(TealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = doctor.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = doctor.specialty,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TealPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = doctor.qualification,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (doctor.hospitalAffiliation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🏥 ${doctor.hospitalAffiliation}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Doctor Bio Card
            if (doctor.bio.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "About Doctor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = doctor.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Select Available Slot Section
            item {
                Text(
                    text = "Select Appointment Time Slot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val unbookedSlots = availableSlots.filter { !it.isBooked }
            if (unbookedSlots.isEmpty()) {
                item {
                    Text(
                        text = "No open slots available at the moment. Please check back later.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        unbookedSlots.forEach { slot ->
                            val isSelected = selectedSlot?.id == slot.id
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) TealPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) BorderStroke(2.dp, TealPrimary) else null,
                                onClick = {
                                    selectedSlot = slot
                                    errorMessage = ""
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSlot = slot
                                            errorMessage = ""
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${slot.date} at ${slot.time}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Symptoms Input Section
            item {
                Column {
                    Text(
                        text = "Reason for Visit / Symptoms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = symptomsText,
                        onValueChange = { symptomsText = it },
                        placeholder = { Text("Briefly describe your health concern or reason for booking...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                item {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Book Button Card
            item {
                Button(
                    onClick = {
                        if (selectedSlot == null) {
                            errorMessage = "Please select a time slot to proceed."
                        } else if (symptomsText.isBlank()) {
                            errorMessage = "Please enter your symptoms or reason for visit."
                        } else {
                            onProceedToPayment(selectedSlot!!.id, symptomsText)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proceed to Checkout (\$${doctor.consultationFee.toInt()})")
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
