package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.theme.*

@Composable
fun AvenSetupScreen(
    initialName: String,
    onNavigateBack: () -> Unit,
    onContinue: (preferredName: String, dob: String, phone: String) -> Unit
) {
    var preferredName by remember { mutableStateOf(initialName.split(" ").firstOrNull() ?: initialName) }
    var dob by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

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
                        onContinue(preferredName.ifBlank { initialName }, dob, phone)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AvenWhite)
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
                text = "Make it yours.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AvenInk,
                letterSpacing = (-0.7).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Help us personalise your appointment details.",
                fontSize = 14.sp,
                color = AvenMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Preferred name
            Text("Preferred name", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = preferredName,
                onValueChange = { preferredName = it },
                placeholder = { Text("e.g. Aarav", fontSize = 14.sp, color = AvenMuted) },
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

            // Date of birth
            Text("Date of birth", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                placeholder = { Text("e.g. 18 March 1998", fontSize = 14.sp, color = AvenMuted) },
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AvenMuted) },
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

            // Mobile number
            Text("Mobile number", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AvenInk)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("+91  98765 43210", fontSize = 14.sp, color = AvenMuted) },
                trailingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AvenMuted) },
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

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "You can add medical details later.",
                fontSize = 12.sp,
                color = AvenMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(15.dp),
                color = AvenMint
            ) {
                Text(
                    text = "1 of 2 · Your details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvenTeal,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
