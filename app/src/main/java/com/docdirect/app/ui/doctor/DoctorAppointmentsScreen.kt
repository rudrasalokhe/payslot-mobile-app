package com.docdirect.app.ui.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.AppointmentStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen(
    viewModel: DoctorViewModel,
    onNavigateBack: () -> Unit,
    onOpenChat: (appointmentId: String) -> Unit,
    onStartVideoCall: (appointmentId: String, patientName: String) -> Unit
) {
    val appointments by viewModel.appointments.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showPrescriptionDialog by remember { mutableStateOf<Appointment?>(null) }

    val filteredAppointments = remember(appointments, selectedFilter) {
        when (selectedFilter) {
            "UPCOMING" -> appointments.filter { it.status == AppointmentStatus.UPCOMING }
            "COMPLETED" -> appointments.filter { it.status == AppointmentStatus.COMPLETED }
            else -> appointments
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Appointments (${appointments.size})") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Filter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "UPCOMING", "COMPLETED").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredAppointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No $selectedFilter appointments found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredAppointments) { appointment ->
                        DoctorAppointmentCard(
                            appointment = appointment,
                            onOpenChat = { onOpenChat(appointment.id) },
                            onIssuePrescription = { showPrescriptionDialog = appointment },
                            onComplete = { viewModel.completeAppointment(appointment.id) },
                            onStartVideoCall = { onStartVideoCall(appointment.id, appointment.patientName) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    showPrescriptionDialog?.let { appointment ->
        PrescriptionDialog(
            appointment = appointment,
            onDismiss = { showPrescriptionDialog = null },
            onSubmit = { prescriptionText ->
                viewModel.issuePrescription(appointment.id, prescriptionText)
                showPrescriptionDialog = null
            }
        )
    }
}
