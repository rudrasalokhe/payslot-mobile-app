package com.docdirect.app.ui.aven

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.theme.*

@Composable
fun AvenPermissionsScreen(
    onNavigateBack: () -> Unit,
    onFinishSetup: (remindersEnabled: Boolean, newsEnabled: Boolean) -> Unit,
    onSkip: () -> Unit
) {
    var remindersEnabled by remember { mutableStateOf(true) }
    var newsEnabled by remember { mutableStateOf(false) }

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
                    onClick = { onFinishSetup(remindersEnabled, newsEnabled) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AvenTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        "Finish setup",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvenWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Skip for now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AvenTeal,
                    modifier = Modifier
                        .clickable { onSkip() }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
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
                text = "You’re in control.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AvenInk,
                letterSpacing = (-0.7).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose what makes care easier for you.",
                fontSize = 14.sp,
                color = AvenMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nearby care card
            Surface(
                shape = RoundedCornerShape(17.dp),
                color = AvenWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, AvenLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* location picker or permission */ }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
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
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AvenTeal,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nearby care",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AvenInk
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Use location or enter your area",
                            fontSize = 12.sp,
                            color = AvenMuted
                        )
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AvenMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Appointment reminders row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Appointment reminders",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AvenInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Updates before and after your visit.",
                        fontSize = 12.sp,
                        color = AvenMuted
                    )
                }

                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = { remindersEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AvenWhite,
                        checkedTrackColor = AvenTeal,
                        uncheckedThumbColor = AvenWhite,
                        uncheckedTrackColor = AvenLine,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product news row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Product news",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AvenInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Optional tips and product updates.",
                        fontSize = 12.sp,
                        color = AvenMuted
                    )
                }

                Switch(
                    checked = newsEnabled,
                    onCheckedChange = { newsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AvenWhite,
                        checkedTrackColor = AvenTeal,
                        uncheckedThumbColor = AvenWhite,
                        uncheckedTrackColor = AvenLine,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info banner: Your choice, always
            Surface(
                shape = RoundedCornerShape(15.dp),
                color = AvenMint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = AvenTeal,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Your choice, always",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AvenTeal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Change these preferences in Settings.",
                            fontSize = 12.sp,
                            color = AvenTeal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
