package com.docdirect.app.data.firebase

import android.content.Context
import android.util.Log
import com.docdirect.app.data.local.AppDatabase
import com.docdirect.app.data.local.entity.AppointmentEntity
import com.docdirect.app.data.local.entity.DoctorEntity
import com.docdirect.app.data.model.AppointmentStatus
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"
    private const val FIREBASE_DB_URL = "https://mad-assignment-2-23da9-default-rtdb.firebaseio.com"

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false

    private val database: FirebaseDatabase by lazy {
        try {
            FirebaseDatabase.getInstance(FIREBASE_DB_URL)
        } catch (e: Exception) {
            Log.w(TAG, "Defaulting to FirebaseDatabase instance: ${e.message}")
            FirebaseDatabase.getInstance()
        }
    }

    private val doctorsRef: DatabaseReference by lazy { database.getReference("doctors") }
    private val appointmentsRef: DatabaseReference by lazy { database.getReference("appointments") }

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val db = AppDatabase.getDatabase(context)
        val doctorDao = db.doctorDao()
        val appointmentDao = db.appointmentDao()

        // 1. Listen for real-time doctor profile updates across phones
        try {
            doctorsRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    handleDoctorSnapshot(snapshot, doctorDao)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    handleDoctorSnapshot(snapshot, doctorDao)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Doctor sync cancelled: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach doctor listener: ${e.message}")
        }

        // 2. Listen for real-time appointments across phones
        try {
            appointmentsRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    handleAppointmentSnapshot(snapshot, appointmentDao)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    handleAppointmentSnapshot(snapshot, appointmentDao)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Appointment sync cancelled: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach appointment listener: ${e.message}")
        }
    }

    private fun handleDoctorSnapshot(snapshot: DataSnapshot, doctorDao: com.docdirect.app.data.local.dao.DoctorDao) {
        scope.launch {
            try {
                val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return@launch
                val name = snapshot.child("name").getValue(String::class.java) ?: return@launch
                val specialty = snapshot.child("specialty").getValue(String::class.java) ?: "General Medicine"
                val license = snapshot.child("medicalLicense").getValue(String::class.java) ?: ""
                val qualification = snapshot.child("qualification").getValue(String::class.java) ?: ""
                val exp = snapshot.child("experienceYears").getValue(Int::class.java) ?: 5
                val fee = (snapshot.child("consultationFee").getValue(Double::class.java))
                    ?: (snapshot.child("consultationFee").getValue(Long::class.java)?.toDouble()) ?: 999.0
                val bio = snapshot.child("bio").getValue(String::class.java) ?: ""
                val hospital = snapshot.child("hospitalAffiliation").getValue(String::class.java) ?: ""
                val available = snapshot.child("isAvailable").getValue(Boolean::class.java) ?: true
                val rating = (snapshot.child("rating").getValue(Double::class.java)) ?: 4.9
                val reviewCount = snapshot.child("reviewCount").getValue(Int::class.java) ?: 100

                val entity = DoctorEntity(
                    id = id,
                    userId = id,
                    name = name,
                    medicalLicense = license,
                    specialty = specialty,
                    qualification = qualification,
                    experienceYears = exp,
                    consultationFee = fee,
                    bio = bio,
                    hospitalAffiliation = hospital,
                    isAvailable = available,
                    rating = rating,
                    reviewCount = reviewCount
                )
                doctorDao.insertDoctor(entity)
                Log.d(TAG, "Realtime doctor synced from cloud: ${entity.name}")
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing doctor snapshot: ${e.message}")
            }
        }
    }

    private fun handleAppointmentSnapshot(snapshot: DataSnapshot, appointmentDao: com.docdirect.app.data.local.dao.AppointmentDao) {
        scope.launch {
            try {
                val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return@launch
                val patientId = snapshot.child("patientId").getValue(String::class.java) ?: ""
                val patientName = snapshot.child("patientName").getValue(String::class.java) ?: ""
                val doctorId = snapshot.child("doctorId").getValue(String::class.java) ?: ""
                val doctorName = snapshot.child("doctorName").getValue(String::class.java) ?: ""
                val specialty = snapshot.child("doctorSpecialty").getValue(String::class.java) ?: ""
                val date = snapshot.child("appointmentDate").getValue(String::class.java) ?: ""
                val time = snapshot.child("appointmentTime").getValue(String::class.java) ?: ""
                val symptoms = snapshot.child("symptoms").getValue(String::class.java) ?: ""
                val fee = (snapshot.child("feePaid").getValue(Double::class.java)) ?: 999.0
                val statusStr = snapshot.child("status").getValue(String::class.java) ?: "UPCOMING"
                val prescription = snapshot.child("prescription").getValue(String::class.java) ?: ""

                val entity = AppointmentEntity(
                    id = id,
                    patientId = patientId,
                    patientName = patientName,
                    doctorId = doctorId,
                    doctorName = doctorName,
                    doctorSpecialty = specialty,
                    appointmentDate = date,
                    appointmentTime = time,
                    symptoms = symptoms,
                    feePaid = fee,
                    status = AppointmentStatus.valueOf(statusStr),
                    prescription = prescription
                )
                appointmentDao.insertAppointment(entity)
                Log.d(TAG, "Realtime appointment synced from cloud: ${entity.id}")
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing appointment snapshot: ${e.message}")
            }
        }
    }

    fun syncDoctorToCloud(doctor: DoctorEntity) {
        scope.launch {
            try {
                val data = mapOf(
                    "id" to doctor.id,
                    "name" to doctor.name,
                    "medicalLicense" to doctor.medicalLicense,
                    "specialty" to doctor.specialty,
                    "qualification" to doctor.qualification,
                    "experienceYears" to doctor.experienceYears,
                    "consultationFee" to doctor.consultationFee,
                    "bio" to doctor.bio,
                    "hospitalAffiliation" to doctor.hospitalAffiliation,
                    "isAvailable" to doctor.isAvailable,
                    "rating" to doctor.rating,
                    "reviewCount" to doctor.reviewCount,
                    "updatedAt" to System.currentTimeMillis()
                )
                doctorsRef.child(doctor.id).setValue(data)
                Log.d(TAG, "Doctor pushed to Firebase: ${doctor.name}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to push doctor to Firebase: ${e.message}")
            }
        }
    }

    fun syncAppointmentToCloud(appointment: AppointmentEntity) {
        scope.launch {
            try {
                val data = mapOf(
                    "id" to appointment.id,
                    "patientId" to appointment.patientId,
                    "patientName" to appointment.patientName,
                    "doctorId" to appointment.doctorId,
                    "doctorName" to appointment.doctorName,
                    "doctorSpecialty" to appointment.doctorSpecialty,
                    "appointmentDate" to appointment.appointmentDate,
                    "appointmentTime" to appointment.appointmentTime,
                    "symptoms" to appointment.symptoms,
                    "feePaid" to appointment.feePaid,
                    "status" to appointment.status.name,
                    "prescription" to appointment.prescription,
                    "createdAt" to System.currentTimeMillis()
                )
                appointmentsRef.child(appointment.id).setValue(data)
                Log.d(TAG, "Appointment pushed to Firebase: ${appointment.id}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to push appointment to Firebase: ${e.message}")
            }
        }
    }

    fun syncPrescriptionToCloud(appointmentId: String, prescriptionText: String) {
        scope.launch {
            try {
                appointmentsRef.child(appointmentId).child("prescription").setValue(prescriptionText)
                appointmentsRef.child(appointmentId).child("status").setValue(AppointmentStatus.COMPLETED.name)
                Log.d(TAG, "Prescription pushed to Firebase for: $appointmentId")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to push prescription to Firebase: ${e.message}")
            }
        }
    }
}
