package com.docdirect.app.data.repository

import android.content.Context
import com.docdirect.app.data.auth.SessionManager
import com.docdirect.app.data.local.AppDatabase
import com.docdirect.app.data.local.entity.*
import com.docdirect.app.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AppRepository private constructor(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val doctorDao = db.doctorDao()
    private val appointmentDao = db.appointmentDao()
    private val chatDao = db.chatDao()
    private val sessionManager = SessionManager.getInstance(context)

    val currentRole: StateFlow<UserRole> = MutableStateFlow(sessionManager.getUserRole()).asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedAvenSampleData()
        }
    }

    // Persistent Doctors stream
    val doctors: Flow<List<DoctorProfile>> = doctorDao.getAllDoctors().map { entities ->
        entities.map { entity ->
            DoctorProfile(
                id = entity.id,
                name = entity.name,
                medicalLicense = entity.medicalLicense,
                specialty = entity.specialty,
                qualification = entity.qualification,
                experienceYears = entity.experienceYears,
                consultationFee = entity.consultationFee,
                bio = entity.bio,
                rating = entity.rating,
                reviewCount = entity.reviewCount,
                isAvailable = entity.isAvailable,
                hospitalAffiliation = entity.hospitalAffiliation
            )
        }
    }

    // Auth & Session Actions
    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
    fun getCurrentUserId(): String? = sessionManager.getUserId()
    fun getCurrentUserName(): String = sessionManager.getUserName()
    fun getCurrentUserRole(): UserRole = sessionManager.getUserRole()

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<UserEntity> {
        val existing = userDao.getUserByEmail(email.lowercase().trim())
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }

        val userId = "user_${System.currentTimeMillis()}"
        val pwdHash = hashPassword(password)
        val newUser = UserEntity(
            id = userId,
            name = name.trim(),
            email = email.lowercase().trim(),
            passwordHash = pwdHash,
            role = role
        )

        userDao.insertUser(newUser)
        sessionManager.saveUserSession(userId, newUser.name, newUser.email, role)
        return Result.success(newUser)
    }

    suspend fun registerDoctorProfile(
        userId: String,
        name: String,
        license: String,
        specialty: String,
        qualification: String,
        experienceYears: Int,
        fee: Double,
        bio: String,
        hospital: String
    ) {
        val doctorId = "doc_$userId"
        val doctorEntity = DoctorEntity(
            id = doctorId,
            userId = userId,
            name = name,
            medicalLicense = license,
            specialty = specialty,
            qualification = qualification,
            experienceYears = experienceYears,
            consultationFee = fee,
            bio = bio,
            hospitalAffiliation = hospital,
            isAvailable = true
        )
        doctorDao.insertDoctor(doctorEntity)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email.lowercase().trim())
            ?: return Result.failure(Exception("Account not found. Please check your email."))

        if (user.passwordHash != hashPassword(password)) {
            return Result.failure(Exception("Incorrect password. Please try again."))
        }

        sessionManager.saveUserSession(user.id, user.name, user.email, user.role)
        return Result.success(user)
    }

    fun logout() {
        sessionManager.logout()
    }

    // Doctor details & slots
    fun getCurrentDoctorProfile(userId: String): Flow<DoctorProfile?> {
        return doctorDao.getDoctorByUserIdFlow(userId).map { entity ->
            entity?.let {
                DoctorProfile(
                    id = it.id,
                    name = it.name,
                    medicalLicense = it.medicalLicense,
                    specialty = it.specialty,
                    qualification = it.qualification,
                    experienceYears = it.experienceYears,
                    consultationFee = it.consultationFee,
                    bio = it.bio,
                    rating = it.rating,
                    reviewCount = it.reviewCount,
                    isAvailable = it.isAvailable,
                    hospitalAffiliation = it.hospitalAffiliation
                )
            }
        }
    }

    fun getSlotsForDoctor(doctorId: String): Flow<List<TimeSlot>> {
        return doctorDao.getSlotsForDoctor(doctorId).map { entities ->
            entities.map { TimeSlot(it.id, it.date, it.time, it.isBooked) }
        }
    }

    suspend fun addTimeSlot(doctorId: String, date: String, time: String) {
        val slotId = "slot_${System.currentTimeMillis()}"
        doctorDao.insertSlot(SlotEntity(slotId, doctorId, date, time, false))
    }

    suspend fun deleteTimeSlot(slotId: String) {
        doctorDao.deleteSlot(slotId)
    }

    suspend fun toggleDoctorAvailability(doctorId: String, isAvailable: Boolean) {
        val doctor = doctorDao.getDoctorByUserId(doctorId)
        doctor?.let {
            doctorDao.updateDoctor(it.copy(isAvailable = isAvailable))
        }
    }

    suspend fun updateDoctorFee(doctorId: String, newFee: Double) {
        val doctor = doctorDao.getDoctorByUserId(doctorId)
        doctor?.let {
            doctorDao.updateDoctor(it.copy(consultationFee = newFee))
        }
    }

    suspend fun updateDoctorProfile(
        doctorId: String,
        specialty: String,
        qualification: String,
        bio: String,
        hospital: String
    ) {
        val doctor = doctorDao.getDoctorByUserId(doctorId)
        doctor?.let {
            doctorDao.updateDoctor(
                it.copy(
                    specialty = specialty,
                    qualification = qualification,
                    bio = bio,
                    hospitalAffiliation = hospital
                )
            )
        }
    }

    // Appointments Flow
    fun getAppointmentsForDoctor(doctorId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForDoctor(doctorId).map { entities ->
            entities.map { toAppointment(it) }
        }
    }

    fun getAppointmentsForPatient(patientId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForPatient(patientId).map { entities ->
            entities.map { toAppointment(it) }
        }
    }

    fun getAppointmentById(appointmentId: String): Flow<Appointment?> {
        return appointmentDao.getAppointmentById(appointmentId).map { entity ->
            entity?.let { toAppointment(it) }
        }
    }

    suspend fun bookAppointment(
        doctorId: String,
        doctorName: String,
        doctorSpecialty: String,
        patientId: String,
        patientName: String,
        slotId: String,
        date: String,
        time: String,
        symptoms: String,
        fee: Double
    ): Appointment {
        val aptId = "apt_${System.currentTimeMillis()}"
        val txnId = "TXN-${System.currentTimeMillis().toString().takeLast(7)}"
        val entity = AppointmentEntity(
            id = aptId,
            patientId = patientId,
            patientName = patientName,
            doctorId = doctorId,
            doctorName = doctorName,
            doctorSpecialty = doctorSpecialty,
            appointmentDate = date,
            appointmentTime = time,
            symptoms = symptoms,
            feePaid = fee,
            status = AppointmentStatus.UPCOMING,
            transactionId = txnId
        )
        appointmentDao.insertAppointment(entity)
        doctorDao.markSlotBooked(slotId)
        return toAppointment(entity)
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus) {
        appointmentDao.updateAppointmentStatus(appointmentId, status)
    }

    suspend fun addPrescription(appointmentId: String, prescriptionText: String) {
        appointmentDao.addPrescription(appointmentId, prescriptionText)
    }

    suspend fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        appointmentDao.rescheduleAppointment(appointmentId, newDate, newTime)
    }

    suspend fun cancelAppointment(appointmentId: String) {
        appointmentDao.cancelAppointment(appointmentId)
    }

    private suspend fun seedAvenSampleData() {
        if (doctorDao.getDoctorCount() == 0) {
            val miraShah = DoctorEntity(
                id = "doc_mira_shah",
                userId = "user_mira_shah",
                name = "Dr Mira Shah",
                medicalLicense = "MCI-48921-DERM",
                specialty = "Dermatology",
                qualification = "MBBS, MD (Dermatology)",
                experienceYears = 8,
                consultationFee = 1200.0,
                bio = "Specializes in clinical and aesthetic dermatology, acne protocols, skin barrier restoration, and longevity care at Bandra Skin Clinic.",
                rating = 4.9,
                reviewCount = 128,
                isAvailable = true,
                hospitalAffiliation = "Bandra Skin Clinic, Bandra West, Mumbai"
            )
            doctorDao.insertDoctor(miraShah)

            doctorDao.insertSlot(SlotEntity("slot_mira_1", "doc_mira_shah", "25 Sep 2026", "3:30 PM", true))
            doctorDao.insertSlot(SlotEntity("slot_mira_2", "doc_mira_shah", "25 Sep 2026", "4:30 PM", false))
            doctorDao.insertSlot(SlotEntity("slot_mira_3", "doc_mira_shah", "26 Sep 2026", "10:00 AM", false))
            doctorDao.insertSlot(SlotEntity("slot_mira_4", "doc_mira_shah", "26 Sep 2026", "11:30 AM", false))
            doctorDao.insertSlot(SlotEntity("slot_mira_5", "doc_mira_shah", "26 Sep 2026", "3:30 PM", false))

            val kabirRao = DoctorEntity(
                id = "doc_kabir_rao",
                userId = "user_kabir_rao",
                name = "Dr Kabir Rao",
                medicalLicense = "MCI-31045-MED",
                specialty = "General Medicine",
                qualification = "MBBS, MD (Internal Medicine)",
                experienceYears = 12,
                consultationFee = 950.0,
                bio = "Preventive healthcare, hypertension, metabolic wellness, and chronic disease management.",
                rating = 4.8,
                reviewCount = 94,
                isAvailable = true,
                hospitalAffiliation = "Apollo Medical Centre, Mumbai"
            )
            doctorDao.insertDoctor(kabirRao)
            doctorDao.insertSlot(SlotEntity("slot_kabir_1", "doc_kabir_rao", "25 Sep 2026", "2:00 PM", false))
            doctorDao.insertSlot(SlotEntity("slot_kabir_2", "doc_kabir_rao", "26 Sep 2026", "11:00 AM", false))

            val leenaNair = DoctorEntity(
                id = "doc_leena_nair",
                userId = "user_leena_nair",
                name = "Dr Leena Nair",
                medicalLicense = "MCI-55219-PED",
                specialty = "Pediatrics",
                qualification = "MBBS, DCH, DNB (Pediatrics)",
                experienceYears = 10,
                consultationFee = 1100.0,
                bio = "Pediatric developmental milestones, immunization, and childhood allergy management.",
                rating = 4.9,
                reviewCount = 112,
                isAvailable = true,
                hospitalAffiliation = "Lilavati Children's Wing, Mumbai"
            )
            doctorDao.insertDoctor(leenaNair)
            doctorDao.insertSlot(SlotEntity("slot_leena_1", "doc_leena_nair", "26 Sep 2026", "4:00 PM", false))

            val rohanKapoor = DoctorEntity(
                id = "doc_rohan_kapoor",
                userId = "user_rohan_kapoor",
                name = "Dr Rohan Kapoor",
                medicalLicense = "MCI-22874-CARD",
                specialty = "Cardiology",
                qualification = "MBBS, MD, DM (Cardiology)",
                experienceYears = 15,
                consultationFee = 1800.0,
                bio = "Interventional cardiology, preventive cardiac screenings, lipid disorders, and ECG telemetry.",
                rating = 5.0,
                reviewCount = 156,
                isAvailable = true,
                hospitalAffiliation = "Fortis Heart Institute, Mumbai"
            )
            doctorDao.insertDoctor(rohanKapoor)
            doctorDao.insertSlot(SlotEntity("slot_rohan_1", "doc_rohan_kapoor", "26 Sep 2026", "2:30 PM", false))
        }

        if (appointmentDao.getAppointmentCount() == 0) {
            val patientId = getCurrentUserId() ?: "user_aarav_mehta"
            val patientName = getCurrentUserName().ifBlank { "Aarav Mehta" }
            val initialApt = AppointmentEntity(
                id = "apt_aven_0925",
                patientId = patientId,
                patientName = patientName,
                doctorId = "doc_mira_shah",
                doctorName = "Dr Mira Shah",
                doctorSpecialty = "Dermatologist",
                appointmentDate = "25 Sep 2026",
                appointmentTime = "3:30 PM IST",
                symptoms = "Follow-up for a skin concern. Discussing rash progression and barrier restoration.",
                feePaid = 1249.0,
                status = AppointmentStatus.UPCOMING,
                transactionId = "AV-INV-0925-1042",
                prescription = "",
                createdAt = System.currentTimeMillis()
            )
            appointmentDao.insertAppointment(initialApt)
        }
    }

    // Chat Flow
    fun getMessagesForAppointment(appointmentId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForAppointment(appointmentId).map { entities ->
            entities.map {
                ChatMessage(
                    id = it.id,
                    appointmentId = it.appointmentId,
                    senderId = it.senderId,
                    senderName = it.senderName,
                    senderRole = it.senderRole,
                    messageText = it.messageText,
                    isPrescription = it.isPrescription,
                    timestamp = it.timestamp
                )
            }
        }
    }

    suspend fun sendMessage(
        appointmentId: String,
        senderId: String,
        senderName: String,
        senderRole: UserRole,
        messageText: String,
        isPrescription: Boolean = false
    ) {
        val msgEntity = ChatMessageEntity(
            id = "msg_${System.currentTimeMillis()}",
            appointmentId = appointmentId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            messageText = messageText,
            isPrescription = isPrescription
        )
        chatDao.insertMessage(msgEntity)
    }

    private fun toAppointment(entity: AppointmentEntity): Appointment {
        return Appointment(
            id = entity.id,
            patientId = entity.patientId,
            patientName = entity.patientName,
            doctorId = entity.doctorId,
            doctorName = entity.doctorName,
            doctorSpecialty = entity.doctorSpecialty,
            appointmentDate = entity.appointmentDate,
            appointmentTime = entity.appointmentTime,
            symptoms = entity.symptoms,
            feePaid = entity.feePaid,
            status = entity.status,
            transactionId = entity.transactionId,
            prescription = entity.prescription,
            createdAt = entity.createdAt
        )
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return instance ?: synchronized(this) {
                instance ?: AppRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
