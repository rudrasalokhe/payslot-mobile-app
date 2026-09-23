package com.docdirect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.docdirect.app.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val createdAt: Long = System.currentTimeMillis()
)
