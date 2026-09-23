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

    val doctorProfile: StateFlow<DoctorProfile> = repository.getCurrentDoctorProfile(currentUserId)
        .map { profile ->
            profile ?: DoctorProfile(
                id = "doc_$currentUserId",
                name = repository.getCurrentUserName(),
                medicalLicense = "Pending Verification",
                specialty = "General Medicine",
                qualification = "MBBS",
                experienceYears = 1,
                consultationFee = 50.0,
                bio = "Registered Doctor"
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DoctorProfile(
                id = "doc_$currentUserId",
                name = repository.getCurrentUserName(),
                medicalLicense = "",
                specialty = "General Medicine",
                qualification = "",
                experienceYears = 1,
                consultationFee = 50.0,
                bio = ""
            )
        )

    val availableSlots: StateFlow<List<TimeSlot>> = repository.getSlotsForDoctor("doc_$currentUserId")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val appointments: StateFlow<List<Appointment>> = repository.getAppointmentsForDoctor("doc_$currentUserId")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayAppointments: StateFlow<List<Appointment>> = appointments.map { list ->
        list.filter { it.appointmentDate.equals("Today", ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            repository.toggleDoctorAvailability(currentUserId, isAvailable)
        }
    }

    fun addSlot(date: String, time: String) {
        if (date.isNotBlank() && time.isNotBlank()) {
            viewModelScope.launch {
                repository.addTimeSlot("doc_$currentUserId", date, time)
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
            repository.updateDoctorFee(currentUserId, newFee)
        }
    }

    fun updateProfile(specialty: String, qualification: String, bio: String, hospital: String) {
        viewModelScope.launch {
            repository.updateDoctorProfile(currentUserId, specialty, qualification, bio, hospital)
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
