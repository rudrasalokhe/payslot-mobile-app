package com.docdirect.app.ui.aven

import androidx.compose.animation.*
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
import com.docdirect.app.data.model.TimeSlot
import com.docdirect.app.ui.patient.PatientViewModel
import com.docdirect.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AvenBookingFlowScreen(
    doctorId: String,
    patientViewModel: PatientViewModel,
    onNavigateBack: () -> Unit,
    onBookingComplete: (appointmentId: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val doctors by patientViewModel.doctors.collectAsState()
    val doctor = doctors.find { it.id == doctorId } ?: DoctorProfile(
        id = doctorId,
        name = "Dr Mira Shah",
        medicalLicense = "MCI-48921-DERM",
        specialty = "Dermatologist",
        qualification = "MBBS, MD (Dermatology)",
        experienceYears = 8,
        consultationFee = 1200.0,
        bio = "Specializes in clinical and aesthetic dermatology at Bandra Skin Clinic.",
        rating = 4.9,
        reviewCount = 128,
        hospitalAffiliation = "Bandra Skin Clinic, Mumbai"
    )

    // Current step in the booking wizard (1 to 5)
    var currentStep by remember { mutableIntStateOf(1) }

    // Booking state
    var selectedVisitType by remember { mutableStateOf("Video consultation") }
    var selectedDate by remember { mutableStateOf("25 Sep 2026") }
    var selectedTime by remember { mutableStateOf("3:30 PM") }
    var selectedPatient by remember { mutableStateOf("Aarav Mehta") }
    var symptomNotes by remember { mutableStateOf("Follow-up for a skin concern. I would like to discuss next steps.") }
    var shareRecordsConsent by remember { mutableStateOf(true) }
    var selectedPaymentMethod by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentFailed by remember { mutableStateOf(false) }
    var confirmedAppointmentId by remember { mutableStateOf<String?>(null) }

    val baseFee = if (selectedVisitType == "Video consultation") doctor.consultationFee else doctor.consultationFee + 300
    val platformFee = 49.0
    val totalFee = baseFee + platformFee

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
                    onClick = {
                        if (currentStep > 1 && confirmedAppointmentId == null) {
                            currentStep--
                        } else {
                            onNavigateBack()
                        }
                    },
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

                if (confirmedAppointmentId == null) {
                    Text(
                        text = "STEP $currentStep OF 4",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenMuted,
                        letterSpacing = 1.sp
                    )
                } else {
                    Text(
                        text = "CONFIRMED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenTeal,
                        letterSpacing = 1.sp
                    )
                }

                Box(modifier = Modifier.size(40.dp)) // Spacer for alignment
            }
        },
        bottomBar = {
            if (confirmedAppointmentId == null) {
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
                            Text("Total payable", fontSize = 11.sp, color = AvenMuted)
                            Text(
                                text = "₹${totalFee.toInt()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                        }

                        Button(
                            onClick = {
                                if (currentStep < 4) {
                                    currentStep++
                                } else {
                                    // Process booking & payment
                                    isProcessingPayment = true
                                    coroutineScope.launch {
                                        try {
                                            val dummySlot = TimeSlot(
                                                id = "slot_${System.currentTimeMillis()}",
                                                date = selectedDate,
                                                time = selectedTime,
                                                isBooked = false
                                            )
                                            val created = patientViewModel.bookAppointment(
                                                doctorId = doctor.id,
                                                doctorName = doctor.name,
                                                doctorSpecialty = doctor.specialty,
                                                slot = dummySlot,
                                                symptoms = symptomNotes,
                                                fee = totalFee
                                            )
                                            confirmedAppointmentId = created.id
                                        } catch (e: Exception) {
                                            // Fallback ID
                                            confirmedAppointmentId = "apt_${System.currentTimeMillis()}"
                                        } finally {
                                            isProcessingPayment = false
                                        }
                                    }
                                }
                            },
                            enabled = !isProcessingPayment,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                            modifier = Modifier
                                .height(54.dp)
                                .width(220.dp)
                        ) {
                            if (isProcessingPayment) {
                                CircularProgressIndicator(color = AvenWhite, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = if (currentStep == 4) "Pay ₹${totalFee.toInt()} & Confirm" else "Continue",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenWhite
                                )
                            }
                        }
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
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // STEP 1: VISIT TYPE
            if (currentStep == 1 && confirmedAppointmentId == null) {
                item {
                    Column {
                        Text(
                            text = "How would you\nlike to meet?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            lineHeight = 34.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Consultation with ${doctor.name} • ${doctor.specialty}",
                            fontSize = 14.sp,
                            color = AvenMuted
                        )
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedVisitType == "Video consultation") AvenMint else AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (selectedVisitType == "Video consultation") AvenTeal else AvenLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVisitType = "Video consultation" }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(AvenTeal.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Video consultation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                                        Text("30 mins • High-definition video call", fontSize = 12.sp, color = AvenMuted)
                                    }
                                }
                                Text("₹${doctor.consultationFee.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AvenInk)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Join securely from your phone. Receive digital prescription, clinical notes, and follow-up guidance immediately.",
                                fontSize = 13.sp,
                                color = AvenMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedVisitType == "In-person clinic visit") AvenMint else AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (selectedVisitType == "In-person clinic visit") AvenTeal else AvenLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVisitType = "In-person clinic visit" }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(AvenTeal.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Apartment, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("In-person clinic visit", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                                        Text("Bandra Skin Clinic, Mumbai", fontSize = 12.sp, color = AvenMuted)
                                    }
                                }
                                Text("₹${(doctor.consultationFee + 300).toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AvenInk)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Comprehensive physical exam at Bandra Skin Clinic, 12 Hill Road. Free parking and accessible entry available.",
                                fontSize = 13.sp,
                                color = AvenMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // STEP 2: DATE & TIME
            if (currentStep == 2 && confirmedAppointmentId == null) {
                item {
                    Column {
                        Text(
                            text = "A time that\nsuits you.",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            lineHeight = 34.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Time zone: Asia/Kolkata (IST UTC+05:30)",
                            fontSize = 13.sp,
                            color = AvenMuted
                        )
                    }
                }

                // Date Selection Chips
                item {
                    Text("Select date", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                    Spacer(modifier = Modifier.height(8.dp))
                    val dates = listOf("24 Sep 2026", "25 Sep 2026", "26 Sep 2026", "27 Sep 2026")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dates.forEach { date ->
                            val isSelected = selectedDate == date
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) AvenDeep else AvenWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenDeep else AvenLine),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedDate = date }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val parts = date.split(" ")
                                    Text(text = parts[0], fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isSelected) AvenLime else AvenInk)
                                    Text(text = parts[1], fontSize = 12.sp, color = if (isSelected) AvenWhite else AvenMuted)
                                }
                            }
                        }
                    }
                }

                // Time Slots
                item {
                    Text("Select time slot", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                    Spacer(modifier = Modifier.height(8.dp))
                    val slots = listOf("10:00 AM", "11:30 AM", "3:30 PM", "4:30 PM", "5:30 PM", "6:15 PM")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        slots.chunked(3).forEach { rowSlots ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowSlots.forEach { slot ->
                                    val isSelected = selectedTime == slot
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) AvenMint else AvenWhite,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) AvenTeal else AvenLine
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedTime = slot }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = slot,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) AvenTeal else AvenInk
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // STEP 3: PATIENT SELECTION
            if (currentStep == 3 && confirmedAppointmentId == null) {
                item {
                    Column {
                        Text(
                            text = "Who is this\nvisit for?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            lineHeight = 34.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Keep each person's details and medical history separate.",
                            fontSize = 13.sp,
                            color = AvenMuted
                        )
                    }
                }

                item {
                    // Aarav Mehta (Self)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedPatient == "Aarav Mehta") AvenMint else AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (selectedPatient == "Aarav Mehta") AvenTeal else AvenLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPatient = "Aarav Mehta" }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AvenTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("AM", color = AvenWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Aarav Mehta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text("Self • 28 years • Male • Born 18 Mar 1998", fontSize = 12.sp, color = AvenMuted)
                            }
                            if (selectedPatient == "Aarav Mehta") {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal)
                            }
                        }
                    }
                }

                item {
                    // Nisha Mehta (Parent)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedPatient == "Nisha Mehta") AvenMint else AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (selectedPatient == "Nisha Mehta") AvenTeal else AvenLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPatient = "Nisha Mehta" }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AvenBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("NM", color = AvenWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Nisha Mehta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text("Parent • 56 years • Female", fontSize = 12.sp, color = AvenMuted)
                            }
                            if (selectedPatient == "Nisha Mehta") {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal)
                            }
                        }
                    }
                }
            }

            // STEP 4: INTAKE & REVIEW & PAYMENT
            if (currentStep == 4 && confirmedAppointmentId == null) {
                item {
                    Column {
                        Text(
                            text = "Review & Pay",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Help ${doctor.name} prepare for your consultation.",
                            fontSize = 13.sp,
                            color = AvenMuted
                        )
                    }
                }

                // Reason for visit input
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Reason for visit", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = symptomNotes,
                                onValueChange = { symptomNotes = it },
                                placeholder = { Text("Describe symptoms or key topics to discuss...", fontSize = 13.sp, color = AvenMuted) },
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                // Consent to Share Records
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = shareRecordsConsent,
                                onCheckedChange = { shareRecordsConsent = it },
                                colors = CheckboxDefaults.colors(checkedColor = AvenTeal)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Share records with ${doctor.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                Text("Includes Blood-test-report.pdf. Access expires 27 Sep 2026.", fontSize = 11.sp, color = AvenMuted)
                            }
                        }
                    }
                }

                // Price Breakdown Card
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Price breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Doctor consultation fee", fontSize = 13.sp, color = AvenMuted)
                                Text("₹${baseFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Platform fee & taxes", fontSize = 13.sp, color = AvenMuted)
                                Text("₹${platformFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = AvenLine)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text("₹${totalFee.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                            }
                        }
                    }
                }

                // Payment Method Selector
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Choose payment method", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                            Spacer(modifier = Modifier.height(12.dp))

                            listOf(
                                "UPI (Google Pay / PhonePe)" to Icons.Default.QrCode,
                                "Credit / Debit Card (•••• 4242)" to Icons.Default.CreditCard,
                                "Net Banking" to Icons.Default.AccountBalance
                            ).forEach { (method, icon) ->
                                val isSelected = selectedPaymentMethod == method
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) AvenMint else AvenWhite,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) AvenTeal else AvenLine
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPaymentMethod = method }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(icon, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(method, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AvenInk, modifier = Modifier.weight(1f))
                                        if (isSelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // BOOKING SUCCESS SCREEN (All Booked!)
            if (confirmedAppointmentId != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(AvenLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AvenDeep, modifier = Modifier.size(36.dp))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "You're all booked.",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Text(
                                text = "A little peace of mind.",
                                fontSize = 15.sp,
                                color = AvenMuted
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            Divider(color = AvenLine)
                            Spacer(modifier = Modifier.height(20.dp))

                            // Details
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Clinician", color = AvenMuted, fontSize = 13.sp)
                                    Text(doctor.name, fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Date & Time", color = AvenMuted, fontSize = 13.sp)
                                    Text("$selectedDate, $selectedTime IST", fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Visit Type", color = AvenMuted, fontSize = 13.sp)
                                    Text(selectedVisitType, fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Patient", color = AvenMuted, fontSize = 13.sp)
                                    Text(selectedPatient, fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Amount Paid", color = AvenMuted, fontSize = 13.sp)
                                    Text("₹${totalFee.toInt()} (Paid via UPI)", fontWeight = FontWeight.Bold, color = AvenTeal, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { onBookingComplete(confirmedAppointmentId ?: "apt_aven_0925") },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                                modifier = Modifier.fillMaxWidth().height(54.dp)
                            ) {
                                Text("View in Appointments", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
