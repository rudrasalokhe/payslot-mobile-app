package com.docdirect.app.data.local.dao

import androidx.room.*
import com.docdirect.app.data.local.entity.AppointmentEntity
import com.docdirect.app.data.local.entity.ChatMessageEntity
import com.docdirect.app.data.model.AppointmentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY createdAt DESC")
    fun getAppointmentsForDoctor(doctorId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun getAppointmentsForPatient(patientId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :appointmentId LIMIT 1")
    fun getAppointmentById(appointmentId: String): Flow<AppointmentEntity?>

    @Query("UPDATE appointments SET status = :status WHERE id = :appointmentId")
    suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus)

    @Query("UPDATE appointments SET prescription = :prescription, status = 'COMPLETED' WHERE id = :appointmentId")
    suspend fun addPrescription(appointmentId: String, prescription: String)

    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun getAppointmentCount(): Int

    @Query("UPDATE appointments SET appointmentDate = :newDate, appointmentTime = :newTime WHERE id = :appointmentId")
    suspend fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String)

    @Query("UPDATE appointments SET status = 'CANCELLED' WHERE id = :appointmentId")
    suspend fun cancelAppointment(appointmentId: String)
}

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE appointmentId = :appointmentId ORDER BY timestamp ASC")
    fun getMessagesForAppointment(appointmentId: String): Flow<List<ChatMessageEntity>>
}
