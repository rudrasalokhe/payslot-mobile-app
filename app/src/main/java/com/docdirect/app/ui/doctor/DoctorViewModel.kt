package com.docdirect.app.ui.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.*
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DoctorViewModel(
    private val repository: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    val doctorProfile: StateFlow<DoctorProfile> = repository.currentDoctor

    val appointments: StateFlow<List<Appointment>> = repository.appointments.map { list ->
        list.filter { it.doctorId == doctorProfile.value.id }
    }.stateIn(
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
        repository.toggleDoctorAvailability(isAvailable)
    }

    fun addSlot(date: String, time: String) {
        if (date.isNotBlank() && time.isNotBlank()) {
            repository.addTimeSlot(date, time)
        }
    }

    fun deleteSlot(slotId: String) {
        repository.deleteTimeSlot(slotId)
    }

    fun updateFee(newFee: Double) {
        repository.updateDoctorFee(newFee)
    }

    fun updateProfile(specialty: String, qualification: String, bio: String, hospital: String) {
        repository.updateDoctorProfile(specialty, qualification, bio, hospital)
    }

    fun completeAppointment(appointmentId: String) {
        repository.updateAppointmentStatus(appointmentId, AppointmentStatus.COMPLETED)
    }

    fun issuePrescription(appointmentId: String, prescriptionText: String) {
        repository.addPrescription(appointmentId, prescriptionText)
    }
}
