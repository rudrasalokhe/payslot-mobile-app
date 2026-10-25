package com.docdirect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing logged daily health metrics (Blood Pressure, Blood Sugar, Weight)
 * for the Health Tracker in the Records Hub.
 */
@Entity(tableName = "vitals")
data class VitalEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val type: String, // "BP", "SUGAR", "WEIGHT"
    val valuePrimary: Double, // Systolic BP (mmHg), Blood Sugar (mg/dL), or Body Weight (kg)
    val valueSecondary: Double = 0.0, // Diastolic BP (mmHg) for BP entries
    val unit: String, // "mmHg", "mg/dL", "kg"
    val notes: String = "", // e.g., "Fasting", "Post-meal", "Morning"
    val date: String, // e.g. "Today", "24 Oct 2026"
    val timestamp: Long = System.currentTimeMillis()
)
