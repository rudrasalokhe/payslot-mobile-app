package com.docdirect.app.data.local.dao

import androidx.room.*
import com.docdirect.app.data.local.entity.VitalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVital(vital: VitalEntity)

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY timestamp ASC")
    fun getVitalsForPatient(patientId: String): Flow<List<VitalEntity>>

    @Query("SELECT * FROM vitals WHERE type = :type ORDER BY timestamp ASC")
    fun getVitalsByType(type: String): Flow<List<VitalEntity>>

    @Query("SELECT * FROM vitals ORDER BY timestamp DESC")
    fun getAllVitals(): Flow<List<VitalEntity>>

    @Query("SELECT COUNT(*) FROM vitals")
    suspend fun getVitalCount(): Int

    @Query("DELETE FROM vitals WHERE id = :id")
    suspend fun deleteVital(id: String)
}
