package com.docdirect.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docdirect.app.data.model.Appointment
import com.docdirect.app.data.model.ChatMessage
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ChatViewModel(
    private val repository: AppRepository = AppRepository.getInstance()
) : ViewModel() {

    val currentRole: StateFlow<UserRole> = repository.currentRole

    fun getAppointment(appointmentId: String): StateFlow<Appointment?> {
        return repository.appointments.map { list ->
            list.find { it.id == appointmentId }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun getMessagesForAppointment(appointmentId: String): StateFlow<List<ChatMessage>> {
        return repository.chatMessages.map { messages ->
            messages.filter { it.appointmentId == appointmentId }
        }.stateIn(
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
            repository.sendMessage(
                appointmentId = appointmentId,
                senderId = senderId,
                senderName = senderName,
                senderRole = role,
                messageText = text
            )
        }
    }

    fun issuePrescription(appointmentId: String, prescriptionText: String) {
        repository.addPrescription(appointmentId, prescriptionText)
    }
}
