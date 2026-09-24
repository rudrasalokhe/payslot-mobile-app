package com.docdirect.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.docdirect.app.data.local.dao.*
import com.docdirect.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        DoctorEntity::class,
        SlotEntity::class,
        AppointmentEntity::class,
        ChatMessageEntity::class,
        RecordEntity::class,
        FamilyMemberEntity::class,
        InvoiceEntity::class,
        NotificationEntity::class,
        ReviewEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun doctorDao(): DoctorDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun chatDao(): ChatDao
    abstract fun recordDao(): RecordDao
    abstract fun familyDao(): FamilyDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "docdirect_production.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
