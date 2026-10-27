package com.docdirect.app.services

import android.util.Log
import com.docdirect.app.data.auth.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging Service for PaySlot / DocDirect.
 * Receives remote push notifications for:
 * 1. Incoming / scheduled telehealth video call alerts (15m & 5m reminders with "Tap to Join").
 * 2. Instant real-time recipient chat notifications.
 * 3. Digital prescription issuance alerts.
 * 4. Appointment booking confirmations & schedule updates.
 */
class DocDirectFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "New Firebase FCM Registration Token: $token")
        SessionManager.getInstance(applicationContext).saveFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val payloadType = data["type"] ?: data["event_type"] ?: ""

        when {
            payloadType.contains("chat", ignoreCase = true) || payloadType.contains("message", ignoreCase = true) -> {
                val appointmentId = data["appointment_id"] ?: ""
                val senderName = data["sender_name"] ?: "Doctor"
                val messageText = data["message"] ?: data["body"] ?: remoteMessage.notification?.body ?: "New message received"
                val recipientId = data["recipient_id"] ?: SessionManager.getInstance(applicationContext).getUserId() ?: ""

                DocDirectNotificationHub.postChatMessageNotification(
                    context = applicationContext,
                    appointmentId = appointmentId,
                    senderName = senderName,
                    messageText = messageText,
                    recipientUserId = recipientId
                )
            }

            payloadType.contains("prescription", ignoreCase = true) || payloadType.contains("rx", ignoreCase = true) -> {
                val appointmentId = data["appointment_id"] ?: ""
                val doctorName = data["doctor_name"] ?: "Your Doctor"
                val summary = data["prescription"] ?: data["summary"] ?: "Digital prescription issued"
                val patientId = data["patient_id"] ?: SessionManager.getInstance(applicationContext).getUserId() ?: ""

                DocDirectNotificationHub.postPrescriptionAlert(
                    context = applicationContext,
                    appointmentId = appointmentId,
                    doctorName = doctorName,
                    prescriptionSummary = summary,
                    patientUserId = patientId
                )
            }

            payloadType.contains("call_reminder", ignoreCase = true) || payloadType.contains("reminder", ignoreCase = true) -> {
                val appointmentId = data["appointment_id"] ?: ""
                val doctorName = data["doctor_name"] ?: "Doctor"
                val patientName = data["patient_name"] ?: "Patient"
                val minutesBefore = data["minutes_before"]?.toIntOrNull() ?: 15
                val isForDoctor = data["is_doctor"]?.toBoolean() ?: false
                val targetUserId = data["target_user_id"] ?: SessionManager.getInstance(applicationContext).getUserId() ?: ""

                DocDirectNotificationHub.postCallReminder(
                    context = applicationContext,
                    appointmentId = appointmentId,
                    doctorName = doctorName,
                    patientName = patientName,
                    minutesBefore = minutesBefore,
                    isForDoctor = isForDoctor,
                    targetUserId = targetUserId
                )
            }

            payloadType.contains("call", ignoreCase = true) -> {
                val doctorName = data["doctor_name"] ?: "Your Doctor"
                val appointmentId = data["appointment_id"] ?: ""
                DocDirectNotificationHub.postCallReminder(
                    context = applicationContext,
                    appointmentId = appointmentId,
                    doctorName = doctorName,
                    patientName = "Patient",
                    minutesBefore = 0,
                    isForDoctor = false
                )
            }

            else -> {
                val title = remoteMessage.notification?.title ?: data["title"] ?: "Payslot Healthcare"
                val body = remoteMessage.notification?.body ?: data["body"] ?: "You have a new update regarding your care."
                val appointmentId = data["appointment_id"] ?: ""
                val doctorName = data["doctor_name"] ?: "Doctor"
                DocDirectNotificationHub.postCallReminder(
                    context = applicationContext,
                    appointmentId = appointmentId,
                    doctorName = doctorName,
                    minutesBefore = 15,
                    isForDoctor = false
                )
            }
        }
    }

    companion object {
        const val TAG = "DocDirectFCMService"
    }
}
