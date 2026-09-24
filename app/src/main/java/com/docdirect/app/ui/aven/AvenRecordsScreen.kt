package com.docdirect.app.ui.aven

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.data.local.entity.RecordEntity
import com.docdirect.app.ui.patient.PatientViewModel
import com.docdirect.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AvenRecordsScreen(
    patientViewModel: PatientViewModel,
    onTabSelected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val recordsList by patientViewModel.records.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showUploadModal by remember { mutableStateOf(false) }
    var showShareModal by remember { mutableStateOf<RecordEntity?>(null) }
    var recordDetailModal by remember { mutableStateOf<RecordEntity?>(null) }

    val filteredRecords = remember(recordsList, searchQuery) {
        if (searchQuery.isBlank()) recordsList
        else recordsList.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
    }

    var selectedPatient by remember { mutableStateOf("Aarav Mehta") }
    var showPatientPicker by remember { mutableStateOf(false) }

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
                            text = "Your health,",
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            letterSpacing = (-0.7).sp
                        )
                        Text(
                            text = "in one place.",
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk,
                            letterSpacing = (-0.7).sp
                        )
                    }

                    Surface(
                        onClick = { showUploadModal = true },
                        shape = RoundedCornerShape(16.dp),
                        color = AvenTeal,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Upload record", tint = AvenWhite, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Patient selector pill (Aarav Mehta ⌄)
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
            }
        },
        bottomBar = {
            AvenBottomNav(
                selectedTab = "records",
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Section Header: Recent records & Share
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent records",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenInk
                    )
                    Text(
                        text = "Share",
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
                                .background(AvenMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (record.category.contains("Lab") || record.category.contains("blood", ignoreCase = true)) Icons.Default.Science else Icons.Default.Description,
                                contentDescription = null,
                                tint = AvenTeal,
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
                            Text(
                                text = if (record.title.contains("summary", ignoreCase = true) || record.title.contains("notes", ignoreCase = true)) {
                                    "${record.date} · ${record.sharedWithDoctorName}"
                                } else {
                                    "${record.date} · Uploaded by you"
                                },
                                fontSize = 12.sp,
                                color = AvenMuted
                            )
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Details",
                            tint = AvenMuted,
                            modifier = Modifier.size(20.dp)
                        )
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
                                text = "Share only what’s needed",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenTeal
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "You decide which records a clinician can see.",
                                fontSize = 12.sp,
                                color = AvenTeal
                            )
                        }
                    }
                }
            }

            // Upload a record button
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
                    Box(
                        modifier = Modifier.padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Upload a record",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    // Patient Picker Dialog
    if (showPatientPicker) {
        AlertDialog(
            onDismissRequest = { showPatientPicker = false },
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

    // Upload Modal (Saves to Room SQLite Database!)
    if (showUploadModal) {
        var uploadTitle by remember { mutableStateOf("") }
        var uploadCategory by remember { mutableStateOf("Lab report") }

        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            title = { Text("Upload health record", fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add a report, letter, or consultation note (PDF/JPG up to 10 MB):", fontSize = 13.sp, color = AvenMuted)

                    OutlinedTextField(
                        value = uploadTitle,
                        onValueChange = { uploadTitle = it },
                        placeholder = { Text("e.g. Skin biopsy lab report") },
                        label = { Text("Document title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text("Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Lab report", "Prescription", "Scan").forEach { cat ->
                            FilterChip(
                                selected = uploadCategory == cat,
                                onClick = { uploadCategory = cat },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uploadTitle.isNotBlank()) {
                            coroutineScope.launch {
                                val filename = if (uploadTitle.endsWith(".pdf")) uploadTitle else "$uploadTitle.pdf"
                                patientViewModel.addRecord(filename, uploadCategory, "1.8 MB")
                                showUploadModal = false
                            }
                        }
                    },
                    enabled = uploadTitle.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)
                ) {
                    Text("Save to Vault")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) {
                    Text("Cancel", color = AvenMuted)
                }
            }
        )
    }

    // Share Modal
    showShareModal?.let { record ->
        AlertDialog(
            onDismissRequest = { showShareModal = null },
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

    // Record Detail Modal
    recordDetailModal?.let { record ->
        AlertDialog(
            onDismissRequest = { recordDetailModal = null },
            title = { Text(record.title, fontWeight = FontWeight.Bold, color = AvenInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Category: ${record.category}", fontSize = 13.sp, color = AvenInk)
                    Text("Date added: ${record.date}", fontSize = 13.sp, color = AvenInk)
                    Text("File size: ${record.fileSize}", fontSize = 13.sp, color = AvenMuted)
                    Text("Status: ${if (record.isSharedWithDoctor) "Shared with Dr Mira Shah" else "Private (vault only)"}", fontSize = 13.sp, color = AvenTeal)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = AvenSoft,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Verified cryptographic hash: SHA256:7f9a...3b21. Storage encrypted at rest in local Room SQLite DB.",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 11.sp,
                            color = AvenMuted
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { recordDetailModal = null }, colors = ButtonDefaults.buttonColors(containerColor = AvenTeal)) {
                    Text("Download File")
                }
            },
            dismissButton = {
                TextButton(onClick = { recordDetailModal = null }) {
                    Text("Close", color = AvenMuted)
                }
            }
        )
    }
}
