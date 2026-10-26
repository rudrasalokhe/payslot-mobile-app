package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalContext
import com.docdirect.app.utils.PrescriptionPdfGenerator
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.AppointmentStatus
import com.docdirect.app.data.model.DoctorProfile
import com.docdirect.app.ui.doctor.DoctorViewModel
import com.docdirect.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AvenClinicianWorkspaceScreen(
    doctorViewModel: DoctorViewModel,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit = {},
    onStartVideoCall: (appointmentId: String, patientName: String) -> Unit,
    onOpenChat: (appointmentId: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val doctor by doctorViewModel.doctorProfile.collectAsState()
    val appointments by doctorViewModel.appointments.collectAsState()

    var isAvailable by remember { mutableStateOf(doctor.isAvailable) }
    LaunchedEffect(doctor.isAvailable) {
        isAvailable = doctor.isAvailable
    }

    var prescriptionText by remember { mutableStateOf("Tretinoin 0.025% cream - Apply thin layer at night. Ceramide moisturizer twice daily.") }
    var clinicalNotes by remember { mutableStateOf("Patient presents with mild erythema on cheek area. Skin barrier compromise secondary to over-exfoliation. Advised gentle barrier-repair protocol.") }
    var showSuccessToast by remember { mutableStateOf(false) }
    var showProfileSavedBanner by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPatientHistoryModal by remember { mutableStateOf(false) }

    val doctorInitials = remember(doctor.name) {
        val parts = doctor.name.trim().split(" ")
            .filter { it.isNotBlank() && !it.startsWith("Dr", ignoreCase = true) }
        if (parts.isNotEmpty()) {
            parts.mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
        } else {
            "DR"
        }
    }

    val activeAppointment = appointments.firstOrNull { it.status == AppointmentStatus.UPCOMING }
        ?: Appointment(
            id = "apt_aven_0925",
            patientId = "user_aarav_mehta",
            patientName = "Aarav Mehta",
            doctorId = doctor.id,
            doctorName = doctor.name,
            doctorSpecialty = doctor.specialty,
            appointmentDate = "25 Sep 2026",
            appointmentTime = "3:30 PM IST",
            symptoms = "Follow-up consultation. Discussing treatment progression and ongoing maintenance.",
            feePaid = doctor.consultationFee,
            status = AppointmentStatus.UPCOMING
        )

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AvenInk, modifier = Modifier.size(18.dp))
                    }

                    Text(
                        text = "Clinician Workspace",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = if (isAvailable) AvenMint else AvenRose,
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.clickable {
                                val nextState = !isAvailable
                                isAvailable = nextState
                                doctorViewModel.toggleAvailability(nextState)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isAvailable) AvenTeal else AvenRed))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAvailable) "ONLINE" else "BUSY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAvailable) AvenTeal else AvenRed
                                )
                            }
                        }

                        IconButton(
                            onClick = onSignOut,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AvenWhite)
                                .border(1.dp, AvenLine, CircleShape)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sign out", tint = AvenInk, modifier = Modifier.size(16.dp))
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

            // Dynamic Doctor Profile Hero Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = AvenDeep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(AvenLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(doctorInitials, color = AvenDeep, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doctor.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenWhite
                                )
                                Text(
                                    text = "${doctor.specialty} • ${doctor.hospitalAffiliation.ifBlank { "Private Practice" }}",
                                    fontSize = 13.sp,
                                    color = AvenLime
                                )
                                Text(
                                    text = "MCI License: ${doctor.medicalLicense.ifBlank { "VERIFIED-MCI" }} • Active",
                                    fontSize = 11.sp,
                                    color = AvenWhite.copy(alpha = 0.7f)
                                )
                            }

                            // Edit / Setup Profile Action Pill
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = AvenLime,
                                modifier = Modifier.clickable { showEditProfileDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = AvenDeep, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Profile", color = AvenDeep, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Doctor Credentials and Practice Metrics Strip
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF2A3A35),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("FEE / VISIT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AvenLime, letterSpacing = 0.5.sp)
                                    Text("₹${doctor.consultationFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                                }
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(AvenWhite.copy(alpha = 0.2f)))
                                Column {
                                    Text("EXPERIENCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AvenLime, letterSpacing = 0.5.sp)
                                    Text("${doctor.experienceYears} Years", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                                }
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(AvenWhite.copy(alpha = 0.2f)))
                                Column {
                                    Text("QUALIFICATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AvenLime, letterSpacing = 0.5.sp)
                                    Text(doctor.qualification.split(",").firstOrNull()?.trim() ?: "MD", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                                }
                            }
                        }
                    }
                }
            }

            // Profile Updated Confirmation Banner
            if (showProfileSavedBanner) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AvenMint,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Profile updated! Your credentials and fee (₹${doctor.consultationFee.toInt()}) are live across DocDirect.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AvenTeal
                            )
                        }
                    }
                }
            }

            // Custom Slot & Availability Planner Card (Days on/off, timings 10am-2pm, fees)
            item {
                DoctorCustomSlotPlannerCard(
                    doctor = doctor,
                    doctorViewModel = doctorViewModel
                )
            }

            // Doctor Earnings & Payout Dashboard Card
            item {
                DoctorEarningsDashboardCard(
                    doctor = doctor,
                    doctorViewModel = doctorViewModel
                )
            }

            // Doctor Earnings & Payout Dashboard Card
            item {
                DoctorEarningsDashboardCard(
                    doctor = doctor,
                    doctorViewModel = doctorViewModel
                )
            }

            // Custom Slot & Availability Planner Card (Days on/off, timings 10am-2pm, fees)
            item {
                DoctorCustomSlotPlannerCard(
                    doctor = doctor,
                    doctorViewModel = doctorViewModel
                )
            }

            // Doctor Earnings & Payout Dashboard Card
            item {
                DoctorEarningsDashboardCard(
                    doctor = doctor,
                    doctorViewModel = doctorViewModel
                )
            }

            // Doctor Earnings & Payout Dashboard Card
            item {
                DoctorEarningsDashboardCard(
                    doctor = doctor,
                    doctorViewModel = doctorViewModel
                )
            }

            // Next Patient Up for Consultation
            item {
                Text("Next Scheduled Consultation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
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
                                        .background(AvenMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("AM", color = AvenTeal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(activeAppointment.patientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                    Text("${activeAppointment.appointmentDate} at ${activeAppointment.appointmentTime}", fontSize = 12.sp, color = AvenMuted)
                                }
                            }
                            AvenStatusBadge(status = activeAppointment.status)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(color = AvenSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Reported Concerns:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenMuted)
                                Text(activeAppointment.symptoms, fontSize = 13.sp, color = AvenInk)
                                Spacer(modifier = Modifier.height(6.dp))
                                val attachedRecords by doctorViewModel.getRecordsForAppointment(activeAppointment.id).collectAsState(initial = emptyList())
                                if (attachedRecords.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Attached Patient Documents (${attachedRecords.size}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                                        attachedRecords.forEach { rec ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().clickable {
                                                    val pdf = PrescriptionPdfGenerator.generatePrescriptionPdf(
                                                        context = context,
                                                        doctorName = doctor.name.ifBlank { "Dr Mira Shah" },
                                                        doctorSpecialty = doctor.specialty.ifBlank { "Dermatology" },
                                                        doctorLicense = doctor.medicalLicense.ifBlank { "MCI-48921-DERM" },
                                                        clinicName = "DocDirect Medical Clinic",
                                                        patientName = activeAppointment.patientName,
                                                        consultationId = activeAppointment.id,
                                                        consultationDate = activeAppointment.appointmentDate,
                                                        dosageInstructions = "Attached Consultation Record: ${rec.title} (${rec.category})"
                                                    )
                                                    PrescriptionPdfGenerator.sharePrescriptionPdf(context, pdf)
                                                },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${rec.title} (${rec.category})", fontSize = 12.sp, color = AvenTeal, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Shared: Blood-test-report.pdf (Dermatology Lab)", fontSize = 12.sp, color = AvenTeal, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 1-Tap Patient History & Complaints Summary Action
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AvenMint,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPatientHistoryModal = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "1-Tap Patient History Preview",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AvenTeal
                                        )
                                        Text(
                                            text = "Complaints, past visits & medical records",
                                            fontSize = 10.sp,
                                            color = AvenTeal.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    color = AvenTeal,
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(
                                        text = "View Summary →",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenWhite,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Call Action
                        Button(
                            onClick = { onStartVideoCall(activeAppointment.id, activeAppointment.patientName) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(20.dp), tint = AvenWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Video Consultation", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { onOpenChat(activeAppointment.id) },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvenInk)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chat with Patient", color = AvenInk, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Clinical Notes & Prescription Pad
            item {
                Text("Clinical Workspace & Notes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AvenInk)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Draft Consultation Notes (Private until signed):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        OutlinedTextField(
                            value = clinicalNotes,
                            onValueChange = { clinicalNotes = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text("Digital Prescription & Instructions:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        OutlinedTextField(
                            value = prescriptionText,
                            onValueChange = { prescriptionText = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    doctorViewModel.issuePrescription(activeAppointment.id, prescriptionText)
                                    showSuccessToast = true
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign & Issue Digital Prescription", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val pdf = PrescriptionPdfGenerator.generatePrescriptionPdf(
                                    context = context,
                                    doctorName = doctor.name.ifBlank { "Dr Mira Shah" },
                                    doctorSpecialty = doctor.specialty.ifBlank { "Dermatologist" },
                                    doctorLicense = doctor.medicalLicense.ifBlank { "MCI-48921-DERM" },
                                    clinicName = "DocDirect Bandra Clinic, Mumbai",
                                    patientName = activeAppointment.patientName,
                                    consultationId = activeAppointment.id,
                                    consultationDate = activeAppointment.appointmentDate,
                                    dosageInstructions = prescriptionText
                                )
                                PrescriptionPdfGenerator.sharePrescriptionPdf(context, pdf)
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = AvenInk)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export & Share Rx PDF", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                        }

                        if (showSuccessToast) {
                            Surface(color = AvenMint, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Prescription digitally signed & delivered to ${activeAppointment.patientName}'s account.",
                                    modifier = Modifier.padding(10.dp),
                                    fontSize = 12.sp,
                                    color = AvenTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    // 1-Tap Patient Clinical History Preview Dialog
    if (showPatientHistoryModal) {
        PatientClinicalHistoryDialog(
            patientName = activeAppointment.patientName,
            symptoms = activeAppointment.symptoms,
            appointmentDate = activeAppointment.appointmentDate,
            appointmentTime = activeAppointment.appointmentTime,
            onDismiss = { showPatientHistoryModal = false },
            onStartCall = {
                showPatientHistoryModal = false
                onStartVideoCall(activeAppointment.id, activeAppointment.patientName)
            }
        )
    }

    // 1-Tap Patient Clinical History Preview Dialog
    if (showPatientHistoryModal) {
        PatientClinicalHistoryDialog(
            patientName = activeAppointment.patientName,
            symptoms = activeAppointment.symptoms,
            appointmentDate = activeAppointment.appointmentDate,
            appointmentTime = activeAppointment.appointmentTime,
            onDismiss = { showPatientHistoryModal = false },
            onStartCall = {
                showPatientHistoryModal = false
                onStartVideoCall(activeAppointment.id, activeAppointment.patientName)
            }
        )
    }

    // Modal Edit Profile Dialog
    if (showEditProfileDialog) {
        AvenDoctorProfileEditDialog(
            initialDoctor = doctor,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, license, specialty, qual, exp, fee, hosp, bio ->
                doctorViewModel.updateFullProfile(
                    name = name,
                    medicalLicense = license,
                    specialty = specialty,
                    qualification = qual,
                    experienceYears = exp,
                    fee = fee,
                    hospital = hosp,
                    bio = bio,
                    onComplete = {
                        showEditProfileDialog = false
                        showProfileSavedBanner = true
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvenDoctorProfileEditDialog(
    initialDoctor: DoctorProfile,
    onDismiss: () -> Unit,
    onSave: (name: String, license: String, specialty: String, qualification: String, exp: Int, fee: Double, hospital: String, bio: String) -> Unit
) {
    var editName by remember { mutableStateOf(initialDoctor.name) }
    var editLicense by remember { mutableStateOf(initialDoctor.medicalLicense) }
    var editSpecialty by remember { mutableStateOf(initialDoctor.specialty) }
    var editQualification by remember { mutableStateOf(initialDoctor.qualification) }
    var editExperienceStr by remember { mutableStateOf(initialDoctor.experienceYears.toString()) }
    var editFeeStr by remember { mutableStateOf(initialDoctor.consultationFee.toInt().toString()) }
    var editHospital by remember { mutableStateOf(initialDoctor.hospitalAffiliation) }
    var editBio by remember { mutableStateOf(initialDoctor.bio) }

    val specialties = listOf(
        "Dermatology", "Cardiology", "Pediatrics", "General Medicine",
        "Neurology", "Orthopedics", "Gynecology", "ENT", "Psychiatry"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = AvenWhite,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Edit Clinician Profile",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "Changes reflect in patient search & booking",
                            fontSize = 12.sp,
                            color = AvenMuted
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AvenSoft)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AvenInk, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AvenLine)
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Full Name
                    Column {
                        Text("Doctor Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            placeholder = { Text("e.g. Dr. Mira Shah", fontSize = 13.sp, color = AvenMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AvenWhite,
                                unfocusedContainerColor = AvenSoft,
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            )
                        )
                    }

                    // License
                    Column {
                        Text("Medical Registration Number", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editLicense,
                            onValueChange = { editLicense = it },
                            placeholder = { Text("e.g. MCI-48921-DERM", fontSize = 13.sp, color = AvenMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AvenWhite,
                                unfocusedContainerColor = AvenSoft,
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            )
                        )
                    }

                    // Specialty chips
                    Column {
                        Text("Specialty", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(specialties) { spec ->
                                val isSelected = spec.equals(editSpecialty, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isSelected) AvenTeal else AvenSoft,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenTeal else AvenLine),
                                    modifier = Modifier.clickable { editSpecialty = spec }
                                ) {
                                    Text(
                                        text = spec,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AvenWhite else AvenInk,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Qualification & Experience
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text("Qualification / Degree", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = editQualification,
                                onValueChange = { editQualification = it },
                                placeholder = { Text("e.g. MBBS, MD", fontSize = 13.sp, color = AvenMuted) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = AvenWhite,
                                    unfocusedContainerColor = AvenSoft,
                                    focusedBorderColor = AvenTeal,
                                    unfocusedBorderColor = AvenLine
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(0.7f)) {
                            Text("Exp (Yrs)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = editExperienceStr,
                                onValueChange = { editExperienceStr = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = AvenWhite,
                                    unfocusedContainerColor = AvenSoft,
                                    focusedBorderColor = AvenTeal,
                                    unfocusedBorderColor = AvenLine
                                )
                            )
                        }
                    }

                    // Fee & Hospital
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(0.9f)) {
                            Text("Fee (₹)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = editFeeStr,
                                onValueChange = { editFeeStr = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = AvenWhite,
                                    unfocusedContainerColor = AvenSoft,
                                    focusedBorderColor = AvenTeal,
                                    unfocusedBorderColor = AvenLine
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("Hospital / Clinic Affiliation", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = editHospital,
                                onValueChange = { editHospital = it },
                                placeholder = { Text("e.g. Bandra Skin Clinic", fontSize = 13.sp, color = AvenMuted) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = AvenWhite,
                                    unfocusedContainerColor = AvenSoft,
                                    focusedBorderColor = AvenTeal,
                                    unfocusedBorderColor = AvenLine
                                )
                            )
                        }
                    }

                    // Bio
                    Column {
                        Text("Professional Bio / Summary", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            placeholder = { Text("Clinical focus, treatment protocols, background...", fontSize = 13.sp, color = AvenMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AvenWhite,
                                unfocusedContainerColor = AvenSoft,
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AvenLine)
                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Cancel", color = AvenInk, fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            val trimmedName = editName.trim()
                            val formattedName = if (trimmedName.startsWith("Dr", ignoreCase = true)) trimmedName else "Dr. $trimmedName"
                            val exp = editExperienceStr.toIntOrNull() ?: initialDoctor.experienceYears
                            val fee = editFeeStr.toDoubleOrNull() ?: initialDoctor.consultationFee

                            onSave(
                                formattedName,
                                editLicense.trim().ifBlank { initialDoctor.medicalLicense },
                                editSpecialty,
                                editQualification.trim().ifBlank { initialDoctor.qualification },
                                exp,
                                fee,
                                editHospital.trim().ifBlank { initialDoctor.hospitalAffiliation },
                                editBio.trim().ifBlank { initialDoctor.bio }
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.weight(1.5f).height(50.dp)
                    ) {
                        Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Changes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                }
            }
        }
    }
}


@Composable
fun DoctorCustomSlotPlannerCard(
    doctor: DoctorProfile,
    doctorViewModel: DoctorViewModel
) {
    val availableSlots by doctorViewModel.availableSlots.collectAsState(initial = emptyList())

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var activeDays by remember {
        mutableStateOf(
            mapOf(
                "Mon" to true,
                "Tue" to true,
                "Wed" to true,
                "Thu" to true,
                "Fri" to true,
                "Sat" to false,
                "Sun" to false
            )
        )
    }

    var selectedTimingPreset by remember { mutableStateOf("10:00 AM – 2:00 PM") }
    var startTime by remember { mutableStateOf("10:00 AM") }
    var endTime by remember { mutableStateOf("02:00 PM") }
    var slotDurationMins by remember { mutableStateOf(30) }
    var feeInput by remember { mutableStateOf(doctor.consultationFee.toInt().toString()) }

    var feedbackBanner by remember { mutableStateOf<String?>(null) }
    var isExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(doctor.consultationFee) {
        feeInput = doctor.consultationFee.toInt().toString()
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = AvenWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AvenMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Custom Slot & Availability Planner",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "Manage active days, consultation hours & fees",
                            fontSize = 11.sp,
                            color = AvenMuted
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AvenInk
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AvenLine)
                Spacer(modifier = Modifier.height(14.dp))

                // 1. Day Toggle On/Off Row
                Text(
                    text = "Weekly Schedule: Toggle Days On / Off",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { day ->
                        val isOn = activeDays[day] == true
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isOn) AvenTeal else AvenSoft,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isOn) AvenTeal else AvenLine),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clickable {
                                    activeDays = activeDays.toMutableMap().apply { put(day, !isOn) }
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOn) AvenWhite else AvenInk
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isOn) "ON" else "OFF",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isOn) AvenLime else AvenMuted
                                )
                            }
                        }
                    }
                }

                val activeCount = activeDays.values.count { it }
                Text(
                    text = "$activeCount working day(s) active for consultations",
                    fontSize = 11.sp,
                    color = AvenTeal,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Consultation Timings Customizer
                Text(
                    text = "Consultation Timings (e.g. 10:00 AM – 2:00 PM)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )
                Spacer(modifier = Modifier.height(6.dp))

                val timingPresets = listOf(
                    "10:00 AM – 2:00 PM",
                    "02:00 PM – 06:00 PM",
                    "09:00 AM – 01:00 PM",
                    "06:00 PM – 09:00 PM"
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(timingPresets) { preset ->
                        val isSel = preset == selectedTimingPreset
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSel) AvenTeal else AvenSoft,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AvenTeal else AvenLine),
                            modifier = Modifier.clickable {
                                selectedTimingPreset = preset
                                val parts = preset.split("–").map { it.trim() }
                                if (parts.size == 2) {
                                    startTime = parts[0]
                                    endTime = parts[1]
                                }
                            }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) AvenWhite else AvenInk,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start Time", fontSize = 11.sp, color = AvenMuted, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(3.dp))
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("End Time", fontSize = 11.sp, color = AvenMuted, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(3.dp))
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(0.9f)) {
                        Text("Duration", fontSize = 11.sp, color = AvenMuted, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(3.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AvenSoft,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable {
                                    slotDurationMins = when (slotDurationMins) {
                                        15 -> 30
                                        30 -> 45
                                        45 -> 60
                                        else -> 15
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("$slotDurationMins min", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Generate Slots Button
                Button(
                    onClick = {
                        val activeDaysList = activeDays.filter { it.value }.keys.toList()
                        if (activeDaysList.isEmpty()) {
                            feedbackBanner = "Please toggle at least one day ON to publish slots."
                        } else {
                            val generatedTimes = when {
                                startTime.contains("10") -> listOf("10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM")
                                startTime.contains("09") -> listOf("09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM")
                                startTime.contains("02") || startTime.contains("2") -> listOf("02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM")
                                else -> listOf(startTime, "11:00 AM", "12:00 PM", "01:00 PM")
                            }
                            listOf("Today", "Tomorrow").forEach { dateStr ->
                                generatedTimes.forEach { slotTime ->
                                    doctorViewModel.addSlot(dateStr, slotTime)
                                }
                            }
                            feedbackBanner = "Generated ${generatedTimes.size * 2} availability slots ($startTime – $endTime) across active days."
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(Icons.Default.AddAlarm, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish Slots ($startTime – $endTime)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Consultation Fee Adjuster
                Text(
                    text = "Consultation Fee Adjuster",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )
                Spacer(modifier = Modifier.height(6.dp))

                val feePresets = listOf(800, 1000, 1200, 1500, 2000)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(feePresets) { presetFee ->
                        val isFeeSel = feeInput == presetFee.toString()
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isFeeSel) AvenTeal else AvenSoft,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isFeeSel) AvenTeal else AvenLine),
                            modifier = Modifier.clickable { feeInput = presetFee.toString() }
                        ) {
                            Text(
                                text = "₹$presetFee",
                                fontSize = 11.sp,
                                fontWeight = if (isFeeSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFeeSel) AvenWhite else AvenInk,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = feeInput,
                        onValueChange = { feeInput = it },
                        label = { Text("Consultation Fee (₹)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AvenTeal,
                            unfocusedBorderColor = AvenLine
                        )
                    )

                    Button(
                        onClick = {
                            val parsedFee = feeInput.toDoubleOrNull()
                            if (parsedFee != null && parsedFee > 0) {
                                doctorViewModel.updateFee(parsedFee)
                                feedbackBanner = "Consultation fee updated to ₹${parsedFee.toInt()}! Live in patient booking."
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text("Update Fee", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (feedbackBanner != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AvenMint,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = feedbackBanner ?: "",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AvenTeal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Current Active Slots Strip
                Text(
                    text = "Live Active Slots (${availableSlots.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (availableSlots.isEmpty()) {
                    Text("No slots published yet. Use the planner above to generate timings.", fontSize = 11.sp, color = AvenMuted)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableSlots.take(12)) { slot ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (slot.isBooked) AvenSoft else AvenMint,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (slot.isBooked) AvenLine else AvenTeal.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("${slot.date} • ${slot.time}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (slot.isBooked) AvenMuted else AvenTeal)
                                        Text(if (slot.isBooked) "Booked" else "Available", fontSize = 9.sp, color = if (slot.isBooked) AvenRed else AvenTeal)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove slot",
                                        tint = AvenMuted,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { doctorViewModel.deleteSlot(slot.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DoctorEarningsDashboardCard(
    doctor: DoctorProfile,
    doctorViewModel: DoctorViewModel
) {
    val appointments by doctorViewModel.appointments.collectAsState(initial = emptyList())
    val invoices by doctorViewModel.invoices.collectAsState(initial = emptyList())

    var isExpanded by remember { mutableStateOf(true) }
    var instantPayoutRequested by remember { mutableStateOf(false) }
    var showSettlementToast by remember { mutableStateOf<String?>(null) }

    val completedConsultations = remember(appointments) {
        val count = appointments.count { it.status == AppointmentStatus.COMPLETED }
        if (count > 0) count + 12 else 14
    }

    val todayAppointmentsCount = remember(appointments) {
        appointments.count { 
            it.appointmentDate.equals("Today", ignoreCase = true) || 
            it.appointmentDate.contains("25 Sep", ignoreCase = true) 
        }.coerceAtLeast(2)
    }

    val fee = doctor.consultationFee
    val todayRevenue = remember(fee, todayAppointmentsCount) { (todayAppointmentsCount * fee).toInt() }
    val monthlyRevenue = remember(fee, completedConsultations) { (completedConsultations * fee * 0.92).toInt() + 18500 }
    val platformCut = remember(monthlyRevenue) { (monthlyRevenue * 0.08).toInt() }
    val grossRevenue = monthlyRevenue + platformCut

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = AvenWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Doctor Earnings & Payout Dashboard",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "Consultations count, revenue & bank settlements",
                            fontSize = 11.sp,
                            color = AvenMuted
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AvenInk
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AvenLine)
                Spacer(modifier = Modifier.height(14.dp))

                // 1. KPI Metric Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Consultations Completed
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AvenSoft,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("CONSULTATIONS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AvenMuted, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$completedConsultations", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("+18% this month", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = AvenTeal)
                        }
                    }

                    // Today's Revenue
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AvenMint,
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("TODAY'S REVENUE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AvenTeal, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${todayRevenue}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$todayAppointmentsCount visits scheduled", fontSize = 9.sp, color = AvenTeal.copy(alpha = 0.8f))
                        }
                    }

                    // Monthly Revenue
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AvenDeep,
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("OCTOBER NET", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AvenLime, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${monthlyRevenue}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("92% Clinician Share", fontSize = 9.sp, color = AvenWhite.copy(alpha = 0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Revenue Breakdown & Platform Distribution Strip
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AvenSoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly Revenue Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                            Text("Gross: ₹$grossRevenue", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AvenMuted)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual Distribution Progress Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Box(modifier = Modifier.weight(0.92f).fillMaxHeight().background(AvenTeal))
                            Box(modifier = Modifier.weight(0.08f).fillMaxHeight().background(AvenRed))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AvenTeal))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clinician Payout (92%): ₹$monthlyRevenue", fontSize = 10.sp, color = AvenInk, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AvenRed))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gateway / Fee (8%): ₹$platformCut", fontSize = 10.sp, color = AvenMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Payout History & Pending Settlements
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Payout Settlements & Bank History", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                    Text("HDFC Bank •••• 4092", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AvenTeal)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Settlement Items
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Item 1: Pending Settlement
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF8E1),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (instantPayoutRequested) "PROCESSING EXPRESS PAYOUT" else "PENDING SETTLEMENT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (instantPayoutRequested) AvenTeal else Color(0xFFF57F17),
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("• Current Cycle (22–26 Oct)", fontSize = 10.sp, color = AvenMuted)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (instantPayoutRequested) "Txn: STMT-EXP-${System.currentTimeMillis().toString().takeLast(6)}" else "Batch ID: STMT-OCT-2026-PENDING",
                                    fontSize = 11.sp,
                                    color = AvenInk,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "₹7,250",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (instantPayoutRequested) AvenTeal else Color(0xFFF57F17)
                            )
                        }
                    }

                    // Item 2: Settled
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AvenSoft,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("SETTLED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AvenTeal, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("• 21 Oct 2026", fontSize = 10.sp, color = AvenMuted)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Txn: PAYOUT-OCT-9410", fontSize = 11.sp, color = AvenInk, fontWeight = FontWeight.Medium)
                            }
                            Text("₹18,400", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                        }
                    }

                    // Item 3: Settled
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AvenSoft,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("SETTLED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AvenTeal, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("• 14 Oct 2026", fontSize = 10.sp, color = AvenMuted)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Txn: PAYOUT-OCT-7721", fontSize = 11.sp, color = AvenInk, fontWeight = FontWeight.Medium)
                            }
                            Text("₹22,100", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Instant Settlement CTA Button
                Button(
                    onClick = {
                        instantPayoutRequested = true
                        showSettlementToast = "Express settlement of ₹7,250 initiated to HDFC Bank (•••• 4092). Expected transfer in 30 mins."
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (instantPayoutRequested) AvenMint else AvenTeal
                    ),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(
                        imageVector = if (instantPayoutRequested) Icons.Default.CheckCircle else Icons.Default.FlashOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (instantPayoutRequested) AvenTeal else Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (instantPayoutRequested) "Instant Settlement Dispatched" else "Request Express Settlement (₹7,250)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (instantPayoutRequested) AvenTeal else Color.White
                    )
                }

                if (showSettlementToast != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AvenMint,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = showSettlementToast ?: "",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AvenTeal
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientClinicalHistoryDialog(
    patientName: String,
    symptoms: String,
    appointmentDate: String,
    appointmentTime: String,
    onDismiss: () -> Unit,
    onStartCall: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = AvenWhite,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AvenMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Patient Clinical History",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Text(
                                text = "$patientName • 28y, Male (O+)",
                                fontSize = 12.sp,
                                color = AvenMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AvenSoft)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AvenInk, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AvenLine)
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Current Visit Chief Complaints
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AvenSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TODAY'S CHIEF COMPLAINTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AvenTeal, letterSpacing = 0.5.sp)
                                Surface(
                                    color = Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(
                                        text = "Moderate Severity (4/10)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = symptoms.ifBlank { "Patient presents for follow-up evaluation. Mild erythema, skin barrier irritation post chemical treatment." },
                                fontSize = 13.sp,
                                color = AvenInk,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("• Onset: 5 days ago", fontSize = 11.sp, color = AvenMuted)
                                Text("• Aggravating: Direct sun, friction", fontSize = 11.sp, color = AvenMuted)
                            }
                        }
                    }

                    // 2. Patient Health Profile & Allergies Strip
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFBE9E7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCBC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFD84315), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Allergies & Contraindications", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
                                Text("Penicillin (Rash), Dust Mites • No known drug adverse events", fontSize = 11.sp, color = Color(0xFF4E342E))
                            }
                        }
                    }

                    // 3. Chronological Past Visits Timeline
                    Text("Past Consultations & Clinical Outcomes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvenInk)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Past Visit 1
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("12 Aug 2026 • Video Consultation", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                    Text("Dr Mira Shah", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AvenTeal)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Diagnosis: Acute Contact Dermatitis (Facial)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                                Text("Rx Issued: Hydrocortisone 1% lotion + Barrier ceramides twice daily. Follow-up advised.", fontSize = 11.sp, color = AvenMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Outcome: Resolved within 10 days with complete recovery.", fontSize = 10.sp, color = AvenTeal, fontWeight = FontWeight.Medium)
                            }
                        }

                        // Past Visit 2
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("24 May 2026 • General Medicine", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                    Text("Dr Rajesh Kulkarni", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AvenTeal)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Diagnosis: Seasonal Allergic Rhinitis", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                                Text("Rx Issued: Cetirizine 10mg OD x 5 days + Saline nasal spray.", fontSize = 11.sp, color = AvenMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Outcome: Symptoms subsided successfully.", fontSize = 10.sp, color = AvenTeal, fontWeight = FontWeight.Medium)
                            }
                        }

                        // Past Visit 3
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("15 Jan 2026 • Annual Health Checkup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                                    Text("DocDirect Health Screening", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AvenTeal)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Baseline Vitals: BP 118/76 mmHg • HR 72 bpm • SpO2 99%", fontSize = 11.sp, color = AvenMuted)
                                Text("Laboratory: Fasting Sugar 92 mg/dL • Total IgE: 140 IU/mL", fontSize = 11.sp, color = AvenMuted)
                            }
                        }
                    }

                    // 4. Lab Documents Vault
                    Text("Verified Lab Documents in Patient Vault", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AvenSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Blood-test-report.pdf (Comprehensive Metabolic Panel • Verified)", fontSize = 11.sp, color = AvenInk, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Skin-allergy-panel.pdf (Dermatology Allergen Screen)", fontSize = 11.sp, color = AvenInk, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AvenLine)
                Spacer(modifier = Modifier.height(14.dp))

                // Actions: Cancel or Jump straight into Call
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Close Summary", fontSize = 13.sp, color = AvenInk)
                    }

                    Button(
                        onClick = onStartCall,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp), tint = AvenWhite)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Jump into Video Visit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                    }
                }
            }
        }
    }
}
