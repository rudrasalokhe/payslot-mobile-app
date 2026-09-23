package com.docdirect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.docdirect.app.ui.theme.*

@Composable
fun AuraBottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AuraSurface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuraNavItem(
                label = "Home",
                icon = Icons.Default.Dashboard,
                isSelected = selectedTab == "home",
                onClick = { onTabSelected("home") }
            )
            AuraNavItem(
                label = "Clinicians",
                icon = Icons.Default.MedicalServices,
                isSelected = selectedTab == "clinicians",
                onClick = { onTabSelected("clinicians") }
            )
            AuraNavItem(
                label = "Biometrics",
                icon = Icons.Default.Favorite,
                isSelected = selectedTab == "biometrics",
                onClick = { onTabSelected("biometrics") }
            )
            AuraNavItem(
                label = "Care Chat",
                icon = Icons.Default.ChatBubble,
                isSelected = selectedTab == "care_chat",
                onClick = { onTabSelected("care_chat") }
            )
        }
    }
}

@Composable
private fun AuraNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) AuraPrimary else AuraOnSurfaceVariant
    val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = fontWeight
        )
    }
}
