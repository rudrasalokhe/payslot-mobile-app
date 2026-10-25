package com.docdirect.app.data.agora

import android.util.Log
import io.agora.media.RtcTokenBuilder2

object AgoraConfig {
    // Agora App ID from console.agora.io
    const val APP_ID = "7688f9b8ddde4135b599eeb561fac340"

    // Primary Certificate from console.agora.io
    const val APP_CERTIFICATE = "19a9c1a568c548609ea945481b4c2990"

    /**
     * Dynamically generate a valid Agora RTC Token using HMAC-SHA256 authentication (AccessToken2 / Token007).
     * Compatible with Agora 4.x RTC Engine (io.agora.rtc2).
     * Valid for 24 hours (86,400 seconds) so calls never get disconnected by token expiration.
     */
    fun generateRtcToken(channelName: String, uid: Int): String {
        return try {
            val tokenBuilder = RtcTokenBuilder2()
            val expirationInSeconds = 86400 // 24 hours duration from now
            val token = tokenBuilder.buildTokenWithUid(
                APP_ID,
                APP_CERTIFICATE,
                channelName,
                uid,
                RtcTokenBuilder2.Role.ROLE_PUBLISHER,
                expirationInSeconds,
                expirationInSeconds
            )
            Log.i("AgoraConfig", "Generated dynamic Agora RTC (v007) token for channel=$channelName, uid=$uid, len=${token.length}")
            token
        } catch (e: Exception) {
            Log.e("AgoraConfig", "Failed to generate dynamic token: ${e.message}")
            ""
        }
    }

    /**
     * Generate a unique, sanitized channel name from an appointment ID.
     * Both doctor and patient join the same channel.
     * Agora channels must contain only valid ASCII alphanumeric / underscore chars <= 64 bytes.
     */
    fun getChannelName(appointmentId: String): String {
        val sanitized = appointmentId.replace(Regex("[^a-zA-Z0-9_]"), "_").take(32)
        return if (sanitized.isNotBlank()) "docdirect_$sanitized" else "docdirect_telehealth_visit"
    }

    /**
     * Generate deterministic, non-colliding UIDs for doctor vs patient.
     * Doctor UIDs are in range [50001..99999], Patient in range [1..49999].
     * This prevents Agora from disconnecting either party due to duplicate UID collisions.
     */
    fun getUserUid(userId: String, isDoctor: Boolean = false): Int {
        val safeId = userId.ifBlank { if (isDoctor) "doctor_default" else "patient_default" }
        val base = ((safeId.hashCode() and 0x7FFFFFFF) % 40000) + 1
        return if (isDoctor) base + 50000 else base
    }
}
