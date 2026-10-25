package com.docdirect.app.ui.doctor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.*
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DoctorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)
    val currentUserId = repository.getCurrentUserId() ?: ""

    // Resolve doctor ID (default to doc_mira_shah for clinical workspace)
    val doctorId: String = when {
        currentUserId.equals("user_mira_shah", ignoreCase = true) -> "doc_mira_shah"
        currentUserId.startsWith("doc_") -> currentUserId
        currentUserId.isNotBlank() -> "doc_$currentUserId"
        else -> "doc_mira_shah"
    }

    val doctorProfile: StateFlow<DoctorProfile> = repository.getCurrentDoctorProfile(doctorId)
        .map { profile ->
            profile ?: DoctorProfile(
                id = doctorId,
                name = if (repository.getCurrentUserName().isNotBlank()) repository.getCurrentUserName() else "Dr Mira Shah",
                medicalLicense = "MCI-48921-DERM",
                specialty = "Dermatology",
                qualification = "MBBS, MD (Dermatology)",
                experienceYears = 8,
                consultationFee = 1200.0,
                bio = "Specializes in clinical and aesthetic dermatology at Bandra Skin Clinic.",
                hospitalAffiliation = "Bandra Skin Clinic, Mumbai"
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DoctorProfile(
                id = doctorId,
                name = "Dr Mira Shah",
                medicalLicense = "MCI-48921-DERM",
                specialty = "Dermatology",
                qualification = "MBBS, MD (Dermatology)",
                experienceYears = 8,
                consultationFee = 1200.0,
                bio = "Specializes in clinical and aesthetic dermatology."
            )
        )

    val availableSlots: StateFlow<List<TimeSlot>> = repository.getSlotsForDoctor(doctorId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val appointments: StateFlow<List<Appointment>> = repository.getAppointmentsForDoctor(doctorId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayAppointments: StateFlow<List<Appointment>> = appointments.map { list ->
        list.filter { it.appointmentDate.contains("25 Sep", ignoreCase = true) || it.appointmentDate.equals("Today", ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            repository.toggleDoctorAvailability(doctorId, isAvailable)
        }
    }

    fun addSlot(date: String, time: String) {
        if (date.isNotBlank() && time.isNotBlank()) {
            viewModelScope.launch {
                repository.addTimeSlot(doctorId, date, time)
            }
        }
    }

    fun deleteSlot(slotId: String) {
        viewModelScope.launch {
            repository.deleteTimeSlot(slotId)
        }
    }

    fun updateFee(newFee: Double) {
        viewModelScope.launch {
            repository.updateDoctorFee(doctorId, newFee)
        }
    }

    fun updateProfile(specialty: String, qualification: String, bio: String, hospital: String) {
        viewModelScope.launch {
            repository.updateDoctorProfile(doctorId, specialty, qualification, bio, hospital)
        }
    }

    fun completeAppointment(appointmentId: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointmentId, AppointmentStatus.COMPLETED)
        }
    }

    fun issuePrescription(appointmentId: String, prescriptionText: String) {
        viewModelScope.launch {
            repository.addPrescription(appointmentId, prescriptionText)
        }
    }
}
