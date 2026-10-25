package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.auth.AuthViewModel
import com.docdirect.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvenDoctorProfileSetupScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var license by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("Dermatology") }
    var qualification by remember { mutableStateOf("MBBS, MD") }
    var experienceStr by remember { mutableStateOf("7") }
    var feeStr by remember { mutableStateOf("999") }
    var hospital by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val specialties = listOf(
        "Dermatology", "Cardiology", "Pediatrics", "General Medicine",
        "Neurology", "Orthopedics", "Gynecology", "ENT", "Psychiatry"
    )

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
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
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AvenInk,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Clinician Registration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )
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
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Hero Header Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = AvenDeep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AvenLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = AvenDeep, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Set Up Your Clinical Profile",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AvenWhite
                                )
                                Text(
                                    text = "Join the verified clinician network",
                                    fontSize = 12.sp,
                                    color = AvenLime
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Provide your credentials and practice details. Your profile will be instantly visible to patients seeking telehealth and in-clinic consultations.",
                            fontSize = 13.sp,
                            color = AvenWhite.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Professional Details Section
            item {
                Text(
                    text = "Personal & Medical Credentials",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AvenInk
                )
            }

            // Name
            item {
                Text("Doctor Full Name", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; validationError = null },
                    placeholder = { Text("e.g. Dr. Aryan Varma", color = AvenMuted, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AvenMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine
                    ),
                    singleLine = true
                )
            }

            // Email & Password
            item {
                Text("Email Address", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; validationError = null },
                    placeholder = { Text("e.g. dr.aryan@clinic.com", color = AvenMuted, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AvenMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
            }

            item {
                Text("Account Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; validationError = null },
                    placeholder = { Text("Create strong password (6+ chars)", color = AvenMuted, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AvenMuted) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = AvenMuted
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
            }

            // License
            item {
                Text("Medical Registration / License Number", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = license,
                    onValueChange = { license = it; validationError = null },
                    placeholder = { Text("e.g. MCI-78492-MED", color = AvenMuted, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null, tint = AvenMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine
                    ),
                    singleLine = true
                )
            }

            // Specialty Selector Chips
            item {
                Text("Primary Specialty", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(specialties) { spec ->
                        val isSelected = spec.equals(specialty, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) AvenTeal else AvenWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AvenTeal else AvenLine),
                            modifier = Modifier.clickable { specialty = spec }
                        ) {
                            Text(
                                text = spec,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AvenWhite else AvenInk,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Qualification & Experience
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("Degrees / Qualification", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = qualification,
                            onValueChange = { qualification = it },
                            placeholder = { Text("e.g. MBBS, MD, DNB", color = AvenMuted, fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AvenWhite,
                                unfocusedContainerColor = AvenWhite,
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            ),
                            singleLine = true
                        )
                    }

                    Column(modifier = Modifier.weight(0.7f)) {
                        Text("Exp (Yrs)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = experienceStr,
                            onValueChange = { experienceStr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AvenWhite,
                                unfocusedContainerColor = AvenWhite,
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Fee & Hospital
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(0.9f)) {
                        Text("Fee (₹)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = feeStr,
                            onValueChange = { feeStr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AvenWhite,
                                unfocusedContainerColor = AvenWhite,
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            ),
                            singleLine = true
                        )
                    }

                    Column(modifier = Modifier.weight(1.5f)) {
                        Text("Hospital / Clinic Affiliation", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = hospital,
                            onValueChange = { hospital = it },
                            placeholder = { Text("e.g. City Health Clinic", color = AvenMuted, fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AvenWhite,
                                unfocusedContainerColor = AvenWhite,
                                focusedBorderColor = AvenTeal,
                                unfocusedBorderColor = AvenLine
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Bio
            item {
                Text("Professional Summary / Bio", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = { Text("Brief description of clinical experience, patient care focus, and expertise...", color = AvenMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine
                    )
                )
            }

            // Errors
            val currentError = validationError ?: errorMessage
            if (currentError != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AvenRose,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentError,
                            color = AvenRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        val trimmedName = name.trim()
                        val formattedName = if (trimmedName.startsWith("Dr", ignoreCase = true)) trimmedName else "Dr. $trimmedName"
                        val exp = experienceStr.toIntOrNull() ?: 5
                        val fee = feeStr.toDoubleOrNull() ?: 999.0

                        if (trimmedName.isBlank()) {
                            validationError = "Please enter your doctor name."
                            return@Button
                        }
                        if (email.isBlank() || !email.contains("@")) {
                            validationError = "Please enter a valid email address."
                            return@Button
                        }
                        if (password.length < 6) {
                            validationError = "Password must be at least 6 characters."
                            return@Button
                        }
                        if (license.isBlank()) {
                            validationError = "Please provide your medical license number."
                            return@Button
                        }

                        authViewModel.registerDoctor(
                            name = formattedName,
                            email = email,
                            password = password,
                            license = license,
                            specialty = specialty,
                            qualification = qualification.ifBlank { "MBBS, MD" },
                            experienceYears = exp,
                            fee = fee,
                            bio = bio.ifBlank { "Experienced clinician specializing in $specialty." },
                            hospital = hospital.ifBlank { "DocDirect Partner Clinic" },
                            onSuccess = onSignupSuccess
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = AvenWhite, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "Save & Launch Clinician Workspace",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenWhite
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(28.dp)) }
        }
    }
}
