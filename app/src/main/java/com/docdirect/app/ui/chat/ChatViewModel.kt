package com.docdirect.app.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.ChatMessage
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)

    fun getCurrentRole(): UserRole = repository.getCurrentUserRole()
    fun getCurrentUserId(): String = repository.getCurrentUserId() ?: ""
    fun getCurrentUserName(): String = repository.getCurrentUserName()

    fun getAppointment(appointmentId: String): StateFlow<Appointment?> {
        return repository.getAppointmentById(appointmentId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun getMessagesForAppointment(appointmentId: String): StateFlow<List<ChatMessage>> {
        return repository.getMessagesForAppointment(appointmentId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun sendMessage(
        appointmentId: String,
        senderId: String,
        senderName: String,
        role: UserRole,
        text: String
    ) {
        if (text.isNotBlank()) {
            viewModelScope.launch {
                repository.sendMessage(
                    appointmentId = appointmentId,
                    senderId = senderId,
                    senderName = senderName,
                    senderRole = role,
                    messageText = text
                )
            }
        }
    }

    fun issuePrescription(appointmentId: String, prescriptionText: String) {
        viewModelScope.launch {
            repository.addPrescription(appointmentId, prescriptionText)
        }
    }
}
