package com.docdirect.app.data.model

enum class AppointmentStatus {
    UPCOMING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class Appointment(
    val id: String,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialty: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val symptoms: String,
    val feePaid: Double,
    val status: AppointmentStatus = AppointmentStatus.UPCOMING,
    val transactionId: String = "",
    val prescription: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
