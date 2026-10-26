package com.docdirect.app.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.docdirect.app.services.DocDirectNotificationHub

/**
 * WorkManager worker that dispatches local notification call reminders
 * 15 minutes and 5 minutes before scheduled appointments with "Tap to Join" action button.
 */
class AppointmentReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val appointmentId = inputData.getString(KEY_APPOINTMENT_ID) ?: return Result.failure()
        val doctorName = inputData.getString(KEY_DOCTOR_NAME) ?: "Doctor"
        val patientName = inputData.getString(KEY_PATIENT_NAME) ?: "Patient"
        val reminderMinutes = inputData.getInt(KEY_REMINDER_MINUTES, 15)
        val isForDoctor = inputData.getBoolean(KEY_IS_DOCTOR, false)
        val targetUserId = inputData.getString(KEY_TARGET_USER_ID) ?: ""

        Log.d(TAG, "Triggering visit reminder: $appointmentId ($reminderMinutes min before, isDoctor=$isForDoctor)")

        DocDirectNotificationHub.postCallReminder(
            context = applicationContext,
            appointmentId = appointmentId,
            doctorName = doctorName,
            patientName = patientName,
            minutesBefore = reminderMinutes,
            isForDoctor = isForDoctor,
            targetUserId = targetUserId
        )

        return Result.success()
    }

    companion object {
        const val TAG = "AppointmentReminderWorker"
        const val KEY_APPOINTMENT_ID = "appointment_id"
        const val KEY_DOCTOR_NAME = "doctor_name"
        const val KEY_PATIENT_NAME = "patient_name"
        const val KEY_APPOINTMENT_DATE = "appointment_date"
        const val KEY_APPOINTMENT_TIME = "appointment_time"
        const val KEY_REMINDER_MINUTES = "reminder_minutes"
        const val KEY_IS_DOCTOR = "is_doctor"
        const val KEY_TARGET_USER_ID = "target_user_id"
    }
}
