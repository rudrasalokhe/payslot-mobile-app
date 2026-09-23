package com.docdirect.app.data.model

data class TimeSlot(
    val id: String,
    val date: String, // e.g. "2026-09-24"
    val time: String, // e.g. "10:00 AM"
    val isBooked: Boolean = false
)

data class DoctorProfile(
    val id: String,
    val name: String,
    val medicalLicense: String,
    val specialty: String, // Cardiology, Dermatology, Pediatrics, General Medicine, Neurology, Orthopedics
    val qualification: String, // e.g., "MD, MBBS - Harvard Medical School"
    val experienceYears: Int,
    val consultationFee: Double,
    val bio: String,
    val rating: Double = 4.9,
    val reviewCount: Int = 128,
    val isAvailable: Boolean = true,
    val availableSlots: List<TimeSlot> = emptyList(),
    val hospitalAffiliation: String = "City General Hospital"
)
