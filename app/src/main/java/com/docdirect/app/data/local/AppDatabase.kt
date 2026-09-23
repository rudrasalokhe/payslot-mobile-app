package com.docdirect.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.docdirect.app.data.local.dao.AppointmentDao
import com.docdirect.app.data.local.dao.ChatDao
import com.docdirect.app.data.local.dao.DoctorDao
import com.docdirect.app.data.local.dao.UserDao
import com.docdirect.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        DoctorEntity::class,
        SlotEntity::class,
        AppointmentEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun doctorDao(): DoctorDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun chatDao(): ChatDao

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
