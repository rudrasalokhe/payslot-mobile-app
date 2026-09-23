package com.docdirect.app.data.local.dao

import androidx.room.*
import com.docdirect.app.data.local.entity.DoctorEntity
import com.docdirect.app.data.local.entity.SlotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: DoctorEntity)

    @Update
    suspend fun updateDoctor(doctor: DoctorEntity)

    @Query("SELECT * FROM doctors")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors WHERE id = :doctorId LIMIT 1")
    fun getDoctorByIdFlow(doctorId: String): Flow<DoctorEntity?>

    @Query("SELECT * FROM doctors WHERE userId = :userId LIMIT 1")
    fun getDoctorByUserIdFlow(userId: String): Flow<DoctorEntity?>

    @Query("SELECT * FROM doctors WHERE userId = :userId LIMIT 1")
    suspend fun getDoctorByUserId(userId: String): DoctorEntity?

    // Slot Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: SlotEntity)

    @Query("DELETE FROM slots WHERE id = :slotId")
    suspend fun deleteSlot(slotId: String)

    @Query("SELECT * FROM slots WHERE doctorId = :doctorId")
    fun getSlotsForDoctor(doctorId: String): Flow<List<SlotEntity>>

    @Query("UPDATE slots SET isBooked = 1 WHERE id = :slotId")
    suspend fun markSlotBooked(slotId: String)
}
