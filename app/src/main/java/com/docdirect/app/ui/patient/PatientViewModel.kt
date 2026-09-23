package com.docdirect.app.ui.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.DoctorProfile
import com.docdirect.app.data.model.TimeSlot
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PatientViewModel(
    private val repository: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    val doctors: StateFlow<List<DoctorProfile>> = repository.doctors
    val appointments: StateFlow<List<Appointment>> = repository.appointments

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredDoctors: StateFlow<List<DoctorProfile>> = doctors.map { list ->
        list.filter { doc ->
            val matchesCategory = _selectedCategory.value == "All" || doc.specialty.equals(_selectedCategory.value, ignoreCase = true)
            val matchesQuery = _searchQuery.value.isBlank() ||
                    doc.name.contains(_searchQuery.value, ignoreCase = true) ||
                    doc.specialty.contains(_searchQuery.value, ignoreCase = true)
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

    fun bookAppointment(
        doctorId: String,
        patientName: String,
        slot: TimeSlot,
        symptoms: String,
        fee: Double
    ): Appointment {
        return repository.bookAppointment(
            doctorId = doctorId,
            patientName = patientName,
            date = slot.date,
            time = slot.time,
            symptoms = symptoms,
            fee = fee
        )
    }
}
