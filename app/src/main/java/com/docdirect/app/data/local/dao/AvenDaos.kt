package com.docdirect.app.data.local.dao

import androidx.room.*
import com.docdirect.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)

    @Query("SELECT * FROM health_records WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun getRecordsForPatient(patientId: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM health_records ORDER BY createdAt DESC")
    fun getAllRecords(): Flow<List<RecordEntity>>

    @Query("SELECT COUNT(*) FROM health_records")
    suspend fun getRecordCount(): Int

    @Query("UPDATE health_records SET isSharedWithDoctor = :isShared WHERE id = :recordId")
    suspend fun updateSharingStatus(recordId: String, isShared: Boolean)

    @Query("DELETE FROM health_records WHERE id = :recordId")
    suspend fun deleteRecord(recordId: String)
}

@Dao
interface FamilyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMemberEntity)

    @Query("SELECT * FROM family_members WHERE patientId = :patientId")
    fun getFamilyMembers(patientId: String): Flow<List<FamilyMemberEntity>>

    @Query("SELECT * FROM family_members")
    fun getAllFamilyMembers(): Flow<List<FamilyMemberEntity>>

    @Query("SELECT COUNT(*) FROM family_members")
    suspend fun getFamilyCount(): Int
}

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE appointmentId = :appointmentId LIMIT 1")
    fun getInvoiceForAppointment(appointmentId: String): Flow<InvoiceEntity?>

    @Query("SELECT COUNT(*) FROM invoices")
    suspend fun getInvoiceCount(): Int

    @Query("UPDATE invoices SET status = :newStatus WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: String, newStatus: String)
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY id DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getNotificationCount(): Int

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE doctorId = :doctorId ORDER BY createdAt DESC")
    fun getReviewsForDoctor(doctorId: String): Flow<List<ReviewEntity>>

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun getReviewCount(): Int
}
