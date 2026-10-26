package com.docdirect.app.utils

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder

/**
 * Turns the extras of a tapped notification (the "Tap to Join" button, chat alert, prescription
 * alert, FCM call push) into a navigation route that DocDirectApp opens once the user is signed in.
 */
object NotificationRouter {

    private val _pendingRoute = MutableStateFlow<String?>(null)
    val pendingRoute: StateFlow<String?> = _pendingRoute.asStateFlow()

    fun handleIntent(intent: Intent?) {
        val route = routeFor(intent) ?: return
        _pendingRoute.value = route
    }

    fun consume() {
        _pendingRoute.value = null
    }

    private fun routeFor(intent: Intent?): String? {
        val extras = intent?.extras ?: return null
        val action = extras.getString("action") ?: return null
        val appointmentId = extras.getString("appointment_id").orEmpty()

        return when (action) {
            "join_call" -> {
                if (appointmentId.isBlank()) return null
                val isDoctor = extras.getBoolean("is_doctor", false)
                val remoteName = if (isDoctor) {
                    extras.getString("patient_name").orEmpty().ifBlank { "Patient" }
                } else {
                    extras.getString("doctor_name").orEmpty().ifBlank { "Doctor" }
                }
                "video_call/$appointmentId/${URLEncoder.encode(remoteName, "UTF-8")}?isDoctor=$isDoctor"
            }
            "open_chat" -> if (appointmentId.isBlank()) null else "chat/$appointmentId"
            "view_prescription", "view_appointment" -> "aven_visits"
            else -> null
        }
    }
}
