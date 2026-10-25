package com.docdirect.app.data.agora

object AgoraConfig {
    // Agora App ID from console.agora.io
    const val APP_ID = "7688f9b8ddde4135b599eeb561fac340"

    // In testing mode (App ID only), token is null or ""
    // If your Agora project has "App Certificate" enabled, place temporary test token here
    var TOKEN: String? = null

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
