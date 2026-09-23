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
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.ui.theme.TealPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutScreen(
    doctorId: String,
    slotId: String,
    symptoms: String,
    viewModel: PatientViewModel,
    onNavigateBack: () -> Unit,
    onBookingSuccess: (appointmentId: String) -> Unit
) {
    val doctor = remember(doctorId) { viewModel.getDoctorById(doctorId) }
    val slots by viewModel.getSlotsForDoctor(doctorId).collectAsState(initial = emptyList())
    val slot = remember(slots, slotId) { slots.find { it.id == slotId } }

    val coroutineScope = rememberCoroutineScope()
    var selectedPaymentMethod by remember { mutableStateOf("Credit / Debit Card") }
    var isProcessing by remember { mutableStateOf(false) }
    var confirmedAppointment by remember { mutableStateOf<Appointment?>(null) }

    if (doctor == null || slot == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading booking parameters...")
        }
        return
    }

    val consultationFee = doctor.consultationFee
    val serviceFee = 5.0
    val totalAmount = consultationFee + serviceFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Checkout") },
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

            // Consultation Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Appointment Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Doctor: ${doctor.name}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Time: ${slot.date} at ${slot.time}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Fee Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Consultation Fee")
                            Text("\$${consultationFee.toInt()}.00", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Platform Service Fee")
                            Text("\$${serviceFee.toInt()}.00", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("\$${totalAmount.toInt()}.00", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                    }
                }
            }

            // Payment Methods Selector
            item {
                Text(
                    text = "Select Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Credit / Debit Card", "UPI / Wallet", "Net Banking").forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) BorderStroke(2.dp, TealPrimary) else null,
                            onClick = { selectedPaymentMethod = method }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isSelected, onClick = { selectedPaymentMethod = method })
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = when (method) {
                                        "Credit / Debit Card" -> Icons.Default.CreditCard
                                        "UPI / Wallet" -> Icons.Default.AccountBalanceWallet
                                        else -> Icons.Default.AccountBalance
                                    },
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = method, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Pay Button
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        isProcessing = true
                        coroutineScope.launch {
                            val createdApt = viewModel.bookAppointment(
                                doctorId = doctor.id,
                                doctorName = doctor.name,
                                doctorSpecialty = doctor.specialty,
                                slot = slot,
                                symptoms = symptoms,
                                fee = totalAmount
                            )
                            isProcessing = false
                            confirmedAppointment = createdApt
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing Payment...")
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pay \$${totalAmount.toInt()}.00 & Confirm Booking")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Success Invoice Modal Dialog
    confirmedAppointment?.let { apt ->
        AlertDialog(
            onDismissRequest = {},
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Payment Successful!")
                }
            },
            text = {
                Column {
                    Text("Your appointment is confirmed. Here is your transaction invoice:")
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Transaction ID: ${apt.transactionId}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text("Doctor: ${apt.doctorName}", style = MaterialTheme.typography.bodySmall)
                            Text("Slot: ${apt.appointmentDate} @ ${apt.appointmentTime}", style = MaterialTheme.typography.bodySmall)
                            Text("Amount Paid: \$${apt.feePaid.toInt()}.00", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onBookingSuccess(apt.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Go to Consultation Chat")
                }
            }
        )
    }
}
