package com.docdirect.app.data.firebase

import android.content.Context
import android.util.Log
import com.docdirect.app.data.local.AppDatabase
import com.docdirect.app.data.local.dao.ChatDao
import com.docdirect.app.data.local.entity.AppointmentEntity
import com.docdirect.app.data.local.entity.ChatMessageEntity
import com.docdirect.app.data.local.entity.DoctorEntity
import com.docdirect.app.data.local.entity.UserEntity
import com.docdirect.app.data.model.AppointmentStatus
import com.docdirect.app.data.model.UserRole
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

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
    private val usersRef: DatabaseReference by lazy { database.getReference("users") }
    private val chatsRef: DatabaseReference by lazy { database.getReference("chats") }
    private val activeChatListeners = mutableMapOf<String, ChildEventListener>()

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val db = AppDatabase.getDatabase(context)
        val doctorDao = db.doctorDao()
        val appointmentDao = db.appointmentDao()
        val userDao = db.userDao()

        // 1. Listen for real-time user accounts (doctors & patients across phones)
        try {
            usersRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    handleUserSnapshot(snapshot, userDao)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    handleUserSnapshot(snapshot, userDao)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "User sync cancelled: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach user listener: ${e.message}")
        }

        // 2. Listen for real-time doctor profile updates across phones
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

    private fun handleUserSnapshot(snapshot: DataSnapshot, userDao: com.docdirect.app.data.local.dao.UserDao) {
        scope.launch {
            try {
                val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return@launch
                val name = snapshot.child("name").getValue(String::class.java) ?: return@launch
                val email = snapshot.child("email").getValue(String::class.java)?.lowercase()?.trim() ?: return@launch
                val passwordHash = snapshot.child("passwordHash").getValue(String::class.java) ?: return@launch
                val roleStr = snapshot.child("role").getValue(String::class.java) ?: UserRole.PATIENT.name
                val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.PATIENT }

                val user = UserEntity(
                    id = id,
                    name = name,
                    email = email,
                    passwordHash = passwordHash,
                    role = role
                )
                userDao.insertUser(user)
                Log.d(TAG, "User synced from cloud: ${user.email} (${user.role})")
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing user snapshot: ${e.message}")
            }
        }
    }

    fun syncUserToCloud(user: UserEntity) {
        scope.launch {
            try {
                val emailKey = user.email.replace(".", "_").replace("@", "_at_")
                val data = mapOf(
                    "id" to user.id,
                    "name" to user.name,
                    "email" to user.email.lowercase().trim(),
                    "passwordHash" to user.passwordHash,
                    "role" to user.role.name,
                    "createdAt" to user.createdAt
                )
                usersRef.child(emailKey).setValue(data)
                Log.d(TAG, "User account uploaded to cloud: ${user.email} (${user.role})")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to upload user to cloud: ${e.message}")
            }
        }
    }

    suspend fun fetchUserFromCloud(email: String, userDao: com.docdirect.app.data.local.dao.UserDao): UserEntity? {
        val cleanEmail = email.lowercase().trim()
        val emailKey = cleanEmail.replace(".", "_").replace("@", "_at_")
        return suspendCancellableCoroutine { continuation ->
            usersRef.child(emailKey).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val id = snapshot.child("id").getValue(String::class.java) ?: ""
                            val name = snapshot.child("name").getValue(String::class.java) ?: ""
                            val savedEmail = snapshot.child("email").getValue(String::class.java) ?: cleanEmail
                            val pwdHash = snapshot.child("passwordHash").getValue(String::class.java) ?: ""
                            val roleStr = snapshot.child("role").getValue(String::class.java) ?: UserRole.PATIENT.name
                            val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.PATIENT }
                            val user = UserEntity(
                                id = id,
                                name = name,
                                email = savedEmail.lowercase().trim(),
                                passwordHash = pwdHash,
                                role = role
                            )
                            scope.launch { userDao.insertUser(user) }
                            continuation.resumeWith(Result.success(user))
                            return
                        } catch (e: Exception) {
                            Log.w(TAG, "Error parsing cloud user: ${e.message}")
                        }
                    }
                    continuation.resumeWith(Result.success(null))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWith(Result.success(null))
                }
            })
        }
    }

    fun syncChatMessageToCloud(message: ChatMessageEntity) {
        scope.launch {
            try {
                val data = mapOf(
                    "id" to message.id,
                    "appointmentId" to message.appointmentId,
                    "senderId" to message.senderId,
                    "senderName" to message.senderName,
                    "senderRole" to message.senderRole.name,
                    "messageText" to message.messageText,
                    "isPrescription" to message.isPrescription,
                    "timestamp" to message.timestamp
                )
                chatsRef.child(message.appointmentId).child("messages").child(message.id).setValue(data)
                Log.d(TAG, "Chat message synced to cloud: ${message.id} for apt ${message.appointmentId}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync chat message: ${e.message}")
            }
        }
    }

    fun startListeningToChat(appointmentId: String, chatDao: ChatDao) {
        if (activeChatListeners.containsKey(appointmentId)) return
        val messagesNode = chatsRef.child(appointmentId).child("messages")
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                handleChatMessageSnapshot(snapshot, chatDao)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                handleChatMessageSnapshot(snapshot, chatDao)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Chat sync cancelled: ${error.message}")
            }
        }
        activeChatListeners[appointmentId] = listener
        messagesNode.addChildEventListener(listener)
        Log.i(TAG, "Started listening for realtime chat in appointment: $appointmentId")
    }

    fun stopListeningToChat(appointmentId: String) {
        val listener = activeChatListeners.remove(appointmentId)
        if (listener != null) {
            chatsRef.child(appointmentId).child("messages").removeEventListener(listener)
            Log.i(TAG, "Stopped listening for realtime chat in appointment: $appointmentId")
        }
    }

    private fun handleChatMessageSnapshot(snapshot: DataSnapshot, chatDao: ChatDao) {
        scope.launch {
            try {
                val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return@launch
                val appointmentId = snapshot.child("appointmentId").getValue(String::class.java) ?: return@launch
                val senderId = snapshot.child("senderId").getValue(String::class.java) ?: ""
                val senderName = snapshot.child("senderName").getValue(String::class.java) ?: "User"
                val roleStr = snapshot.child("senderRole").getValue(String::class.java) ?: UserRole.PATIENT.name
                val senderRole = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.PATIENT }
                val messageText = snapshot.child("messageText").getValue(String::class.java) ?: ""
                val isPrescription = snapshot.child("isPrescription").getValue(Boolean::class.java) ?: false
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()

                val entity = ChatMessageEntity(
                    id = id,
                    appointmentId = appointmentId,
                    senderId = senderId,
                    senderName = senderName,
                    senderRole = senderRole,
                    messageText = messageText,
                    isPrescription = isPrescription,
                    timestamp = timestamp
                )
                chatDao.insertMessage(entity)
                Log.d(TAG, "Realtime chat message received from cloud: $messageText from $senderName")
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing chat snapshot: ${e.message}")
            }
        }
    }
}
