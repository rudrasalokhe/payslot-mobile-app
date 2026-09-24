package com.docdirect.app.ui.patient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.local.entity.*
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.DoctorProfile
import com.docdirect.app.data.model.TimeSlot
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.*

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)
    val currentUserId = repository.getCurrentUserId() ?: "user_aarav_mehta"
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

    val records: StateFlow<List<RecordEntity>> = repository.getRecordsForPatient(currentUserId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val familyMembers: StateFlow<List<FamilyMemberEntity>> = repository.getFamilyMembers(currentUserId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val invoices: StateFlow<List<InvoiceEntity>> = repository.getAllInvoices().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notifications: StateFlow<List<NotificationEntity>> = repository.getNotificationsForUser(currentUserId).stateIn(
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
        val apt = repository.bookAppointment(
            doctorId = doctorId,
            doctorName = doctorName,
            doctorSpecialty = doctorSpecialty,
            patientId = currentUserId,
            patientName = if (currentUserName.isNotBlank()) currentUserName else "Aarav Mehta",
            slotId = slot.id,
            date = slot.date,
            time = slot.time,
            symptoms = symptoms,
            fee = fee
        )
        // Also automatically create the invoice in database
        repository.createInvoice(
            appointmentId = apt.id,
            patientName = apt.patientName,
            doctorName = apt.doctorName,
            specialty = apt.doctorSpecialty,
            fee = fee - 49.0,
            platformFee = 49.0,
            paymentMethod = "UPI (Google Pay)"
        )
        return apt
    }

    suspend fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        repository.rescheduleAppointment(appointmentId, newDate, newTime)
    }

    suspend fun cancelAppointment(appointmentId: String) {
        repository.cancelAppointment(appointmentId)
    }

    suspend fun addRecord(title: String, category: String, fileSize: String) {
        repository.addRecord(title, category, fileSize, currentUserId)
    }

    suspend fun updateRecordSharing(recordId: String, isShared: Boolean) {
        repository.updateRecordSharing(recordId, isShared)
    }

    suspend fun deleteRecord(recordId: String) {
        repository.deleteRecord(recordId)
    }

    suspend fun addFamilyMember(name: String, relationship: String, age: Int, gender: String, dob: String) {
        repository.addFamilyMember(currentUserId, name, relationship, age, gender, dob)
    }

    fun getReviewsForDoctor(doctorId: String): Flow<List<ReviewEntity>> {
        return repository.getReviewsForDoctor(doctorId)
    }

    suspend fun addReview(doctorId: String, patientName: String, rating: Int, text: String) {
        repository.addReview(doctorId, patientName, rating, text)
    }

    suspend fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }
}
