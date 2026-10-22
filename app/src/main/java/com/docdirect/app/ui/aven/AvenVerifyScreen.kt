package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.docdirect.app.ui.auth.AuthViewModel
import com.docdirect.app.ui.auth.OtpVerificationState
import com.docdirect.app.ui.theme.*

@Composable
fun AvenVerifyScreen(
    email: String,
    authViewModel: AuthViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onVerified: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    val countdown by authViewModel.otpCountdown.collectAsState()
    val isResendEnabled by authViewModel.isResendEnabled.collectAsState()
    val verificationState by authViewModel.otpVerificationState.collectAsState()
    val currentGeneratedOtp by authViewModel.currentOtp.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Request focus on entry and trigger OTP send
    LaunchedEffect(Unit) {
        if (email.isNotBlank()) {
            authViewModel.sendOtp(email)
        }
        focusRequester.requestFocus()
    }

    // Clear error message when user modifies input
    LaunchedEffect(otpValue) {
        if (verificationState is OtpVerificationState.Error) {
            authViewModel.resetOtpState()
        }
    }

    val isVerifying = verificationState is OtpVerificationState.Verifying

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvenBg)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        authViewModel.verifyOtp(
                            enteredOtp = otpValue,
                            onSuccess = onVerified
                        )
                    },
                    enabled = otpValue.length == 6 && !isVerifying,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AvenTeal,
                        disabledContainerColor = AvenTeal.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = AvenWhite,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Verify and continue",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenWhite
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
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Check your inbox.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AvenInk,
                letterSpacing = (-0.7).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We sent a 6-digit code to\n${email.ifBlank { "your email" }}.",
                fontSize = 14.sp,
                color = AvenMuted,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Edit email pill
            Surface(
                onClick = onNavigateBack,
                shape = RoundedCornerShape(15.dp),
                color = AvenMint
            ) {
                Text(
                    text = "Edit email",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenTeal,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Dynamic 6-digit interactive OTP input
            BasicTextField(
                value = otpValue,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    if (filtered.length <= 6) {
                        otpValue = filtered
                        if (filtered.length == 6) {
                            keyboardController?.hide()
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (otpValue.length == 6) {
                            authViewModel.verifyOtp(
                                enteredOtp = otpValue,
                                onSuccess = onVerified
                            )
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                decorationBox = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { focusRequester.requestFocus() },
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isError = verificationState is OtpVerificationState.Error
                        for (index in 0 until 6) {
                            val digit = otpValue.getOrNull(index)?.toString() ?: ""
                            val isCurrentActive = (index == otpValue.length) || (index == 5 && otpValue.length == 6)

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp),
                                shape = RoundedCornerShape(13.dp),
                                color = AvenWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isCurrentActive) 1.5.dp else 1.dp,
                                    color = when {
                                        isError -> AvenRed
                                        isCurrentActive -> AvenTeal
                                        digit.isNotEmpty() -> AvenInk
                                        else -> AvenLine
                                    }
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = digit,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isError) AvenRed else AvenInk
                                    )
                                }
                            }
                        }
                    }
                }
            )

            // Inline error display
            if (verificationState is OtpVerificationState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AvenRose,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AvenRed.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = AvenRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = (verificationState as OtpVerificationState.Error).message,
                            fontSize = 13.sp,
                            color = AvenRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 60s countdown timer & dynamic resend logic
            if (isResendEnabled || countdown == 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable {
                        otpValue = ""
                        authViewModel.resendOtp(email)
                        focusRequester.requestFocus()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Resend OTP",
                        tint = AvenTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Resend code",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenTeal
                    )
                }
            } else {
                val formattedSeconds = if (countdown < 10) "0$countdown" else "$countdown"
                Text(
                    text = "Resend code in 00:$formattedSeconds",
                    fontSize = 13.sp,
                    color = AvenMuted
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Shield banner: Keep this code private
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                color = AvenMint
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AvenTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = AvenTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Keep this code private",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AvenTeal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Aven support will never ask you for it.",
                            fontSize = 12.sp,
                            color = AvenTeal
                        )
                    }
                }
            }

            // Developer / Demo OTP Hint
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Demo code: $currentGeneratedOtp (or 428915)",
                    fontSize = 11.sp,
                    color = AvenMuted.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
