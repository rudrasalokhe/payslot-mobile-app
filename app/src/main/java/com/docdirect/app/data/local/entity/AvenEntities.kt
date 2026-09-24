package com.docdirect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class RecordEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val title: String,
    val category: String,
    val fileSize: String,
    val date: String,
    val isSharedWithDoctor: Boolean = true,
    val sharedWithDoctorName: String = "Dr Mira Shah",
    val sharingExpiresAt: String = "27 Sep 2026",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val name: String,
    val relationship: String,
    val age: Int,
    val gender: String,
    val dob: String
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val appointmentId: String,
    val patientName: String,
    val doctorName: String,
    val specialty: String,
    val consultationFee: Double,
    val platformFee: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val status: String = "PAID",
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val deepLink: String = ""
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val patientName: String,
    val rating: Int,
    val reviewText: String,
    val visitType: String = "Verified video visit",
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)
