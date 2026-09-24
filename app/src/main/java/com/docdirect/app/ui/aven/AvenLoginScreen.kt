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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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

@Composable
fun AvenLoginScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            authViewModel.clearError()
        }
    }

    Scaffold(
        containerColor = AvenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Logo mark
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AvenTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        color = AvenWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "aven",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk,
                    letterSpacing = (-0.7).sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title & Subtitle
            Text(
                text = "Welcome back.",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = AvenInk,
                letterSpacing = (-0.7).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Let’s pick up where you left off.",
                fontSize = 14.sp,
                color = AvenMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email input
            Text(
                text = "Email address",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AvenInk
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    authViewModel.clearError()
                },
                placeholder = { Text("you@example.com", fontSize = 14.sp, color = AvenMuted) },
                trailingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = AvenMuted
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Password input
            Text(
                text = "Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AvenInk
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    authViewModel.clearError()
                },
                placeholder = { Text("••••••••••••", fontSize = 14.sp, color = AvenMuted) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = AvenMuted
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

            Spacer(modifier = Modifier.height(10.dp))

            // Forgot password
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Forgot password?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AvenTeal,
                    modifier = Modifier
                        .clickable { /* Handle forgot password */ }
                        .padding(vertical = 4.dp)
                )
            }

            // Error display
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AvenRose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 13.sp,
                        color = AvenRed,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log in button
            Button(
                onClick = {
                    authViewModel.login(email.trim(), password) { _ ->
                        onLoginSuccess()
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AvenTeal,
                    disabledContainerColor = AvenTeal.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = AvenWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Log in",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // or continue with
            Text(
                text = "or continue with",
                fontSize = 12.sp,
                color = AvenMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Continue with Google
            Surface(
                onClick = { /* OAuth Google mock */ },
                shape = RoundedCornerShape(16.dp),
                color = AvenWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Continue with Google",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AvenInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Continue with Apple
            Surface(
                onClick = { /* OAuth Apple mock */ },
                shape = RoundedCornerShape(16.dp),
                color = AvenWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Continue with Apple",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AvenInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create account link
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "New here? Create an account",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AvenTeal,
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .padding(8.dp)
                )
            }
        }
    }
}
