package com.docdirect.app.ui.patient

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.components.AuraBottomNavBar
import com.docdirect.app.ui.components.SparklineCanvas
import com.docdirect.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraHomeScreen(
    userName: String = "Alex",
    onNavigateToClinicians: () -> Unit,
    onNavigateToMyAppointments: () -> Unit = {},
    onNavigateToTelehealthCall: () -> Unit,
    onOpenChat: () -> Unit,
    onSignOut: () -> Unit
) {
    var selectedNavTab by remember { mutableStateOf("home") }

    // Task items state
    var task1Checked by remember { mutableStateOf(false) }
    var task2Checked by remember { mutableStateOf(false) }
    var task3Checked by remember { mutableStateOf(true) }

    val completedCount = (if (task1Checked) 1 else 0) + (if (task2Checked) 1 else 0) + (if (task3Checked) 1 else 0)

    Scaffold(
        containerColor = AuraBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = AuraPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AURA HEALTH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraOnSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Home",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraOnSurface
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToMyAppointments) {
                        Icon(Icons.Default.Event, contentDescription = "My Appointments", tint = AuraPrimary)
                    }
                    IconButton(onClick = onOpenChat) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = AuraOnSurfaceVariant)
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = AuraOnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraSurface.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            AuraBottomNavBar(
                selectedTab = selectedNavTab,
                onTabSelected = { tab ->
                    selectedNavTab = tab
                    if (tab == "clinicians") {
                        onNavigateToClinicians()
                    } else if (tab == "care_chat") {
                        onOpenChat()
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Welcome & Date Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PATIENT OVERVIEW • TUE, OCT 15",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraOnSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Surface(
                            color = AuraSecondaryContainer,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraSecondary))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Vitals Synchronized",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AuraOnSecondaryContainer
                                )
                            }
                        }
                    }

                    Text(
                        text = "Good morning, $userName",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AuraOnSurface
                    )

                    Text(
                        text = "Your longitudinal biomarkers remain stable and within target longevity thresholds.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AuraOnSurfaceVariant
                    )
                }
            }

            // Upcoming Consultation Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLow),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AuraPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("UPCOMING CONSULTATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AuraPrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("•", fontSize = 10.sp, color = AuraOutlineVariant)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tomorrow, 10:00 AM", fontSize = 11.sp, color = AuraOnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Comprehensive Review with Dr. Vance",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = AuraOnSurface
                                    )
                                    Text(
                                        text = "Virtual Clinic • 30 mins",
                                        fontSize = 12.sp,
                                        color = AuraOnSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onOpenChat,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Prep Notes", fontSize = 13.sp)
                            }

                            Button(
                                onClick = onNavigateToMyAppointments,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Join Call", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Attending Lead Physician Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ATTENDING LEAD PHYSICIAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AuraOnSurfaceVariant, letterSpacing = 0.5.sp)
                            Surface(
                                color = AuraSecondaryContainer.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraSecondary))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Available Today", fontSize = 11.sp, color = AuraOnSecondaryContainer, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AuraPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Dr. Julian Vance, MD", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AuraOnSurface)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(16.dp))
                                }
                                Text("Lead Longevity & Preventive Medicine", fontSize = 12.sp, color = AuraPrimary, fontWeight = FontWeight.Medium)
                                Text("Stanford Health Care • 14 yrs clinical tenure", fontSize = 12.sp, color = AuraOnSurfaceVariant)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onOpenChat,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
                            ) {
                                Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Message Team", fontSize = 13.sp)
                            }

                            Button(
                                onClick = onNavigateToClinicians,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AuraSurfaceContainerHigh,
                                    contentColor = AuraOnSurface
                                )
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Book Visit", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Core Biometrics Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Core Biometrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = AuraSurfaceContainerHigh,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text("Continuous", fontSize = 10.sp, color = AuraOnSurfaceVariant, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        TextButton(onClick = {}) {
                            Text("Detailed Trends", fontSize = 12.sp, color = AuraPrimary, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(14.dp))
                        }
                    }

                    // 2x2 Grid of Biometrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 1: Resting HR with Sparkline
                        BiometricCard(
                            modifier = Modifier.weight(1f),
                            label = "RESTING HR",
                            sublabel = "Within baseline",
                            value = "58",
                            unit = "bpm",
                            icon = Icons.Default.Favorite,
                            iconTint = AuraPrimary
                        ) {
                            SparklineCanvas(color = AuraPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("7-day avg: 59", fontSize = 10.sp, color = AuraOnSurfaceVariant)
                                Text("-2 bpm", fontSize = 10.sp, color = AuraSecondary, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Metric 2: Blood Pressure
                        BiometricCard(
                            modifier = Modifier.weight(1f),
                            label = "BLOOD PRESSURE",
                            sublabel = "Optimal",
                            value = "118/76",
                            unit = "mmHg",
                            icon = Icons.Default.Speed,
                            iconTint = AuraPrimary
                        ) {
                            LinearProgressIndicator(
                                progress = { 0.65f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = AuraSecondary,
                                trackColor = AuraSurfaceContainerHigh
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Systolic: 118", fontSize = 10.sp, color = AuraOnSurfaceVariant)
                                Text("2d ago", fontSize = 10.sp, color = AuraOnSurfaceVariant)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 3: Fasting Glucose
                        BiometricCard(
                            modifier = Modifier.weight(1f),
                            label = "FASTING GLUCOSE",
                            sublabel = "Target <95",
                            value = "88",
                            unit = "mg/dL",
                            icon = Icons.Default.WaterDrop,
                            iconTint = AuraPrimary
                        ) {
                            LinearProgressIndicator(
                                progress = { 0.42f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = AuraSecondary,
                                trackColor = AuraSurfaceContainerHigh
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dawn spike: None", fontSize = 10.sp, color = AuraOnSurfaceVariant)
                                Text("Steady", fontSize = 10.sp, color = AuraSecondary, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Metric 4: ApoB Biomarker
                        BiometricCard(
                            modifier = Modifier.weight(1f),
                            label = "APOB BIOMARKER",
                            sublabel = "Target achieved",
                            value = "74",
                            unit = "mg/dL",
                            icon = Icons.Default.Biotech,
                            iconTint = AuraPrimary
                        ) {
                            LinearProgressIndicator(
                                progress = { 0.78f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = AuraPrimary,
                                trackColor = AuraSurfaceContainerHigh
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Goal <80 mg/dL", fontSize = 10.sp, color = AuraOnSurfaceVariant)
                                Text("Verified", fontSize = 10.sp, color = AuraPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Active Care Plan & Tasks Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = AuraPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Active Care Plan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AuraOnSurface)
                            }
                            Text("$completedCount of 3 complete", fontSize = 12.sp, color = AuraOnSurfaceVariant)
                        }

                        // Task 1
                        CareTaskItem(
                            title = "Log fasting blood glucose before 10 AM",
                            subtitle = "Daily Longevity Log • Due in 45m",
                            isChecked = task1Checked,
                            onToggle = { task1Checked = !task1Checked }
                        )

                        // Task 2
                        CareTaskItem(
                            title = "Schedule Q3 diagnostic blood draw",
                            subtitle = "Quest Diagnostics • 1.2 mi away",
                            isChecked = task2Checked,
                            onToggle = { task2Checked = !task2Checked }
                        )

                        // Task 3
                        CareTaskItem(
                            title = "Review vitamin D3 micro-protocol with care team",
                            subtitle = "Completed yesterday by Dr. Vance",
                            isChecked = task3Checked,
                            onToggle = { task3Checked = !task3Checked }
                        )
                    }
                }
            }

            // Care Concierge 24/7 Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = AuraSecondaryFixed, modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Care Concierge 24/7", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraSecondaryFixed))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Have a symptom? Connect with a registered nurse in <2 minutes.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onOpenChat,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraSurfaceContainerLowest,
                                contentColor = AuraPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Start Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BiometricCard(
    modifier: Modifier = Modifier,
    label: String,
    sublabel: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AuraSurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AuraOnSurfaceVariant, letterSpacing = 0.5.sp)
                    Text(sublabel, fontSize = 11.sp, color = AuraSecondary, fontWeight = FontWeight.Medium)
                }
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AuraOnSurface)
                if (unit.isNotBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(unit, fontSize = 12.sp, color = AuraOnSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun CareTaskItem(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        color = if (isChecked) AuraSurfaceContainerLow.copy(alpha = 0.5f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = AuraPrimary,
                    uncheckedColor = AuraSurfaceContainerHighest
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isChecked) AuraOnSurfaceVariant else AuraOnSurface,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = if (isChecked) AuraSecondary else AuraOnSurfaceVariant
                )
            }
            if (isChecked) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AuraSecondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
