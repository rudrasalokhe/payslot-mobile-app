package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.theme.*

@Composable
fun AvenSignUpScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onProceedToVerify: (name: String, email: String, password: String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Surface(
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(14.dp),
                    color = AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AvenInk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            validationError = "Please enter your full name."
                        } else if (email.isBlank() || !email.contains("@")) {
                            validationError = "Please enter a valid email address."
                        } else if (password.length < 6) {
                            validationError = "Password must be at least 6 characters."
                        } else if (!agreedToTerms) {
                            validationError = "Please accept the Terms and Privacy Notice."
                        } else {
                            validationError = null
                            onProceedToVerify(fullName.trim(), email.trim(), password)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Create account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Already a member? Log in",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenTeal,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your care.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AvenInk,
                letterSpacing = (-0.7).sp
            )
            Text(
                text = "Your account.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AvenInk,
                letterSpacing = (-0.7).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "A few details, and you’re ready to begin.",
                fontSize = 14.sp,
                color = AvenMuted
            )

            if (validationError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = AvenRose,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationError ?: "",
                        color = AvenRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Full name
            Text("Full name", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("e.g. Aarav Mehta", fontSize = 14.sp, color = AvenMuted) },
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AvenWhite,
                    unfocusedContainerColor = AvenWhite,
                    focusedBorderColor = AvenTeal,
                    unfocusedBorderColor = AvenLine
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Email address
            Text("Email address", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("e.g. aarav@example.com", fontSize = 14.sp, color = AvenMuted) },
                trailingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null, tint = AvenMuted) },
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AvenWhite,
                    unfocusedContainerColor = AvenWhite,
                    focusedBorderColor = AvenTeal,
                    unfocusedBorderColor = AvenLine
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Password
            Text("Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Create a password", fontSize = 14.sp, color = AvenMuted) },
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
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AvenWhite,
                    unfocusedContainerColor = AvenWhite,
                    focusedBorderColor = AvenTeal,
                    unfocusedBorderColor = AvenLine
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Use 6+ characters, with a number or symbol.", fontSize = 11.sp, color = AvenMuted)

            Spacer(modifier = Modifier.height(20.dp))

            // Terms Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreedToTerms = !agreedToTerms },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (agreedToTerms) AvenTeal else AvenWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (agreedToTerms) AvenTeal else AvenLine),
                    modifier = Modifier.size(22.dp)
                ) {
                    if (agreedToTerms) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AvenWhite, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "I agree to the Terms and Privacy Notice.",
                    fontSize = 12.sp,
                    color = AvenInk
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
