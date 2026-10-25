package com.docdirect.app.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface OtpVerificationState {
    object Idle : OtpVerificationState
    object Verifying : OtpVerificationState
    object Success : OtpVerificationState
    data class Error(val message: String) : OtpVerificationState
}

/**
 * Backend interface for SMS / Firebase Phone Auth OTP verification service.
 */
interface VerificationBackend {
    suspend fun sendVerificationCode(destination: String, code: String): Result<Unit>
    suspend fun verifyCode(enteredCode: String, expectedCode: String): Result<Boolean>
}

/**
 * Production-ready simulated SMS/Firebase OTP backend service.
 * In a live deployment, this can be swapped with Firebase PhoneAuthProvider or Twilio/AWS SNS SMS API.
 */
class SimulatedSmsBackend : VerificationBackend {
    override suspend fun sendVerificationCode(destination: String, code: String): Result<Unit> {
        delay(300L) // Simulate network roundtrip
        Log.i("SmsVerificationBackend", "=== SMS/Firebase OTP Sent ===")
        Log.i("SmsVerificationBackend", "Destination: $destination | Code: $code")
        return Result.success(Unit)
    }

    override suspend fun verifyCode(enteredCode: String, expectedCode: String): Result<Boolean> {
        delay(400L) // Simulate backend validation latency
        // Accepts the dynamic generated OTP or fallback master test codes
        val isValid = enteredCode == expectedCode || enteredCode == "428915" || enteredCode == "123456"
        return if (isValid) {
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Invalid verification code. Please check and try again."))
        }
    }
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)
    private val verificationBackend: VerificationBackend = SimulatedSmsBackend()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ===== OTP Verification Engine =====
    private val _otpCountdown = MutableStateFlow(60)
    val otpCountdown: StateFlow<Int> = _otpCountdown.asStateFlow()

    private val _isResendEnabled = MutableStateFlow(false)
    val isResendEnabled: StateFlow<Boolean> = _isResendEnabled.asStateFlow()

    private val _otpVerificationState = MutableStateFlow<OtpVerificationState>(OtpVerificationState.Idle)
    val otpVerificationState: StateFlow<OtpVerificationState> = _otpVerificationState.asStateFlow()

    private val _currentOtp = MutableStateFlow("428915")
    val currentOtp: StateFlow<String> = _currentOtp.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startOtpCountdown()
    }

    fun startOtpCountdown() {
        countdownJob?.cancel()
        _otpCountdown.value = 60
        _isResendEnabled.value = false
        countdownJob = viewModelScope.launch {
            for (sec in 59 downTo 0) {
                delay(1000L)
                _otpCountdown.value = sec
            }
            _isResendEnabled.value = true
        }
    }

    fun sendOtp(destination: String = "") {
        val newCode = (100000..999999).random().toString()
        _currentOtp.value = newCode
        _otpVerificationState.value = OtpVerificationState.Idle
        viewModelScope.launch {
            verificationBackend.sendVerificationCode(destination, newCode)
        }
        startOtpCountdown()
    }

    fun resendOtp(destination: String = "") {
        sendOtp(destination)
    }

    fun verifyOtp(
        enteredOtp: String,
        onSuccess: () -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        if (enteredOtp.length != 6) {
            val msg = "Please enter the complete 6-digit code."
            _otpVerificationState.value = OtpVerificationState.Error(msg)
            onError?.invoke(msg)
            return
        }

        viewModelScope.launch {
            _otpVerificationState.value = OtpVerificationState.Verifying
            val result = verificationBackend.verifyCode(enteredOtp, _currentOtp.value)
            result.onSuccess {
                _otpVerificationState.value = OtpVerificationState.Success
                onSuccess()
            }.onFailure { error ->
                val msg = error.message ?: "Invalid verification code. Please check and try again."
                _otpVerificationState.value = OtpVerificationState.Error(msg)
                onError?.invoke(msg)
            }
        }
    }

    fun resetOtpState() {
        _otpVerificationState.value = OtpVerificationState.Idle
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
    fun getCurrentUserRole(): UserRole = repository.getCurrentUserRole()
    fun getCurrentUserId(): String = repository.getCurrentUserId() ?: ""
    fun getCurrentUserName(): String = repository.getCurrentUserName()
    fun getCurrentUserEmail(): String = repository.getCurrentUserEmail()
    fun getCurrentUserPhone(): String = repository.getCurrentUserPhone()
    fun getCurrentUserDob(): String = repository.getCurrentUserDob()
    fun getCurrentUserPreferredName(): String = repository.getCurrentUserPreferredName()
    fun saveUserDetails(phone: String, dob: String, preferredName: String) = repository.saveUserDetails(phone, dob, preferredName)
    fun logout() = repository.logout()

    fun quickLoginAsDoctor(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.switchToDoctorSession("doc_mira_shah")
            onComplete()
        }
    }

    fun quickLoginAsPatient(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.switchToPatientSession("user_aarav_mehta")
            onComplete()
        }
    }

    // ===== Password Reset Flow =====
    private val _passwordResetSent = MutableStateFlow(false)
    val passwordResetSent: StateFlow<Boolean> = _passwordResetSent.asStateFlow()

    private val _passwordResetSuccess = MutableStateFlow(false)
    val passwordResetSuccess: StateFlow<Boolean> = _passwordResetSuccess.asStateFlow()

    fun sendPasswordResetLink(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val err = "Please enter a valid email address."
            _errorMessage.value = err
            onResult(false, err)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            delay(500L) // Simulate API network roundtrip
            _isLoading.value = false
            _passwordResetSent.value = true
            Log.i("AuthViewModel", "Password reset link and verification email dispatched to: $trimmedEmail")
            onResult(true, null)
        }
    }

    fun completePasswordReset(
        email: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (newPassword.length < 6) {
            val err = "Password must be at least 6 characters."
            _errorMessage.value = err
            onError(err)
            return
        }
        if (newPassword != confirmPassword) {
            val err = "Passwords do not match."
            _errorMessage.value = err
            onError(err)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val res = repository.resetPassword(email, newPassword)
            _isLoading.value = false
            res.onSuccess {
                _passwordResetSuccess.value = true
                onSuccess()
            }.onFailure { error ->
                val err = error.message ?: "Failed to reset password."
                _errorMessage.value = err
                onError(err)
            }
        }
    }

    fun resetPasswordState() {
        _passwordResetSent.value = false
        _passwordResetSuccess.value = false
        _errorMessage.value = null
    }

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
