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
import com.docdirect.app.data.local.AppDatabase
import com.docdirect.app.data.local.entity.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Centralized Notification Manager for PaySlot / DocDirect.
 * Handles Real-time Push & Local Notifications for:
 * 1. Call Reminders (15m & 5m before visit with "Tap to Join" action button).
 * 2. Instant Chat Notifications when a new message arrives.
 * 3. Digital Prescription Alerts when clinician issues prescriptions.
 */
object DocDirectNotificationHub {

    private const val TAG = "DocDirectNotifHub"

    const val CHANNEL_CALLS_ID = "channel_doctor_calls"
    const val CHANNEL_CHAT_ID = "channel_chat_messages"
    const val CHANNEL_APPOINTMENTS_ID = "channel_appointment_updates"
    const val CHANNEL_PRESCRIPTIONS_ID = "channel_prescriptions"

    private const val BASE_CALL_NOTIFICATION_ID = 3000
    private const val BASE_CHAT_NOTIFICATION_ID = 4000
    private const val BASE_RX_NOTIFICATION_ID = 5000

    private val scope = CoroutineScope(Dispatchers.IO)

    fun initializeChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val callSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()

            // 1. Call Reminders & Video Visits
            val callChannel = NotificationChannel(
                CHANNEL_CALLS_ID,
                "Doctor Video Consultations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent consultation alerts and 15m/5m reminders with Tap to Join"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
                setSound(callSound, audioAttributes)
            }

            // 2. Chat Notifications
            val chatChannel = NotificationChannel(
                CHANNEL_CHAT_ID,
                "Patient & Doctor Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Instant notifications when doctor or patient sends a chat message"
                enableVibration(true)
            }

            // 3. Appointments & Schedule
            val appointmentChannel = NotificationChannel(
                CHANNEL_APPOINTMENTS_ID,
                "Appointment Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Booking confirmations, receipts and appointment schedule changes"
                enableVibration(true)
            }

            // 4. Prescriptions
            val rxChannel = NotificationChannel(
                CHANNEL_PRESCRIPTIONS_ID,
                "Prescriptions & Clinical Documents",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Instant alerts when doctors issue digital prescriptions"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(
                listOf(callChannel, chatChannel, appointmentChannel, rxChannel)
            )
        }
    }

    /**
     * Dispatches Call Reminder (15 min or 5 min before scheduled visit)
     * with an interactive "Tap to Join" action button that launches Agora video consultation.
     */
    fun postCallReminder(
        context: Context,
        appointmentId: String,
        doctorName: String,
        patientName: String = "",
        minutesBefore: Int,
        isForDoctor: Boolean = false,
        targetUserId: String = ""
    ) {
        initializeChannels(context)

        val title = if (isForDoctor) {
            "Upcoming Visit with $patientName in $minutesBefore min"
        } else {
            "Consultation with Dr. $doctorName in $minutesBefore min"
        }

        val message = if (isForDoctor) {
            "Your appointment with $patientName starts in $minutesBefore minutes. Tap below to join consultation room."
        } else {
            "Your video consultation with Dr. $doctorName is starting in $minutesBefore minutes. Tap below to join."
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "join_call")
            putExtra("appointment_id", appointmentId)
            putExtra("doctor_name", doctorName)
            putExtra("patient_name", patientName)
            putExtra("channel_name", appointmentId)
            putExtra("is_doctor", isForDoctor)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val notificationId = BASE_CALL_NOTIFICATION_ID + (appointmentId.hashCode() xor minutesBefore).let { Math.abs(it) % 1000 }
        val pendingIntent = PendingIntent.getActivity(context, notificationId, intent, pendingFlags)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_CALLS_ID)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message You will be connected live through high-definition encrypted Agora video."))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_camera, "Tap to Join", pendingIntent)
            .build()

        notifySafely(context, notificationId, notification)

        // Record in in-app notification center
        val timeLabel = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val recipientId = if (targetUserId.isNotBlank()) targetUserId else if (isForDoctor) "doctor_mira_shah" else "user_aarav_mehta"
        recordInNotificationCenter(
            context = context,
            notificationId = "call_remind_${appointmentId}_${minutesBefore}m",
            userId = recipientId,
            title = title,
            subtitle = message,
            timestamp = "Today, $timeLabel",
            deepLink = "video_call/$appointmentId/${if (isForDoctor) patientName else doctorName}?isDoctor=$isForDoctor"
        )
    }

    /**
     * Dispatches real-time push notification when a chat message arrives.
     */
    fun postChatMessageNotification(
        context: Context,
        appointmentId: String,
        senderName: String,
        messageText: String,
        recipientUserId: String
    ) {
        initializeChannels(context)

        val title = "Message from $senderName"
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "open_chat")
            putExtra("appointment_id", appointmentId)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val notificationId = BASE_CHAT_NOTIFICATION_ID + (appointmentId.hashCode() xor senderName.hashCode()).let { Math.abs(it) % 1000 }
        val pendingIntent = PendingIntent.getActivity(context, notificationId, intent, pendingFlags)

        val notification = NotificationCompat.Builder(context, CHANNEL_CHAT_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(title)
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_send, "Reply", pendingIntent)
            .build()

        notifySafely(context, notificationId, notification)

        val timeLabel = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        recordInNotificationCenter(
            context = context,
            notificationId = "chat_${System.currentTimeMillis()}",
            userId = recipientUserId.ifBlank { "user_aarav_mehta" },
            title = title,
            subtitle = messageText,
            timestamp = "Today, $timeLabel",
            deepLink = "chat/$appointmentId"
        )
    }

    /**
     * Dispatches real-time push notification as soon as doctor issues a prescription.
     */
    fun postPrescriptionAlert(
        context: Context,
        appointmentId: String,
        doctorName: String,
        prescriptionSummary: String,
        patientUserId: String
    ) {
        initializeChannels(context)

        val title = "Prescription Issued by Dr. $doctorName"
        val message = "Your doctor has issued your official digital prescription and care regimen."

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "view_prescription")
            putExtra("appointment_id", appointmentId)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val notificationId = BASE_RX_NOTIFICATION_ID + appointmentId.hashCode().let { Math.abs(it) % 1000 }
        val pendingIntent = PendingIntent.getActivity(context, notificationId, intent, pendingFlags)

        val fullText = "$message Regimen: ${prescriptionSummary.take(120)}. Available in your Health Records."

        val notification = NotificationCompat.Builder(context, CHANNEL_PRESCRIPTIONS_ID)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_view, "View Rx PDF", pendingIntent)
            .build()

        notifySafely(context, notificationId, notification)

        val timeLabel = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        recordInNotificationCenter(
            context = context,
            notificationId = "rx_${appointmentId}_${System.currentTimeMillis()}",
            userId = patientUserId.ifBlank { "user_aarav_mehta" },
            title = title,
            subtitle = fullText,
            timestamp = "Today, $timeLabel",
            deepLink = "aven_records"
        )
    }

    private fun recordInNotificationCenter(
        context: Context,
        notificationId: String,
        userId: String,
        title: String,
        subtitle: String,
        timestamp: String,
        deepLink: String
    ) {
        scope.launch {
            try {
                val db = AppDatabase.getDatabase(context)
                db.notificationDao().insertNotification(
                    NotificationEntity(
                        id = notificationId,
                        userId = userId,
                        title = title,
                        subtitle = subtitle,
                        timestamp = timestamp,
                        isRead = false,
                        deepLink = deepLink
                    )
                )
                Log.d(TAG, "Recorded notification in Hub for user: $userId - $title")
            } catch (e: Exception) {
                Log.w(TAG, "Could not insert notification into local DB: ${e.message}")
            }
        }
    }

    private fun notifySafely(context: Context, notificationId: Int, notification: android.app.Notification) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat.from(context).notify(notificationId, notification)
                } else {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted.")
                }
            } else {
                NotificationManagerCompat.from(context).notify(notificationId, notification)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while displaying notification", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying notification", e)
        }
    }
}
