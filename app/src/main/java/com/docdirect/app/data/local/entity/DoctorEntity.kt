package com.docdirect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class DoctorEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val medicalLicense: String,
    val specialty: String,
    val qualification: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val bio: String,
    val rating: Double = 5.0,
    val reviewCount: Int = 0,
    val isAvailable: Boolean = true,
    val hospitalAffiliation: String = ""
)
