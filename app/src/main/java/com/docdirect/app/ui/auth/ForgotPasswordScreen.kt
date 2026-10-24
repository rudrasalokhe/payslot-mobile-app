package com.docdirect.app.ui.auth

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.theme.*

/**
 * Forgot Password & Security Flow screen.
 * Handles email verification link dispatch, input validation, and password reset completion.
 */
@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLinkSent by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val focusManager = LocalFocusManager.current

    DisposableEffect(Unit) {
        onDispose {
            authViewModel.resetPasswordState()
        }
    }

    fun validateEmail(): Boolean {
        val trimmed = email.trim()
        return when {
            trimmed.isBlank() -> {
                emailError = "Please enter your email address."
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(trimmed).matches() -> {
                emailError = "Please enter a valid email address (e.g. name@example.com)."
                false
            }
            else -> {
                emailError = null
                true
            }
        }
    }

    fun handleSendResetLink() {
        focusManager.clearFocus()
        if (validateEmail()) {
            authViewModel.sendPasswordResetLink(email.trim()) { success, err ->
                if (success) {
                    isLinkSent = true
                } else if (err != null) {
                    emailError = err
                }
            }
        }
    }

    fun handleCompleteReset() {
        focusManager.clearFocus()
        when {
            newPassword.length < 6 -> {
                passwordError = "Password must be at least 6 characters."
            }
            newPassword != confirmPassword -> {
                passwordError = "Passwords do not match."
            }
            else -> {
                passwordError = null
                authViewModel.completePasswordReset(
                    email = email.trim(),
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    onSuccess = {
                        showSuccessDialog = true
                    },
                    onError = { err ->
                        passwordError = err
                    }
                )
            }
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
                        text = "A",
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

            // Header titles
            Text(
                text = if (isLinkSent) "Check your inbox." else "Reset password.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AvenInk,
                letterSpacing = (-0.7).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isLinkSent) {
                    "We've sent a secure password reset link to\n${email.trim()}."
                } else {
                    "Enter the email associated with your account and we’ll send you a recovery link to reset your password."
                },
                fontSize = 14.sp,
                color = AvenMuted,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // STEP 1: Enter email
            if (!isLinkSent) {
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
                        emailError = null
                        authViewModel.clearError()
                    },
                    placeholder = { Text("you@example.com", fontSize = 14.sp, color = AvenMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = if (emailError != null) AvenRed else AvenTeal
                        )
                    },
                    trailingIcon = {
                        if (email.isNotBlank()) {
                            IconButton(onClick = { email = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AvenMuted)
                            }
                        }
                    },
                    isError = emailError != null || errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { handleSendResetLink() }
                    ),
                    shape = RoundedCornerShape(13.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AvenWhite,
                        unfocusedContainerColor = AvenWhite,
                        focusedBorderColor = AvenTeal,
                        unfocusedBorderColor = AvenLine,
                        errorBorderColor = AvenRed
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (emailError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = emailError ?: "",
                        fontSize = 12.sp,
                        color = AvenRed,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { handleSendResetLink() },
                    enabled = !isLoading,
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
                            color = AvenWhite,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Send reset link",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenWhite
                        )
                    }
                }
            } else {
                // STEP 2: Link Sent + Set New Password option
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AvenMint,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenTeal.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AvenTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MarkEmailRead,
                                contentDescription = null,
                                tint = AvenWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Verification link active",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvenInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Check spam folder if not visible within 2 minutes.",
                                fontSize = 12.sp,
                                color = AvenMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Optional immediate password update
                Text(
                    text = "Set new password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Or update your password right away below.",
                    fontSize = 13.sp,
                    color = AvenMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // New password field
                Text(
                    text = "New Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AvenInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        passwordError = null
                    },
                    placeholder = { Text("At least 6 characters", fontSize = 14.sp, color = AvenMuted) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AvenMuted)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
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
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm password field
                Text(
                    text = "Confirm New Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AvenInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        passwordError = null
                    },
                    placeholder = { Text("Re-enter new password", fontSize = 14.sp, color = AvenMuted) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Default.LockReset, contentDescription = null, tint = AvenMuted)
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
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
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (passwordError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = passwordError ?: "",
                        fontSize = 12.sp,
                        color = AvenRed,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { handleCompleteReset() },
                    enabled = !isLoading && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
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
                            color = AvenWhite,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Save new password",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Resend email link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { handleSendResetLink() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Resend",
                        tint = AvenTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Resend recovery email",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Security note banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                color = AvenWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AvenMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AvenTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Account Security Guarantee",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Password reset links expire in 15 minutes for your protection.",
                            fontSize = 12.sp,
                            color = AvenMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Return to log in button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToLogin() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Remembered your password? ",
                    fontSize = 13.sp,
                    color = AvenMuted
                )
                Text(
                    text = "Log in",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenTeal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Success Confirmation Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onNavigateToLogin()
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = AvenWhite,
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AvenMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = AvenTeal,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Password Reset Complete",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Your password has been successfully updated. You can now log in using your new credentials.",
                    fontSize = 14.sp,
                    color = AvenMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateToLogin()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Log In Now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenWhite
                    )
                }
            }
        )
    }
}
