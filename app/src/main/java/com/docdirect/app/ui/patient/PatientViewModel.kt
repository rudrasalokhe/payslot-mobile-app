package com.docdirect.app.ui.patient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.DoctorProfile
import com.docdirect.app.data.model.TimeSlot
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.*

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)
    val currentUserId = repository.getCurrentUserId() ?: ""
    val currentUserName = repository.getCurrentUserName()

    val doctors: StateFlow<List<DoctorProfile>> = repository.doctors.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val appointments: StateFlow<List<Appointment>> = repository.getAppointmentsForPatient(currentUserId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredDoctors: StateFlow<List<DoctorProfile>> = combine(doctors, _searchQuery, _selectedCategory) { docList, query, cat ->
        docList.filter { doc ->
            val matchesCategory = cat == "All" || doc.specialty.equals(cat, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    doc.name.contains(query, ignoreCase = true) ||
                    doc.specialty.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun getDoctorById(doctorId: String): DoctorProfile? {
        return doctors.value.find { it.id == doctorId }
    }

    fun getSlotsForDoctor(doctorId: String): Flow<List<TimeSlot>> {
        return repository.getSlotsForDoctor(doctorId)
    }

    suspend fun bookAppointment(
        doctorId: String,
        doctorName: String,
        doctorSpecialty: String,
        slot: TimeSlot,
        symptoms: String,
        fee: Double
    ): Appointment {
        return repository.bookAppointment(
            doctorId = doctorId,
            doctorName = doctorName,
            doctorSpecialty = doctorSpecialty,
            patientId = currentUserId,
            patientName = if (currentUserName.isNotBlank()) currentUserName else "Patient",
            slotId = slot.id,
            date = slot.date,
            time = slot.time,
            symptoms = symptoms,
            fee = fee
        )
    }

    suspend fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        repository.rescheduleAppointment(appointmentId, newDate, newTime)
    }

    suspend fun cancelAppointment(appointmentId: String) {
        repository.cancelAppointment(appointmentId)
    }
}
