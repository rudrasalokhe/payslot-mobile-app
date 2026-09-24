package com.docdirect.app.data.agora

object AgoraConfig {
    // Agora App ID from console.agora.io
    const val APP_ID = "7688f9b8ddde4135b599eeb561fac340"
    
    // In testing mode, no token is needed
    // For production, implement a token server
    val TOKEN: String? = null
    
    /**
     * Generate a unique channel name from an appointment ID.
     * Both doctor and patient join the same channel.
     */
    fun getChannelName(appointmentId: String): String {
        return "docdirect_${appointmentId}"
    }
    
    /**
     * Generate a deterministic UID from a user ID string.
     * Uses absolute value of hashCode to ensure positive int.
     */
    fun getUserUid(userId: String): Int {
        return (userId.hashCode() and 0x7FFFFFFF) % 100000 + 1
    }
}
