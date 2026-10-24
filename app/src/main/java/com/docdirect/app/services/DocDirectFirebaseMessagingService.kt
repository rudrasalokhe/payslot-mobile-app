package com.docdirect.app.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.docdirect.app.MainActivity
import com.docdirect.app.data.auth.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging Service for PaySlot / DocDirect.
 * Receives remote push notifications for:
 * 1. Incoming / started telehealth video consultations (high priority call alerts).
 * 2. Real-time appointment booking confirmations & scheduling updates.
 */
class DocDirectFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "New Firebase FCM Registration Token: $token")
        // Persist token in session manager for backend dispatch
        SessionManager.getInstance(applicationContext).saveFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val payloadType = data["type"] ?: data["event_type"] ?: ""

        when {
            payloadType.contains("call", ignoreCase = true) -> {
                handleDoctorCallNotification(data)
            }
            payloadType.contains("appointment", ignoreCase = true) || payloadType.contains("confirm", ignoreCase = true) -> {
                handleAppointmentConfirmedNotification(data)
            }
            else -> {
                // General or fallback notification
                val title = remoteMessage.notification?.title ?: data["title"] ?: "DocDirect Healthcare"
                val body = remoteMessage.notification?.body ?: data["body"] ?: "You have a new update regarding your care."
                sendGeneralNotification(title, body, data)
            }
        }
    }

    /**
     * Dispatches urgent call notification when doctor enters consultation room.
     */
    private fun handleDoctorCallNotification(data: Map<String, String>) {
        createNotificationChannels()

        val doctorName = data["doctor_name"] ?: "Your Doctor"
        val channelName = data["channel_name"] ?: data["room_id"] ?: "telehealth_room"
        val appointmentId = data["appointment_id"] ?: ""

        val title = "Doctor Started Consultation"
        val message = "Dr. $doctorName has initiated your video visit. Tap to join now."

        // Deep-link intent directly into Agora consultation room
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "join_call")
            putExtra("channel_name", channelName)
            putExtra("doctor_name", doctorName)
            putExtra("appointment_id", appointmentId)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(this, CALL_NOTIFICATION_ID, intent, pendingFlags)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, CHANNEL_CALLS_ID)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Dr. $doctorName is currently waiting for you in the telehealth consultation room. Please tap below to connect your video and audio."))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_camera, "Join Call Now", pendingIntent)
            .build()

        notifySafely(CALL_NOTIFICATION_ID, notification)
    }

    /**
     * Dispatches confirmation notification when an appointment is confirmed by clinic or payment.
     */
    private fun handleAppointmentConfirmedNotification(data: Map<String, String>) {
        createNotificationChannels()

        val doctorName = data["doctor_name"] ?: "Doctor"
        val appointmentDate = data["date"] ?: "Today"
        val appointmentTime = data["time"] ?: "Scheduled Time"
        val appointmentId = data["appointment_id"] ?: ""

        val title = "Appointment Confirmed"
        val message = "Your appointment with Dr. $doctorName on $appointmentDate ($appointmentTime) is confirmed."

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "view_appointment")
            putExtra("appointment_id", appointmentId)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(this, APPOINTMENT_NOTIFICATION_ID, intent, pendingFlags)

        val notification = NotificationCompat.Builder(this, CHANNEL_APPOINTMENTS_ID)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Your appointment with Dr. $doctorName is confirmed for $appointmentTime on $appointmentDate. A calendar reminder and slot receipt have been generated."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notifySafely(APPOINTMENT_NOTIFICATION_ID, notification)
    }

    private fun sendGeneralNotification(title: String, body: String, data: Map<String, String>) {
        createNotificationChannels()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data.forEach { (key, value) -> putExtra(key, value) }
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(this, GENERAL_NOTIFICATION_ID, intent, pendingFlags)

        val notification = NotificationCompat.Builder(this, CHANNEL_APPOINTMENTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notifySafely(GENERAL_NOTIFICATION_ID, notification)
    }

    private fun notifySafely(notificationId: Int, notification: android.app.Notification) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat.from(this).notify(notificationId, notification)
                } else {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted; notification skipped.")
                }
            } else {
                NotificationManagerCompat.from(this).notify(notificationId, notification)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while displaying notification", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying notification", e)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // Channel 1: Doctor Video Consultations (Max Importance + Custom Vibration)
            val callSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()

            val callChannel = NotificationChannel(
                CHANNEL_CALLS_ID,
                "Doctor Video Consultations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts when a doctor begins your scheduled telehealth call"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
                setSound(callSound, audioAttributes)
            }

            // Channel 2: Appointment Confirmations & Updates
            val appointmentChannel = NotificationChannel(
                CHANNEL_APPOINTMENTS_ID,
                "Appointment Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for appointment booking confirmations, schedule changes, and receipts"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(callChannel)
            notificationManager.createNotificationChannel(appointmentChannel)
        }
    }

    companion object {
        const val TAG = "DocDirectFCMService"
        const val CHANNEL_CALLS_ID = "channel_doctor_calls"
        const val CHANNEL_APPOINTMENTS_ID = "channel_appointment_updates"

        private const val CALL_NOTIFICATION_ID = 2001
        private const val APPOINTMENT_NOTIFICATION_ID = 2002
        private const val GENERAL_NOTIFICATION_ID = 2003
    }
}
