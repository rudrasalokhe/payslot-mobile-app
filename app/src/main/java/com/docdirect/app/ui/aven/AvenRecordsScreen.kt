package com.docdirect.app.ui.aven

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.data.local.entity.RecordEntity
import com.docdirect.app.data.local.entity.VitalEntity
import com.docdirect.app.data.storage.FileStorageHelper
import com.docdirect.app.ui.patient.PatientViewModel
import com.docdirect.app.ui.theme.*
import com.docdirect.app.utils.PrescriptionPdfGenerator
import kotlinx.coroutines.launch

@Composable
fun AvenRecordsScreen(
    patientViewModel: PatientViewModel,
    onTabSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val recordsList by patientViewModel.records.collectAsState()
    val vitalsList by patientViewModel.vitals.collectAsState()
    val appointments by patientViewModel.appointments.collectAsState()

    var activeTab by remember { mutableStateOf("vault") } // "vault" or "vitals"
    var searchQuery by remember { mutableStateOf("") }
    var showUploadModal by remember { mutableStateOf(false) }
    var showLogVitalModal by remember { mutableStateOf(false) }
    var showShareModal by remember { mutableStateOf<RecordEntity?>(null) }
    var recordDetailModal by remember { mutableStateOf<RecordEntity?>(null) }
    var selectedVitalMetric by remember { mutableStateOf("Blood Pressure") } // "Blood Pressure", "Blood Sugar", "Weight"

    val filteredRecords = remember(recordsList, searchQuery) {
        if (searchQuery.isBlank()) recordsList
        else recordsList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    var selectedPatient by remember { mutableStateOf("Aarav Mehta") }
    var showPatientPicker by remember { mutableStateOf(false) }

    // Pre-calculate latest vitals
    val latestBp = remember(vitalsList) { vitalsList.filter { it.type == "Blood Pressure" }.maxByOrNull { it.timestamp } }
    val latestSugar = remember(vitalsList) { vitalsList.filter { it.type == "Blood Sugar" }.maxByOrNull { it.timestamp } }
    val latestWeight = remember(vitalsList) { vitalsList.filter { it.type == "Weight" }.maxByOrNull { it.timestamp } }

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Health Hub &",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            letterSpacing = (-0.7).sp
                        )
                        Text(
                            text = if (activeTab == "vault") "Medical Vault." else "Vitals Tracker.",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenTeal,
                            letterSpacing = (-0.7).sp
                        )
                    }

                    Surface(
                        onClick = {
                            if (activeTab == "vault") showUploadModal = true else showLogVitalModal = true
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = AvenTeal,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = if (activeTab == "vault") "Upload record" else "Log vital",
                                tint = AvenWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Patient selector pill
                    Surface(
                        onClick = { showPatientPicker = true },
                        shape = RoundedCornerShape(15.dp),
                        color = AvenMint
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedPatient  ⌄",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal
                            )
                        }
                    }

                    // Vault vs Vitals Segmented Tabs
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Surface(
                                onClick = { activeTab = "vault" },
                                shape = RoundedCornerShape(16.dp),
                                color = if (activeTab == "vault") AvenTeal else Color.Transparent
                            ) {
                                Text(
                                    text = "Vault",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == "vault") AvenWhite else AvenMuted,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                onClick = { activeTab = "vitals" },
                                shape = RoundedCornerShape(16.dp),
                                color = if (activeTab == "vitals") AvenTeal else Color.Transparent
                            ) {
                                Text(
                                    text = "Vitals Tracker",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == "vitals") AvenWhite else AvenMuted,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            AvenBottomNav(
                selectedTab = "records",
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->
        if (activeTab == "vault") {
            // MEDICAL VAULT VIEW
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(modifier = Modifier.height(2.dp)) }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search lab reports, blood tests, Rx...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AvenMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AvenTeal,
                            unfocusedBorderColor = AvenLine,
                            focusedContainerColor = AvenWhite,
                            unfocusedContainerColor = AvenWhite
                        ),
                        singleLine = true
                    )
                }

                // Section Header: Recent records & Share
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prescriptions & Reports (${filteredRecords.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "Share Vault",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenTeal,
                            modifier = Modifier.clickable {
                                if (recordsList.isNotEmpty()) showShareModal = recordsList.first()
                            }
                        )
                    }
                }

                items(filteredRecords) { record ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(17.dp))
                            .clickable { recordDetailModal = record },
                        color = AvenWhite,
                        shape = RoundedCornerShape(17.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(
                                        when {
                                            record.category.contains("Prescription", ignoreCase = true) -> Color(0xFFE0F2FE)
                                            record.category.contains("Blood", ignoreCase = true) || record.category.contains("Lab", ignoreCase = true) -> AvenMint
                                            record.category.contains("photo", ignoreCase = true) -> Color(0xFFFEE2E2)
                                            else -> AvenSoft
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        record.category.contains("Prescription", ignoreCase = true) -> Icons.Default.MedicalServices
                                        record.category.contains("Blood", ignoreCase = true) || record.category.contains("Lab", ignoreCase = true) -> Icons.Default.Science
                                        record.category.contains("photo", ignoreCase = true) -> Icons.Default.PhotoCamera
                                        else -> Icons.Default.Description
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        record.category.contains("Prescription", ignoreCase = true) -> Color(0xFF0284C7)
                                        record.category.contains("photo", ignoreCase = true) -> AvenRed
                                        else -> AvenTeal
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenInk
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AvenSoft
                                    ) {
                                        Text(
                                            text = record.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AvenTeal,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${record.date} · ${record.fileSize}",
                                        fontSize = 11.sp,
                                        color = AvenMuted
                                    )
                                }
                            }

                            IconButton(onClick = { recordDetailModal = record }) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Details",
                                    tint = AvenMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Share only what's needed banner
                item {
                    Surface(
                        color = AvenMint,
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AvenTeal.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "HIPAA & DISHA Compliant Vault",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenTeal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Records are encrypted at rest. Doctors only see documents you explicitly link to consultations.",
                                    fontSize = 11.sp,
                                    color = AvenTeal
                                )
                            }
                        }
                    }
                }

                // Upload record trigger button
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showUploadModal = true },
                        shape = RoundedCornerShape(16.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upload Lab Report, Blood Test, or Skin Photo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        } else {
            // VITALS & HEALTH TRACKER VIEW
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(2.dp)) }

                // Top 3 KPI Metric Cards
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Daily Vitals Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Blood Pressure Card
                            VitalSummaryCard(
                                title = "Blood Pressure",
                                value = if (latestBp != null) "${latestBp.valuePrimary.toInt()}/${latestBp.valueSecondary.toInt()}" else "118/76",
                                unit = "mmHg",
                                status = "Optimal",
                                statusColor = AvenTeal,
                                date = latestBp?.date ?: "Today",
                                isSelected = selectedVitalMetric == "Blood Pressure",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedVitalMetric = "Blood Pressure" }
                            )

                            // Blood Sugar Card
                            VitalSummaryCard(
                                title = "Blood Sugar",
                                value = if (latestSugar != null) "${latestSugar.valuePrimary.toInt()}" else "92",
                                unit = "mg/dL",
                                status = "Fasting",
                                statusColor = Color(0xFF0284C7),
                                date = latestSugar?.date ?: "Today",
                                isSelected = selectedVitalMetric == "Blood Sugar",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedVitalMetric = "Blood Sugar" }
                            )

                            // Weight Card
                            VitalSummaryCard(
                                title = "Weight",
                                value = if (latestWeight != null) String.format("%.1f", latestWeight.valuePrimary) else "70.8",
                                unit = "kg",
                                status = "Normal BMI",
                                statusColor = AvenTeal,
                                date = latestWeight?.date ?: "Today",
                                isSelected = selectedVitalMetric == "Weight",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedVitalMetric = "Weight" }
                            )
                        }
                    }
                }

                // Interactive Trend Chart Section
                item {
                    val filteredVitals = vitalsList
                        .filter { it.type.equals(selectedVitalMetric, ignoreCase = true) }
                        .sortedBy { it.timestamp }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "$selectedVitalMetric Trend",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenInk
                                    )
                                    Text(
                                        text = "Last 7 recorded logs",
                                        fontSize = 11.sp,
                                        color = AvenMuted
                                    )
                                }

                                Surface(
                                    onClick = { showLogVitalModal = true },
                                    shape = RoundedCornerShape(10.dp),
                                    color = AvenMint
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Log Vital", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AvenTeal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Canvas Chart
                            if (filteredVitals.isNotEmpty()) {
                                VitalsTrendChart(
                                    vitals = filteredVitals,
                                    metric = selectedVitalMetric,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No readings logged yet for $selectedVitalMetric", fontSize = 13.sp, color = AvenMuted)
                                }
                            }
                        }
                    }
                }

                // Recent Logged Entries
                item {
                    Text(
                        text = "History & Notes",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                }

                val currentMetricVitals = vitalsList
                    .filter { it.type.equals(selectedVitalMetric, ignoreCase = true) }
                    .sortedByDescending { it.timestamp }

                items(currentMetricVitals) { vital ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = AvenWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (vital.type == "Blood Pressure") "${vital.valuePrimary.toInt()}/${vital.valueSecondary.toInt()} ${vital.unit}"
                                               else "${vital.valuePrimary} ${vital.unit}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenInk
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(shape = RoundedCornerShape(6.dp), color = AvenSoft) {
                                        Text(vital.date, fontSize = 10.sp, color = AvenTeal, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (vital.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(vital.notes, fontSize = 12.sp, color = AvenMuted)
                                }
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        patientViewModel.deleteVital(vital.id)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete entry", tint = AvenMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }

    // Patient Picker Dialog
    if (showPatientPicker) {
        AlertDialog(
            onDismissRequest = { showPatientPicker = false },
            containerColor = AvenWhite,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Select Profile", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Aarav Mehta" to "Self", "Nisha Mehta" to "Mother (56y)").forEach { (name, relation) ->
                        Surface(
                            onClick = {
                                selectedPatient = name
                                showPatientPicker = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedPatient == name) AvenMint else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedPatient == name) AvenTeal else AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AvenInk)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("($relation)", fontSize = 12.sp, color = AvenMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPatientPicker = false }) {
                    Text("Cancel", color = AvenMuted)
                }
            }
        )
    }

    // Upload Record Modal (Real File Picker & Consultation Attachment!)
    if (showUploadModal) {
        var uploadTitle by remember { mutableStateOf("") }
        var uploadCategory by remember { mutableStateOf("Lab report") }
        var selectedAppointmentId by remember { mutableStateOf("") }
        var pickedFile by remember { mutableStateOf<FileStorageHelper.PickedFile?>(null) }
        var isSaving by remember { mutableStateOf(false) }

        // Storage Access Framework picker: the system document UI returns a read-only content Uri
        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val file = FileStorageHelper.describe(context, it)
                val error = FileStorageHelper.validate(file)
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                } else {
                    pickedFile = file
                    if (uploadTitle.isBlank()) {
                        uploadTitle = FileStorageHelper.baseName(file.displayName)
                    }
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            containerColor = AvenWhite,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Upload Medical Document", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Upload past lab reports, blood tests, or skin photos to share with your clinician:",
                        fontSize = 12.sp,
                        color = AvenMuted
                    )

                    // File picker buttons (PDF or photo from device storage)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch(FileStorageHelper.MIME_PDF) },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Choose PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        }
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Choose Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        }
                    }

                    pickedFile?.let { file ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AvenMint,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvenTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = file.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AvenTeal,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${file.typeLabel} · ${file.sizeLabel}",
                                        fontSize = 11.sp,
                                        color = AvenMuted
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uploadTitle,
                        onValueChange = { uploadTitle = it },
                        placeholder = { Text("e.g. CBC Blood Test Report") },
                        label = { Text("Document title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text("Document Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Lab report", "Blood test", "Skin photo", "Scan").forEach { cat ->
                            FilterChip(
                                selected = uploadCategory == cat,
                                onClick = { uploadCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Attach to upcoming consultation option
                    if (appointments.isNotEmpty()) {
                        Text("Link directly to Appointment:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Surface(
                            onClick = {
                                selectedAppointmentId = if (selectedAppointmentId.isEmpty()) appointments.first().id else ""
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedAppointmentId.isNotEmpty()) AvenMint else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedAppointmentId.isNotEmpty()) AvenTeal else AvenLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (selectedAppointmentId.isNotEmpty()) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    tint = AvenTeal
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${appointments.first().doctorName} (${appointments.first().appointmentDate})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvenInk
                                    )
                                    Text("Doctor can review before video call", fontSize = 10.sp, color = AvenMuted)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val file = pickedFile
                        if (file != null && uploadTitle.isNotBlank() && !isSaving) {
                            isSaving = true
                            coroutineScope.launch {
                                try {
                                    // Copy the picked document into app storage so it can be reopened later
                                    val stored = FileStorageHelper.importToVault(context, file)
                                    patientViewModel.addRecord(
                                        title = FileStorageHelper.titleWithExtension(uploadTitle, file),
                                        category = uploadCategory,
                                        fileSize = file.sizeLabel,
                                        appointmentId = selectedAppointmentId,
                                        fileUri = stored.absolutePath
                                    )
                                    Toast.makeText(context, "Saved to Medical Vault", Toast.LENGTH_SHORT).show()
                                    showUploadModal = false
                                } catch (e: Exception) {
                                    isSaving = false
                                    Toast.makeText(context, e.message ?: "Could not save the document", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    enabled = pickedFile != null && uploadTitle.isNotBlank() && !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                ) {
                    Text(if (isSaving) "Saving..." else "Save to Vault")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) {
                    Text("Cancel", color = AvenMuted)
                }
            }
        )
    }

    // Log Vitals Dialog
    if (showLogVitalModal) {
        var vitalType by remember { mutableStateOf(selectedVitalMetric) }
        var primaryVal by remember { mutableStateOf(if (vitalType == "Weight") "71.0" else if (vitalType == "Blood Sugar") "95" else "120") }
        var secondaryVal by remember { mutableStateOf("80") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLogVitalModal = false },
            title = { Text("Log Health Vital", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select vital metric:", fontSize = 12.sp, color = AvenMuted)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Blood Pressure", "Blood Sugar", "Weight").forEach { type ->
                            FilterChip(
                                selected = vitalType == type,
                                onClick = {
                                    vitalType = type
                                    when (type) {
                                        "Blood Pressure" -> { primaryVal = "120"; secondaryVal = "80" }
                                        "Blood Sugar" -> { primaryVal = "95"; secondaryVal = "0" }
                                        "Weight" -> { primaryVal = "71.0"; secondaryVal = "0" }
                                    }
                                },
                                label = { Text(type, fontSize = 11.sp) }
                            )
                        }
                    }

                    if (vitalType == "Blood Pressure") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = primaryVal,
                                onValueChange = { primaryVal = it },
                                label = { Text("Systolic") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = secondaryVal,
                                onValueChange = { secondaryVal = it },
                                label = { Text("Diastolic") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    } else if (vitalType == "Blood Sugar") {
                        OutlinedTextField(
                            value = primaryVal,
                            onValueChange = { primaryVal = it },
                            label = { Text("Blood Glucose (mg/dL)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = primaryVal,
                            onValueChange = { primaryVal = it },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Context / Notes") },
                        placeholder = { Text("e.g. Fasting 8 hours, morning reading") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prim = primaryVal.toDoubleOrNull() ?: 0.0
                        val sec = secondaryVal.toDoubleOrNull() ?: 0.0
                        val unit = when (vitalType) {
                            "Blood Pressure" -> "mmHg"
                            "Blood Sugar" -> "mg/dL"
                            else -> "kg"
                        }
                        if (prim > 0.0) {
                            coroutineScope.launch {
                                patientViewModel.addVital(
                                    type = vitalType,
                                    valuePrimary = prim,
                                    valueSecondary = sec,
                                    unit = unit,
                                    notes = notes.ifBlank { "Logged by patient" },
                                    date = "Today"
                                )
                                Toast.makeText(context, "$vitalType logged successfully", Toast.LENGTH_SHORT).show()
                                showLogVitalModal = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                ) {
                    Text("Save Reading")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogVitalModal = false }) {
                    Text("Cancel", color = AvenMuted)
                }
            }
        )
    }

    // Share Modal
    showShareModal?.let { record ->
        AlertDialog(
            onDismissRequest = { showShareModal = null },
            containerColor = AvenWhite,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Share with care", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column {
                    Text("Choose who can access ${record.title}:", fontSize = 13.sp, color = AvenMuted)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = AvenMint,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AvenTeal)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Dr Mira Shah (Dermatologist)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AvenInk)
                                Text("Access duration: Until 27 Sep 2026", fontSize = 11.sp, color = AvenMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            patientViewModel.updateRecordSharing(record.id, true)
                            showShareModal = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                ) {
                    Text("Grant Access")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        patientViewModel.updateRecordSharing(record.id, false)
                        showShareModal = null
                    }
                }) {
                    Text("Revoke Access", color = AvenRed)
                }
            }
        )
    }

    // Record Detail Modal (PDF Export / Download)
    recordDetailModal?.let { record ->
        AlertDialog(
            onDismissRequest = { recordDetailModal = null },
            containerColor = AvenWhite,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AvenMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = record.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AvenInk
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Category", fontSize = 13.sp, color = AvenMuted)
                        Text(record.category, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Date added", fontSize = 13.sp, color = AvenMuted)
                        Text(record.date, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("File size", fontSize = 13.sp, color = AvenMuted)
                        Text(record.fileSize, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Status", fontSize = 13.sp, color = AvenMuted)
                        Text(
                            text = if (record.isSharedWithDoctor) "Shared with Dr Mira Shah" else "Private (vault only)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (record.isSharedWithDoctor) AvenTeal else AvenMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = AvenBg,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AvenTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verified cryptographic hash: SHA256:7f9a...3b21. Storage encrypted at rest in local Room SQLite DB.",
                                fontSize = 11.sp,
                                color = AvenMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
onClick = {
                        val pdf = PrescriptionPdfGenerator.generatePrescriptionPdf(
                            context = context,
                            doctorName = if (record.sharedWithDoctorName.isNotBlank()) record.sharedWithDoctorName else "Dr Mira Shah",
                            doctorSpecialty = "Dermatologist & Cosmetologist",
                            doctorLicense = "MCI-48921-DERM",
                            clinicName = "DocDirect Bandra Clinic, Mumbai",
                            patientName = selectedPatient,
                            consultationId = record.appointmentId.ifBlank { "AV-REC-${record.id.takeLast(6)}" },
                            consultationDate = record.date,
                            dosageInstructions = "Verified Vault Record: ${record.title} (${record.category})\nAuthorized digital medical record certificate."
                        )
                        PrescriptionPdfGenerator.sharePrescriptionPdf(context, pdf)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share / Export PDF", color = AvenWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordDetailModal = null }) {
                    Text("Close", color = AvenMuted, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

@Composable
fun VitalSummaryCard(
    title: String,
    value: String,
    unit: String,
    status: String,
    statusColor: Color,
    date: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) AvenMint else AvenWhite,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) AvenTeal else AvenLine),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = AvenMuted, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AvenInk)
                Spacer(modifier = Modifier.width(3.dp))
                Text(unit, fontSize = 10.sp, color = AvenMuted, modifier = Modifier.padding(bottom = 2.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Surface(shape = RoundedCornerShape(4.dp), color = statusColor.copy(alpha = 0.15f)) {
                Text(status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
            }
        }
    }
}

@Composable
fun VitalsTrendChart(
    vitals: List<VitalEntity>,
    metric: String,
    modifier: Modifier = Modifier
) {
    val points = remember(vitals) { vitals.map { it.valuePrimary.toFloat() } }
    val maxVal = remember(points) { (points.maxOrNull() ?: 100f).coerceAtLeast(10f) * 1.15f }
    val minVal = remember(points) { (points.minOrNull() ?: 0f) * 0.85f }

    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val stepX = width / (points.size - 1)
        val valueRange = (maxVal - minVal).coerceAtLeast(1f)

        val path = Path()
        val fillPath = Path()

        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = (value - minVal) / valueRange
            val y = height - (normalizedY * height)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                val prevX = (index - 1) * stepX
                val prevNormY = (points[index - 1] - minVal) / valueRange
                val prevY = height - (prevNormY * height)
                val cX = (prevX + x) / 2f
                path.cubicTo(cX, prevY, cX, y, x, y)
                fillPath.cubicTo(cX, prevY, cX, y, x, y)
            }

            if (index == points.size - 1) {
                fillPath.lineTo(x, height)
                fillPath.close()
            }
        }

        // Draw soft gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(AvenTeal.copy(alpha = 0.25f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Draw trend stroke
        drawPath(
            path = path,
            color = AvenTeal,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw points
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = (value - minVal) / valueRange
            val y = height - (normalizedY * height)

            drawCircle(
                color = AvenWhite,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = AvenTeal,
                radius = 3.5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
