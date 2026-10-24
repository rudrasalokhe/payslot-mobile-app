package com.docdirect.app.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.docdirect.app.MainActivity

/**
 * WorkManager worker that dispatches local notification reminders
 * 15 minutes and 1 hour before scheduled appointments.
 */
class AppointmentReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val appointmentId = inputData.getString(KEY_APPOINTMENT_ID) ?: return Result.failure()
        val doctorName = inputData.getString(KEY_DOCTOR_NAME) ?: "Doctor"
        val appointmentDate = inputData.getString(KEY_APPOINTMENT_DATE) ?: ""
        val appointmentTime = inputData.getString(KEY_APPOINTMENT_TIME) ?: ""
        val reminderMinutes = inputData.getInt(KEY_REMINDER_MINUTES, 15)

        Log.d(TAG, "Triggering appointment reminder: $appointmentId ($reminderMinutes min before)")

        sendNotification(
            appointmentId = appointmentId,
            doctorName = doctorName,
            appointmentDate = appointmentDate,
            appointmentTime = appointmentTime,
            reminderMinutes = reminderMinutes
        )

        return Result.success()
    }

    private fun sendNotification(
        appointmentId: String,
        doctorName: String,
        appointmentDate: String,
        appointmentTime: String,
        reminderMinutes: Int
    ) {
        val timeLabel = if (reminderMinutes >= 60) {
            "${reminderMinutes / 60} hour"
        } else {
            "$reminderMinutes minutes"
        }

        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("appointment_id", appointmentId)
        }

        val notificationId = (appointmentId.hashCode() xor reminderMinutes) and 0x7FFFFFFF

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            intent,
            pendingIntentFlags
        )

        val title = "Upcoming Appointment in $timeLabel"
        val message = "Your appointment with Dr. $doctorName is scheduled at $appointmentTime."
        val bigText = "You have an upcoming consultation with Dr. $doctorName scheduled for $appointmentTime on $appointmentDate. Please prepare and join $timeLabel in advance."

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
                } else {
                    Log.w(TAG, "Notification permission POST_NOTIFICATIONS not granted.")
                }
            } else {
                NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while showing notification", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to display reminder notification", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Appointment Reminders"
            val descriptionText = "Notifications for upcoming doctor appointments"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val TAG = "AppointmentReminderWorker"
        const val CHANNEL_ID = "appointment_reminders_channel"
        const val KEY_APPOINTMENT_ID = "appointment_id"
        const val KEY_DOCTOR_NAME = "doctor_name"
        const val KEY_APPOINTMENT_DATE = "appointment_date"
        const val KEY_APPOINTMENT_TIME = "appointment_time"
        const val KEY_REMINDER_MINUTES = "reminder_minutes"
    }
}
