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

                        Spacer(modifier = Modifier.height(18.dp))

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
