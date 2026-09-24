package com.docdirect.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
    fun getCurrentUserRole(): UserRole = repository.getCurrentUserRole()
    fun getCurrentUserId(): String = repository.getCurrentUserId() ?: ""
    fun getCurrentUserName(): String = repository.getCurrentUserName()
    fun logout() = repository.logout()

    fun login(
        email: String,
        password: String,
        onSuccess: (role: UserRole) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please enter your email and password."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.login(email, password)
            _isLoading.value = false
            result.onSuccess { user ->
                onSuccess(user.role)
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Authentication failed."
            }
        }
    }

    fun registerDoctor(
        name: String,
        email: String,
        password: String,
        license: String,
        specialty: String,
        qualification: String,
        experienceYears: Int,
        fee: Double,
        bio: String,
        hospital: String,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || license.isBlank() || specialty.isBlank()) {
            _errorMessage.value = "Please fill in all required doctor profile fields."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val userResult = repository.registerUser(name, email, password, UserRole.DOCTOR)
            _isLoading.value = false

            userResult.onSuccess { user ->
                viewModelScope.launch {
                    repository.registerDoctorProfile(
                        userId = user.id,
                        name = name,
                        license = license,
                        specialty = specialty,
                        qualification = qualification,
                        experienceYears = experienceYears,
                        fee = fee,
                        bio = bio,
                        hospital = hospital
                    )
                    onSuccess()
                }
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Doctor registration failed."
            }
        }
    }

    fun registerPatient(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all required patient fields."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val userResult = repository.registerUser(name, email, password, UserRole.PATIENT)
            _isLoading.value = false

            userResult.onSuccess {
                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Patient registration failed."
            }
        }
    }
}
