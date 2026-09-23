package com.docdirect.app.data.model

data class ChatMessage(
    val id: String,
    val appointmentId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val messageText: String,
    val isPrescription: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class PaymentTransaction(
    val transactionId: String,
    val appointmentId: String,
    val patientName: String,
    val doctorName: String,
    val amount: Double,
    val paymentMethod: String, // "Credit/Debit Card", "UPI / Wallet", "Net Banking"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS"
)
