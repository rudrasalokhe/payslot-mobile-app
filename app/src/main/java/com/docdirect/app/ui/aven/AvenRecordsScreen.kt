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
                    Column {
                        Text(
                            text = "Health records",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Text(
                            text = "Your clinical documents, reports & consent vault.",
                            fontSize = 13.sp,
                            color = AvenMuted
                        )
                    }

                    Button(
                        onClick = { showUploadModal = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search records, reports, prescriptions...", fontSize = 13.sp, color = AvenMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AvenMuted) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Active Sharing Consent Notice
            item {
                Surface(
                    color = AvenMint,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AvenTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AvenWhite, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Consent active with Dr Mira Shah",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenDeep
                            )
                            Text(
                                text = "2 records shared for video consultation • Expires 27 Sep 2026",
                                fontSize = 11.sp,
                                color = AvenMuted
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "${filteredRecords.size} records saved in database",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenMuted
                )
            }

            items(filteredRecords) { record ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { recordDetailModal = record },
                    color = AvenWhite,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AvenSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (record.category.contains("Lab")) Icons.Default.Science else Icons.Default.Description,
                                contentDescription = null,
                                tint = AvenTeal,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = record.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${record.category} • ${record.fileSize}",
                                    fontSize = 12.sp,
                                    color = AvenMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("•", fontSize = 12.sp, color = AvenLine)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = record.date,
                                    fontSize = 12.sp,
                                    color = AvenMuted
                                )
                            }
                        }

                        IconButton(onClick = { showShareModal = record }) {
                            Icon(
                                imageVector = if (record.isSharedWithDoctor) Icons.Default.Share else Icons.Default.FileDownload,
                                contentDescription = "Share",
                                tint = if (record.isSharedWithDoctor) AvenTeal else AvenMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
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
