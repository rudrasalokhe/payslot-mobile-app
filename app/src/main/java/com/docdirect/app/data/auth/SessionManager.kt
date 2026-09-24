package com.docdirect.app.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.docdirect.app.data.model.UserRole

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("DocDirectSession", Context.MODE_PRIVATE)

    fun saveUserSession(userId: String, name: String, email: String, role: UserRole) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role.name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun getUserName(): String {
        return prefs.getString(KEY_NAME, "") ?: ""
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    fun getUserRole(): UserRole {
        val roleStr = prefs.getString(KEY_ROLE, UserRole.PATIENT.name)
        return try {
            UserRole.valueOf(roleStr ?: UserRole.PATIENT.name)
        } catch (e: Exception) {
            UserRole.PATIENT
        }
    }

    fun saveUserDetails(phone: String, dob: String, preferredName: String) {
        prefs.edit().apply {
            putString(KEY_PHONE, phone)
            putString(KEY_DOB, dob)
            putString(KEY_PREFERRED_NAME, preferredName)
            apply()
        }
    }

    fun getUserPhone(): String {
        return prefs.getString(KEY_PHONE, "+91 98765 43210") ?: "+91 98765 43210"
    }

    fun getUserDob(): String {
        return prefs.getString(KEY_DOB, "18 March 1998") ?: "18 March 1998"
    }

    fun getUserPreferredName(): String {
        val pref = prefs.getString(KEY_PREFERRED_NAME, "") ?: ""
        return if (pref.isNotBlank()) pref else getUserName().split(" ").firstOrNull() ?: getUserName()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_ROLE = "user_role"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_DOB = "user_dob"
        private const val KEY_PREFERRED_NAME = "user_preferred_name"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
