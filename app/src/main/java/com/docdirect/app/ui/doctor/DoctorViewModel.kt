package com.docdirect.app.ui.doctor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.local.entity.RecordEntity
import com.docdirect.app.data.local.entity.InvoiceEntity
import com.docdirect.app.data.model.*
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DoctorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)

    private fun resolveDoctorId(): String {
        val currentUserId = repository.getCurrentUserId() ?: ""
        return when {
            currentUserId.equals("user_mira_shah", ignoreCase = true) -> "doc_mira_shah"
            currentUserId.startsWith("doc_") -> currentUserId
            currentUserId.isNotBlank() -> "doc_$currentUserId"
            else -> "doc_mira_shah"
        }
    }

    private val _activeDoctorId = MutableStateFlow(resolveDoctorId())
    val doctorId: String get() = _activeDoctorId.value

    fun refreshDoctorSession() {
        _activeDoctorId.value = resolveDoctorId()
    }

    val doctorProfile: StateFlow<DoctorProfile> = _activeDoctorId
        .flatMapLatest { id ->
            repository.getCurrentDoctorProfile(id).map { profile ->
                profile ?: DoctorProfile(
                    id = id,
                    name = if (repository.getCurrentUserName().isNotBlank()) repository.getCurrentUserName() else "Dr Mira Shah",
                    medicalLicense = "MCI-48921-DERM",
                    specialty = "Dermatology",
                    qualification = "MBBS, MD (Dermatology)",
                    experienceYears = 8,
                    consultationFee = 1200.0,
                    bio = "Specializes in clinical and aesthetic dermatology at Bandra Skin Clinic.",
                    hospitalAffiliation = "Bandra Skin Clinic, Mumbai"
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DoctorProfile(
                id = "doc_mira_shah",
                name = "Dr Mira Shah",
                medicalLicense = "MCI-48921-DERM",
                specialty = "Dermatology",
                qualification = "MBBS, MD (Dermatology)",
                experienceYears = 8,
                consultationFee = 1200.0,
                bio = "Specializes in clinical and aesthetic dermatology."
            )
        )

    val availableSlots: StateFlow<List<TimeSlot>> = _activeDoctorId
        .flatMapLatest { id -> repository.getSlotsForDoctor(id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val appointments: StateFlow<List<Appointment>> = _activeDoctorId
        .flatMapLatest { id -> repository.getAppointmentsForDoctor(id) }
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

    val invoices: StateFlow<List<InvoiceEntity>> = repository.getAllInvoices().stateIn(
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

    fun updateFullProfile(
        name: String,
        medicalLicense: String,
        specialty: String,
        qualification: String,
        experienceYears: Int,
        fee: Double,
        hospital: String,
        bio: String,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            repository.updateDoctorFullProfile(
                doctorId = doctorId,
                name = name,
                medicalLicense = medicalLicense,
                specialty = specialty,
                qualification = qualification,
                experienceYears = experienceYears,
                fee = fee,
                hospital = hospital,
                bio = bio
            )
            onComplete?.invoke()
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

    fun getRecordsForAppointment(appointmentId: String): Flow<List<RecordEntity>> {
        return repository.getRecordsForAppointment(appointmentId)
    }
}
