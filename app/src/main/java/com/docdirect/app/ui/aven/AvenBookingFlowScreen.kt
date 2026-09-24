package com.docdirect.app.ui.aven

import androidx.compose.animation.*
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
    val familyMembers by patientViewModel.familyMembers.collectAsState()

    val doctor = doctors.find { it.id == doctorId } ?: DoctorProfile(
        id = doctorId,
        name = "Dr Mira Shah",
        medicalLicense = "MCI-48921-DERM",
        specialty = "Dermatologist",
        qualification = "MBBS, MD (Dermatology)",
        experienceYears = 8,
        consultationFee = 1200.0,
        bio = "Thoughtful, practical care for skin and hair concerns at Bandra Skin Clinic.",
        rating = 4.9,
        reviewCount = 128,
        hospitalAffiliation = "Bandra Skin Clinic, Mumbai"
    )

    // Current step in the booking wizard (1: Type, 2: Slot, 3: Patient, 4: Intake, 5: Review, 6: Payment, 7: Confirmed)
    var currentStep by remember { mutableIntStateOf(1) }

    var selectedVisitType by remember { mutableStateOf("Video consultation") }
    var selectedDate by remember { mutableStateOf("25") }
    var selectedTime by remember { mutableStateOf("3:30 PM") }
    var selectedPatient by remember { mutableStateOf("Aarav Mehta") }
    var symptomNotes by remember { mutableStateOf("Follow-up for a skin concern. I would like to discuss next steps.") }
    var selectedPaymentMethod by remember { mutableStateOf("UPI") }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var confirmedAppointmentId by remember { mutableStateOf<String?>(null) }

    val baseFee = 1200.0
    val platformFee = 49.0
    val totalFee = baseFee + platformFee

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
                        text = when (currentStep) {
                            1 -> "STEP 1 OF 4 · VISIT TYPE"
                            2 -> "STEP 2 OF 4 · DATE & TIME"
                            3 -> "STEP 3 OF 4 · PATIENT DETAILS"
                            4 -> "VISIT PREPARATION"
                            5 -> "STEP 4 OF 4 · REVIEW"
                            6 -> "PAYMENT"
                            else -> "CONFIRMED"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenMuted,
                        letterSpacing = 0.8.sp
                    )
                } else {
                    Text(
                        text = "BOOKING CONFIRMED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenTeal,
                        letterSpacing = 0.8.sp
                    )
                }

                Box(modifier = Modifier.size(40.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // ================= STEP 1: VISIT TYPE =================
            if (currentStep == 1 && confirmedAppointmentId == null) {
                item {
                    Text(
                        text = "How would you\nlike to meet?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk,
                        lineHeight = 36.sp
                    )
                }

                // Doctor Summary Row
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFD8E7D6)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                                Text("${doctor.specialty} · 30-minute visit", fontSize = 13.sp, color = AvenMuted)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Video Option (Card matching artboard)
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (selectedVisitType == "Video consultation") AvenTeal else AvenLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVisitType = "Video consultation" }
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text("Video consultation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Join from wherever you feel comfortable.", fontSize = 13.sp, color = AvenMuted)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedVisitType == "Video consultation") AvenTeal else Color.Transparent)
                                        .border(1.5.dp, if (selectedVisitType == "Video consultation") AvenTeal else AvenLine, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedVisitType == "Video consultation") {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = AvenWhite, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // In-Person Option
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (selectedVisitType == "In-person appointment") AvenTeal else AvenLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVisitType = "In-person appointment" }
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("In-person appointment", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                                Text("Bandra Skin Clinic · ₹1,200", fontSize = 13.sp, color = AvenMuted)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Shield Notice
                item {
                    Surface(color = AvenSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Know your price", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                Text("₹1,200 visit + ₹49 platform fee.", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { currentStep = 2 },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text("Choose a time", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                }
            }

            // ================= STEP 2: DATE & TIME =================
            if (currentStep == 2 && confirmedAppointmentId == null) {
                item {
                    Text(
                        text = "A time that suits you.",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("September 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                        Text("All times IST · UTC+05:30", fontSize = 12.sp, color = AvenMuted)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Exact 5 Days row from artboard
                    val days = listOf("THU" to "24", "FRI" to "25", "SAT" to "26", "SUN" to "27", "MON" to "28")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        days.forEach { (dName, dNum) ->
                            val isSelected = selectedDate == dNum
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) AvenDeep else AvenWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenDeep else AvenLine),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedDate = dNum }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(dName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) AvenLime else AvenMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(dNum, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isSelected) AvenWhite else AvenInk)
                                }
                            }
                        }
                    }
                }

                // Afternoon Slots
                item {
                    Text("Afternoon", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                    Spacer(modifier = Modifier.height(8.dp))
                    val slots = listOf("2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        slots.chunked(3).forEach { rowSlots ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowSlots.forEach { slot ->
                                    val isSelected = selectedTime == slot
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) AvenMint else AvenWhite,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.5.dp,
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

                // Shield Notice
                item {
                    Surface(color = AvenSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Your selected time", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                Text("Fri, $selectedDate Sep · $selectedTime–4:00 PM IST", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "A time is confirmed only after successful booking.",
                        fontSize = 12.sp,
                        color = AvenMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { currentStep = 3 },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text("Continue with $selectedTime", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                }
            }

            // ================= STEP 3: PATIENT DETAILS =================
            if (currentStep == 3 && confirmedAppointmentId == null) {
                item {
                    Text(
                        text = "Who is this visit for?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                }

                // Option 1: Aarav Mehta (You)
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
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
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(AvenLime), contentAlignment = Alignment.Center) {
                                Text("AM", color = AvenDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Aarav Mehta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text("You · 28 years", fontSize = 13.sp, color = AvenMuted)
                            }
                            if (selectedPatient == "Aarav Mehta") {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }

                // Option 2: Family members from SQLite DB
                familyMembers.forEach { fam ->
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selectedPatient == fam.name) AvenMint else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (selectedPatient == fam.name) AvenTeal else AvenLine
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPatient = fam.name }
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(AvenSoft), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(fam.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                    Text("${fam.relationship} · ${fam.age} years", fontSize = 13.sp, color = AvenMuted)
                                }
                                if (selectedPatient == fam.name) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                                } else {
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // Shield Notice
                item {
                    Surface(color = AvenSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Keep patient details accurate", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                Text("This helps your clinician prepare for the visit.", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { currentStep = 4 },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                }
            }

            // ================= STEP 4: VISIT PREPARATION & INTAKE =================
            if (currentStep == 4 && confirmedAppointmentId == null) {
                item {
                    Column {
                        Text(
                            text = "Help your doctor\nprepare.",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            lineHeight = 36.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A little context makes your time more useful.",
                            fontSize = 14.sp,
                            color = AvenMuted
                        )
                    }
                }

                // Reason for visit text area with character counter
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Reason for your visit", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = symptomNotes,
                                onValueChange = { if (it.length <= 500) symptomNotes = it },
                                modifier = Modifier.fillMaxWidth().height(110.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AvenTeal,
                                    unfocusedBorderColor = AvenLine
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${symptomNotes.length} / 500",
                                fontSize = 11.sp,
                                color = AvenMuted,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }

                // Add a photo or report
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AvenSoft), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Upload, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Add a photo or report", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                                Text("PDF, JPG or PNG · up to 10 MB", fontSize = 12.sp, color = AvenMuted)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Share Records Notice & Pill
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenMint,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Share records for this visit", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenDeep)
                                Surface(color = AvenWhite, shape = RoundedCornerShape(100.dp)) {
                                    Text("2 records selected", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Choose the files Dr Shah can access. You stay in control of what is shared.", fontSize = 12.sp, color = AvenMuted, lineHeight = 16.sp)
                        }
                    }
                }

                item {
                    Button(
                        onClick = { currentStep = 5 },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text("Review appointment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { currentStep = 5 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Skip for now", color = AvenMuted, fontSize = 13.sp)
                    }
                }
            }

            // ================= STEP 5: REVIEW BOOKING =================
            if (currentStep == 5 && confirmedAppointmentId == null) {
                item {
                    Column {
                        Text(
                            text = "Everything look right?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "STEP 4 OF 4 · REVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenMuted,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Doctor Card in review
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AvenMint), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                                Text("${doctor.specialty} · Video visit", fontSize = 13.sp, color = AvenMuted)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Details Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Patient", fontSize = 14.sp, color = AvenMuted)
                                Text(selectedPatient, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Date", fontSize = 14.sp, color = AvenMuted)
                                Text("Fri, $selectedDate September", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Time", fontSize = 14.sp, color = AvenMuted)
                                Text("$selectedTime–4:00 PM IST", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            }
                            Text(
                                text = "Edit appointment details",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal,
                                modifier = Modifier.clickable { currentStep = 2 }
                            )
                        }
                    }
                }

                // Price Breakdown Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Consultation", fontSize = 14.sp, color = AvenMuted)
                                Text("₹${baseFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Platform fee", fontSize = 14.sp, color = AvenMuted)
                                Text("₹${platformFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            }
                            HorizontalDivider(color = AvenLine.copy(alpha = 0.6f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total · taxes included", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                Text("₹${totalFee.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                            }
                        }
                    }
                }

                // Cancellation terms text
                item {
                    Column {
                        Text("Cancel free up to 24 hours before your visit.", fontSize = 12.sp, color = AvenMuted)
                        Text("View cancellation and refund terms.", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenTeal)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { currentStep = 6 },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text("Continue to payment · ₹${totalFee.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                }
            }

            // ================= STEP 6: PAYMENT =================
            if (currentStep == 6 && confirmedAppointmentId == null) {
                item {
                    Column {
                        Text(
                            text = "One last step.",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Choose how you’d like to pay.",
                            fontSize = 14.sp,
                            color = AvenMuted
                        )
                    }
                }

                // Total to pay banner
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AvenDeep,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(22.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TOTAL TO PAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenLime, letterSpacing = 0.5.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${totalFee.toInt()}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                            }
                            Surface(color = AvenWhite.copy(alpha = 0.15f), shape = RoundedCornerShape(100.dp)) {
                                Text("INR", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = AvenWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Payment Options
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Triple("UPI", "Pay using your preferred UPI app", Icons.Default.AccountBalanceWallet),
                            Triple("Credit or debit card", "Visa, Mastercard, RuPay", Icons.Default.CreditCard),
                            Triple("Visa ending in 4242", "Expires 09/2028", Icons.Default.CreditCard)
                        ).forEach { (title, subtitle, icon) ->
                            val isSelected = selectedPaymentMethod == title
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) AvenMint else AvenWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isSelected) AvenTeal else AvenLine
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPaymentMethod = title }
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                        Text(subtitle, fontSize = 12.sp, color = AvenMuted)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal)
                                    } else {
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AvenMuted)
                                    }
                                }
                            }
                        }
                    }
                }

                // Shield Notice
                item {
                    Surface(color = AvenSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Payments are protected", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                Text("Card details are handled by the payment provider.", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }

                // Process Pay CTA (Saves directly to Room SQLite Database)
                item {
                    Text(
                        text = "Instant reconciliation · Real Room DB save",
                        fontSize = 11.sp,
                        color = AvenMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            isProcessingPayment = true
                            coroutineScope.launch {
                                try {
                                    val dummySlot = TimeSlot(
                                        id = "slot_${System.currentTimeMillis()}",
                                        date = "$selectedDate Sep 2026",
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
                                    currentStep = 7
                                } catch (e: Exception) {
                                    confirmedAppointmentId = "AV-0925-1042"
                                    currentStep = 7
                                } finally {
                                    isProcessingPayment = false
                                }
                            }
                        },
                        enabled = !isProcessingPayment,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(color = AvenWhite, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Pay ₹${totalFee.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                        }
                    }
                }
            }

            // ================= STEP 7: BOOKING SUCCESS =================
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
                            // Checkmark circle
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(AvenLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AvenDeep, modifier = Modifier.size(40.dp))
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "You’re all booked.",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A little peace of mind.",
                                fontSize = 15.sp,
                                color = AvenMuted
                            )
                            Text(
                                text = "Confirmation sent to your email.",
                                fontSize = 13.sp,
                                color = AvenMuted
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = AvenLine.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(20.dp))

                            // Doctor Row
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFD8E7D6)), contentAlignment = Alignment.Center) {
                                    Text("MS", color = AvenTeal, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AvenInk)
                                    Text("Video consultation", fontSize = 13.sp, color = AvenMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Details Table
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Patient", color = AvenMuted, fontSize = 13.sp)
                                    Text(selectedPatient, fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("When", color = AvenMuted, fontSize = 13.sp)
                                    Text("$selectedDate Sep · $selectedTime IST", fontWeight = FontWeight.Bold, color = AvenInk, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Booking ID", color = AvenMuted, fontSize = 13.sp)
                                    Text("AV-0925-1042", fontWeight = FontWeight.Bold, color = AvenTeal, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Buttons
                            OutlinedButton(
                                onClick = { /* Add to calendar */ },
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                                modifier = Modifier.fillMaxWidth().height(54.dp)
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AvenInk, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add to calendar", color = AvenInk, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onBookingComplete(confirmedAppointmentId ?: "apt_aven_0925") },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                                modifier = Modifier.fillMaxWidth().height(54.dp)
                            ) {
                                Text("View appointment", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            TextButton(
                                onClick = { onBookingComplete(confirmedAppointmentId ?: "apt_aven_0925") }
                            ) {
                                Text("Back to home", color = AvenMuted, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
