package com.docdirect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.docdirect.app.data.model.AppointmentStatus
import com.docdirect.app.data.model.UserRole

@Entity(tableName = "slots")
data class SlotEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val date: String,
    val time: String,
    val isBooked: Boolean = false
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
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

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val appointmentId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val messageText: String,
    val isPrescription: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
