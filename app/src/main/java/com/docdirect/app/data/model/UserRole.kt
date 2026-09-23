package com.docdirect.app.data.model

enum class UserRole {
    DOCTOR,
    PATIENT
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String = ""
)
