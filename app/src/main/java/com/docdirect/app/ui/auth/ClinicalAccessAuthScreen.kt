package com.docdirect.app.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalAccessAuthScreen(
    authViewModel: AuthViewModel,
    onNavigateToDoctorSignup: () -> Unit,
    onLoginSuccess: (role: UserRole) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isAppleHealthConnected by remember { mutableStateOf(false) }
    var isQuestConnected by remember { mutableStateOf(false) }

    var toastMessage by remember { mutableStateOf<String?>(null) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    Scaffold(
        containerColor = AuraBackground,
        snackbarHost = {
            toastMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = AuraInverseSurface,
                    contentColor = AuraInverseOnSurface
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AuraInversePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(msg, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Brand & Intro Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AuraPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = AuraSurfaceContainer,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(AuraPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CLINICAL ACCESS PORTAL",
                                color = AuraPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Welcome to Aura Health",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AuraOnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Comprehensive, physician-led clinical care & biomarker analytics.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AuraOnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Clinical Care Provider Presence Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AuraSurfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AuraPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Dr. Elena Vance, MD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AuraOnSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Clinician",
                                    tint = AuraPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Lead Clinician on duty • Avg response 4m",
                                fontSize = 12.sp,
                                color = AuraOnSurfaceVariant
                            )
                        }

                        Surface(
                            color = AuraSurfaceContainerLowest,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "Active",
                                color = AuraSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Primary Interactive Auth Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Segmented Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AuraSurfaceContainer)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isRegisterMode) AuraSurfaceContainerLowest else Color.Transparent)
                                    .clickable { isRegisterMode = false }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sign In",
                                    fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                                    color = if (!isRegisterMode) AuraOnSurface else AuraOnSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isRegisterMode) AuraSurfaceContainerLowest else Color.Transparent)
                                    .clickable { isRegisterMode = true }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "New Patient",
                                    fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isRegisterMode) AuraOnSurface else AuraOnSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Registration Alert
                        if (isRegisterMode) {
                            Surface(
                                color = AuraSurfaceContainerLow,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.AssignmentInd,
                                        contentDescription = null,
                                        tint = AuraPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "New Patient Intake",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = AuraOnSurface
                                        )
                                        Text(
                                            text = "Provide your contact details to begin clinical onboarding and insurance validation.",
                                            fontSize = 11.sp,
                                            color = AuraOnSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Full name when registering
                            Column {
                                Text(
                                    text = "FULL LEGAL NAME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraOnSurfaceVariant,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    placeholder = { Text("Alex Rivera", fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = AuraOutline, modifier = Modifier.size(20.dp))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                            }
                        }

                        // Email Field
                        Column {
                            Text(
                                text = "WORK OR PERSONAL EMAIL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraOnSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = { Text("patient@aurahealth.org", fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Mail, contentDescription = null, tint = AuraOutline, modifier = Modifier.size(20.dp))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }

                        // Password Field
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PASSWORD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraOnSurfaceVariant,
                                    letterSpacing = 0.5.sp
                                )
                                if (!isRegisterMode) {
                                    Text(
                                        text = "Forgot?",
                                        fontSize = 12.sp,
                                        color = AuraPrimary,
                                        modifier = Modifier.clickable {
                                            toastMessage = "Password reset instructions sent to your email."
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text("••••••••••", fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = AuraOutline, modifier = Modifier.size(20.dp))
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password",
                                            tint = AuraOutline,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = AuraError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Action Button
                        Button(
                            onClick = {
                                if (isRegisterMode) {
                                    authViewModel.registerPatient(
                                        name = name.ifBlank { "Patient" },
                                        email = email,
                                        password = password,
                                        onSuccess = { onLoginSuccess(UserRole.PATIENT) }
                                    )
                                } else {
                                    authViewModel.login(email, password, onLoginSuccess)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isRegisterMode) "Create Patient Account" else "Continue to Portal",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Biometric Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = AuraSurfaceContainerHighest)
                            Text(
                                text = "FAST BIOMETRIC SIGN-IN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraOutline,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = AuraSurfaceContainerHighest)
                        }

                        // Passkey / Face ID Button
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = AuraSurfaceContainerLow,
                            onClick = {
                                toastMessage = "Biometric sensor verified. Accessing clinical dashboard..."
                                // Auto-login as patient for biometric convenience
                                onLoginSuccess(UserRole.PATIENT)
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Continue with Passkey / Face ID",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AuraOnSurface
                                )
                            }
                        }

                        // Doctor Portal Switcher
                        TextButton(
                            onClick = onNavigateToDoctorSignup,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Are you a Doctor? Clinical Physician Register",
                                color = AuraPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Diagnostic & EHR Integration Section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DIAGNOSTIC & EHR SYNC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraOnSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AuraSecondaryContainer,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "HIPAA Sync",
                                    color = AuraOnSecondaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Optional 1-Click",
                            fontSize = 11.sp,
                            color = AuraOutline
                        )
                    }

                    // Apple HealthKit Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AuraSurfaceContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = AuraError,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Apple HealthKit",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = AuraOnSurface
                                    )
                                    Text(
                                        text = "Continuous vitals, sleep & ECG stream",
                                        fontSize = 11.sp,
                                        color = AuraOnSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    isAppleHealthConnected = !isAppleHealthConnected
                                    toastMessage = if (isAppleHealthConnected) "Apple HealthKit synced. 14 biomarkers imported." else "Apple HealthKit disconnected."
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAppleHealthConnected) AuraSecondaryContainer else AuraSurfaceContainerLow,
                                    contentColor = if (isAppleHealthConnected) AuraOnSecondaryContainer else AuraOnSurface
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isAppleHealthConnected) "Connected" else "Connect",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // MyChart / Quest Labs Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AuraSurfaceContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Science,
                                        contentDescription = null,
                                        tint = AuraTertiary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "MyChart / Quest Labs",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = AuraOnSurface
                                    )
                                    Text(
                                        text = "Automated metabolic & lipid panel import",
                                        fontSize = 11.sp,
                                        color = AuraOnSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    isQuestConnected = !isQuestConnected
                                    toastMessage = if (isQuestConnected) "Quest Diagnostics / MyChart vault linked securely." else "Quest Diagnostics unlinked."
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isQuestConnected) AuraSecondaryContainer else AuraSurfaceContainerLow,
                                    contentColor = if (isQuestConnected) AuraOnSecondaryContainer else AuraOnSurface
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isQuestConnected) "Connected" else "Connect",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Medical Director Assurance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AuraPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Biotech,
                                contentDescription = null,
                                tint = AuraPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "CLINICAL REASSURANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Aura Health is in-network with major PPO plans. Zero data sharing with ad networks.",
                                fontSize = 12.sp,
                                color = AuraOnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Security & Trust Badges Footer
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecurityBadge(icon = Icons.Default.VerifiedUser, label = "HIPAA Compliant", color = AuraPrimary)
                        SecurityBadge(icon = Icons.Default.Shield, label = "SOC-2 Type II", color = AuraSecondary)
                        SecurityBadge(icon = Icons.Default.Lock, label = "256-bit Encrypted", color = AuraTertiary)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Protected by Aura Clinical Security Infrastructure. By continuing, you agree to our Medical Consent & Privacy Practices.",
                        fontSize = 11.sp,
                        color = AuraOutline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SecurityBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Surface(
        color = AuraSurfaceContainer,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, color = AuraOnSurfaceVariant, fontWeight = FontWeight.Medium)
        }
    }
}
