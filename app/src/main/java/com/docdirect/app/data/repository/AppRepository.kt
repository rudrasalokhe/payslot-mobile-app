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
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.docdirect.app.workers.AppointmentReminderWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AppRepository private constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val doctorDao = db.doctorDao()
    private val appointmentDao = db.appointmentDao()
    private val chatDao = db.chatDao()
    private val recordDao = db.recordDao()
    private val familyDao = db.familyDao()
    private val invoiceDao = db.invoiceDao()
    private val notificationDao = db.notificationDao()
    private val reviewDao = db.reviewDao()
    private val vitalDao = db.vitalDao()
    private val sessionManager = SessionManager.getInstance(context)

    private val _currentRole = MutableStateFlow(sessionManager.getUserRole())
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

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
    fun getCurrentUserEmail(): String = sessionManager.getUserEmail()
    fun getCurrentUserPhone(): String = sessionManager.getUserPhone()
    fun getCurrentUserDob(): String = sessionManager.getUserDob()
    fun getCurrentUserPreferredName(): String = sessionManager.getUserPreferredName()
    fun getCurrentUserRole(): UserRole = sessionManager.getUserRole()
    fun saveUserDetails(phone: String, dob: String, preferredName: String) = sessionManager.saveUserDetails(phone, dob, preferredName)

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<UserEntity> {
        val cleanEmail = email.lowercase().trim()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }

        val userId = "user_${System.currentTimeMillis()}"
        val pwdHash = hashPassword(password)
        val newUser = UserEntity(
            id = userId,
            name = name.trim(),
            email = cleanEmail,
            passwordHash = pwdHash,
            role = role
        )

        userDao.insertUser(newUser)
        com.docdirect.app.data.firebase.FirebaseSyncManager.syncUserToCloud(newUser)
        sessionManager.saveUserSession(userId, newUser.name, newUser.email, role)
        _currentRole.value = role
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
        com.docdirect.app.data.firebase.FirebaseSyncManager.syncDoctorToCloud(doctorEntity)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val cleanEmail = email.lowercase().trim()
        var user = userDao.getUserByEmail(cleanEmail)

        // If not in local Room DB, query Firebase Cloud database
        if (user == null) {
            user = com.docdirect.app.data.firebase.FirebaseSyncManager.fetchUserFromCloud(cleanEmail, userDao)
        }

        if (user == null) {
            return Result.failure(Exception("Account not found. Please check your email or sign up."))
        }

        if (user.passwordHash != hashPassword(password)) {
            return Result.failure(Exception("Incorrect password. Please try again."))
        }

        sessionManager.saveUserSession(user.id, user.name, user.email, user.role)
        _currentRole.value = user.role

        // If doctor, verify clinician profile exists in local DB
        if (user.role == UserRole.DOCTOR) {
            val existingDoc = doctorDao.getDoctorByIdOrUserId("doc_${user.id}") ?: doctorDao.getDoctorByIdOrUserId(user.id)
            if (existingDoc == null) {
                val newDoc = DoctorEntity(
                    id = "doc_${user.id}",
                    userId = user.id,
                    name = user.name,
                    medicalLicense = "MCI-VERIFIED",
                    specialty = "General Medicine",
                    qualification = "MBBS, MD",
                    experienceYears = 5,
                    consultationFee = 999.0,
                    bio = "Clinician practicing on DocDirect.",
                    hospitalAffiliation = "DocDirect Telehealth",
                    isAvailable = true
                )
                doctorDao.insertDoctor(newDoc)
            }
        }

        return Result.success(user)
    }

    fun logout() {
        sessionManager.logout()
        _currentRole.value = UserRole.PATIENT
    }

    fun switchToDoctorSession(doctorId: String = "doc_mira_shah") {
        sessionManager.saveUserSession(
            userId = doctorId,
            name = "Dr Mira Shah",
            email = "doctor@docdirect.app",
            role = UserRole.DOCTOR
        )
        _currentRole.value = UserRole.DOCTOR
    }

    fun switchToPatientSession(patientId: String = "user_aarav_mehta") {
        sessionManager.saveUserSession(
            userId = patientId,
            name = "Aarav Mehta",
            email = "patient@docdirect.app",
            role = UserRole.PATIENT
        )
        _currentRole.value = UserRole.PATIENT
    }

    // Doctor details & slots
    fun getCurrentDoctorProfile(userId: String): Flow<DoctorProfile?> {
        return doctorDao.getDoctorByIdOrUserIdFlow(userId).map { entity ->
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
        val doctor = doctorDao.getDoctorByIdOrUserId(doctorId)
        doctor?.let {
            val updated = it.copy(isAvailable = isAvailable)
            doctorDao.updateDoctor(updated)
            com.docdirect.app.data.firebase.FirebaseSyncManager.syncDoctorToCloud(updated)
        }
    }

    suspend fun updateDoctorFee(doctorId: String, newFee: Double) {
        val doctor = doctorDao.getDoctorByIdOrUserId(doctorId)
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
        val doctor = doctorDao.getDoctorByIdOrUserId(doctorId)
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

    suspend fun updateDoctorFullProfile(
        doctorId: String,
        name: String,
        medicalLicense: String,
        specialty: String,
        qualification: String,
        experienceYears: Int,
        fee: Double,
        hospital: String,
        bio: String
    ) {
        val existing = doctorDao.getDoctorByIdOrUserId(doctorId)
        if (existing != null) {
            val updated = existing.copy(
                name = name.trim(),
                medicalLicense = medicalLicense.trim(),
                specialty = specialty.trim(),
                qualification = qualification.trim(),
                experienceYears = experienceYears,
                consultationFee = fee,
                hospitalAffiliation = hospital.trim(),
                bio = bio.trim()
            )
            doctorDao.insertDoctor(updated)
            com.docdirect.app.data.firebase.FirebaseSyncManager.syncDoctorToCloud(updated)
            if (sessionManager.getUserId() == existing.userId || sessionManager.getUserId() == existing.id) {
                sessionManager.saveUserSession(
                    userId = sessionManager.getUserId() ?: existing.id,
                    name = name.trim(),
                    email = sessionManager.getUserEmail(),
                    role = UserRole.DOCTOR
                )
            }
        } else {
            val newDoctor = DoctorEntity(
                id = doctorId,
                userId = doctorId,
                name = name.trim(),
                medicalLicense = medicalLicense.trim(),
                specialty = specialty.trim(),
                qualification = qualification.trim(),
                experienceYears = experienceYears,
                consultationFee = fee,
                bio = bio.trim(),
                hospitalAffiliation = hospital.trim(),
                isAvailable = true
            )
            doctorDao.insertDoctor(newDoctor)
            com.docdirect.app.data.firebase.FirebaseSyncManager.syncDoctorToCloud(newDoctor)
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
        fee: Double,
        transactionId: String? = null
    ): Appointment {
        val aptId = "apt_${System.currentTimeMillis()}"
        val txnId = transactionId ?: "TXN-${System.currentTimeMillis().toString().takeLast(7)}"
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
        com.docdirect.app.data.firebase.FirebaseSyncManager.syncAppointmentToCloud(entity)
        doctorDao.markSlotBooked(slotId)

        // Schedule automated local Android notifications (15 min and 1 hr before)
        scheduleAppointmentReminders(
            appointmentId = aptId,
            doctorName = doctorName,
            date = date,
            time = time
        )
        return toAppointment(entity)
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus) {
        appointmentDao.updateAppointmentStatus(appointmentId, status)
        if (status == AppointmentStatus.CANCELLED || status == AppointmentStatus.COMPLETED) {
            cancelAppointmentReminders(appointmentId)
        }
    }

    suspend fun addPrescription(appointmentId: String, prescriptionText: String) {
        appointmentDao.addPrescription(appointmentId, prescriptionText)
        com.docdirect.app.data.firebase.FirebaseSyncManager.syncPrescriptionToCloud(appointmentId, prescriptionText)
        try {
            val apt = appointmentDao.getAppointmentById(appointmentId).firstOrNull()
            val patientId = apt?.patientId ?: getCurrentUserId() ?: "user_aarav_mehta"
            val doctorName = apt?.doctorName ?: "Dr. Mira Shah"
            com.docdirect.app.services.DocDirectNotificationHub.postPrescriptionAlert(
                context = appContext,
                appointmentId = appointmentId,
                doctorName = doctorName,
                prescriptionSummary = prescriptionText,
                patientUserId = patientId
            )
        } catch (e: Exception) {
            android.util.Log.w("AppRepository", "Error triggering prescription notification: ${e.message}")
        }
    }

    suspend fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        appointmentDao.rescheduleAppointment(appointmentId, newDate, newTime)
    }

    /**
     * Schedules WorkManager call reminders 15 minutes and 5 minutes before scheduled visits
     * with an interactive "Tap to Join" action button for both patients & clinicians.
     */
    fun scheduleAppointmentReminders(
        appointmentId: String,
        doctorName: String,
        date: String,
        time: String,
        patientName: String = "Patient"
    ) {
        try {
            val targetEpoch = parseAppointmentEpoch(date, time)
            val now = System.currentTimeMillis()

            // 15-Minute Call Reminder (Patient & Doctor)
            val delay15MinMs = (targetEpoch - 15 * 60 * 1000L) - now
            if (delay15MinMs > 0) {
                val reqPatient15 = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(delay15MinMs, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            AppointmentReminderWorker.KEY_APPOINTMENT_ID to appointmentId,
                            AppointmentReminderWorker.KEY_DOCTOR_NAME to doctorName,
                            AppointmentReminderWorker.KEY_PATIENT_NAME to patientName,
                            AppointmentReminderWorker.KEY_APPOINTMENT_DATE to date,
                            AppointmentReminderWorker.KEY_APPOINTMENT_TIME to time,
                            AppointmentReminderWorker.KEY_REMINDER_MINUTES to 15,
                            AppointmentReminderWorker.KEY_IS_DOCTOR to false
                        )
                    )
                    .addTag("reminder_${appointmentId}_15m")
                    .build()

                WorkManager.getInstance(appContext).enqueueUniqueWork(
                    "reminder_${appointmentId}_15m",
                    ExistingWorkPolicy.REPLACE,
                    reqPatient15
                )

                val reqDoctor15 = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(delay15MinMs, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            AppointmentReminderWorker.KEY_APPOINTMENT_ID to appointmentId,
                            AppointmentReminderWorker.KEY_DOCTOR_NAME to doctorName,
                            AppointmentReminderWorker.KEY_PATIENT_NAME to patientName,
                            AppointmentReminderWorker.KEY_APPOINTMENT_DATE to date,
                            AppointmentReminderWorker.KEY_APPOINTMENT_TIME to time,
                            AppointmentReminderWorker.KEY_REMINDER_MINUTES to 15,
                            AppointmentReminderWorker.KEY_IS_DOCTOR to true
                        )
                    )
                    .addTag("reminder_doc_${appointmentId}_15m")
                    .build()

                WorkManager.getInstance(appContext).enqueueUniqueWork(
                    "reminder_doc_${appointmentId}_15m",
                    ExistingWorkPolicy.REPLACE,
                    reqDoctor15
                )
            } else if (targetEpoch > now) {
                val immediateDelay = (targetEpoch - now).coerceAtLeast(2000L)
                val reqImmediate = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(immediateDelay, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            AppointmentReminderWorker.KEY_APPOINTMENT_ID to appointmentId,
                            AppointmentReminderWorker.KEY_DOCTOR_NAME to doctorName,
                            AppointmentReminderWorker.KEY_PATIENT_NAME to patientName,
                            AppointmentReminderWorker.KEY_APPOINTMENT_DATE to date,
                            AppointmentReminderWorker.KEY_APPOINTMENT_TIME to time,
                            AppointmentReminderWorker.KEY_REMINDER_MINUTES to 15,
                            AppointmentReminderWorker.KEY_IS_DOCTOR to false
                        )
                    )
                    .addTag("reminder_${appointmentId}_15m")
                    .build()

                WorkManager.getInstance(appContext).enqueueUniqueWork(
                    "reminder_${appointmentId}_15m",
                    ExistingWorkPolicy.REPLACE,
                    reqImmediate
                )
            }

            // 5-Minute Call Reminder (Patient & Doctor with immediate "Tap to Join")
            val delay5MinMs = (targetEpoch - 5 * 60 * 1000L) - now
            if (delay5MinMs > 0) {
                val reqPatient5 = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(delay5MinMs, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            AppointmentReminderWorker.KEY_APPOINTMENT_ID to appointmentId,
                            AppointmentReminderWorker.KEY_DOCTOR_NAME to doctorName,
                            AppointmentReminderWorker.KEY_PATIENT_NAME to patientName,
                            AppointmentReminderWorker.KEY_APPOINTMENT_DATE to date,
                            AppointmentReminderWorker.KEY_APPOINTMENT_TIME to time,
                            AppointmentReminderWorker.KEY_REMINDER_MINUTES to 5,
                            AppointmentReminderWorker.KEY_IS_DOCTOR to false
                        )
                    )
                    .addTag("reminder_${appointmentId}_5m")
                    .build()

                WorkManager.getInstance(appContext).enqueueUniqueWork(
                    "reminder_${appointmentId}_5m",
                    ExistingWorkPolicy.REPLACE,
                    reqPatient5
                )

                val reqDoctor5 = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(delay5MinMs, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            AppointmentReminderWorker.KEY_APPOINTMENT_ID to appointmentId,
                            AppointmentReminderWorker.KEY_DOCTOR_NAME to doctorName,
                            AppointmentReminderWorker.KEY_PATIENT_NAME to patientName,
                            AppointmentReminderWorker.KEY_APPOINTMENT_DATE to date,
                            AppointmentReminderWorker.KEY_APPOINTMENT_TIME to time,
                            AppointmentReminderWorker.KEY_REMINDER_MINUTES to 5,
                            AppointmentReminderWorker.KEY_IS_DOCTOR to true
                        )
                    )
                    .addTag("reminder_doc_${appointmentId}_5m")
                    .build()

                WorkManager.getInstance(appContext).enqueueUniqueWork(
                    "reminder_doc_${appointmentId}_5m",
                    ExistingWorkPolicy.REPLACE,
                    reqDoctor5
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("AppRepository", "Error scheduling appointment reminders", e)
        }
    }

    /**
     * Cancels any scheduled reminders for the given appointment.
     */
    fun cancelAppointmentReminders(appointmentId: String) {
        try {
            WorkManager.getInstance(appContext).cancelUniqueWork("reminder_${appointmentId}_15m")
            WorkManager.getInstance(appContext).cancelUniqueWork("reminder_${appointmentId}_5m")
            WorkManager.getInstance(appContext).cancelUniqueWork("reminder_doc_${appointmentId}_15m")
            WorkManager.getInstance(appContext).cancelUniqueWork("reminder_doc_${appointmentId}_5m")
        } catch (e: Exception) {
            android.util.Log.e("AppRepository", "Error cancelling appointment reminders", e)
        }
    }

    private fun parseAppointmentEpoch(dateStr: String, timeStr: String): Long {
        val calendar = Calendar.getInstance()
        val trimmedDate = dateStr.trim()
        val trimmedTime = timeStr.trim()

        var dateSet = false
        if (trimmedDate.equals("Today", ignoreCase = true)) {
            dateSet = true
        } else if (trimmedDate.equals("Tomorrow", ignoreCase = true)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            dateSet = true
        } else {
            val dateFormats = listOf(
                "dd MMM yyyy",
                "d MMM yyyy",
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "MMM dd, yyyy"
            )
            for (format in dateFormats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                    sdf.isLenient = true
                    val parsed = sdf.parse(trimmedDate)
                    if (parsed != null) {
                        val tempCal = Calendar.getInstance().apply { time = parsed }
                        calendar.set(Calendar.YEAR, tempCal.get(Calendar.YEAR))
                        calendar.set(Calendar.MONTH, tempCal.get(Calendar.MONTH))
                        calendar.set(Calendar.DAY_OF_MONTH, tempCal.get(Calendar.DAY_OF_MONTH))
                        dateSet = true
                        break
                    }
                } catch (_: Exception) {}
            }
        }

        val timeFormats = listOf(
            "hh:mm a",
            "h:mm a",
            "HH:mm",
            "H:mm"
        )
        for (format in timeFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = true
                val parsed = sdf.parse(trimmedTime)
                if (parsed != null) {
                    val tempCal = Calendar.getInstance().apply { time = parsed }
                    calendar.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY))
                    calendar.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE))
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    return calendar.timeInMillis
                }
            } catch (_: Exception) {}
        }

        // Fallback default: if date/time could not be parsed, default to 2 hours in the future
        return System.currentTimeMillis() + 2 * 60 * 60 * 1000L
    }

    suspend fun cancelAppointment(appointmentId: String) {
        appointmentDao.cancelAppointment(appointmentId)
        cancelAppointmentReminders(appointmentId)
    }

    private suspend fun seedAvenSampleData() {
        val defaultPwd = hashPassword("password123")

        // Pre-registered Doctor account
        if (userDao.getUserByEmail("doctor@docdirect.app") == null) {
            userDao.insertUser(
                UserEntity(
                    id = "doc_mira_shah",
                    name = "Dr Mira Shah",
                    email = "doctor@docdirect.app",
                    passwordHash = defaultPwd,
                    role = UserRole.DOCTOR
                )
            )
        }
        if (userDao.getUserByEmail("mira.shah@docdirect.app") == null) {
            userDao.insertUser(
                UserEntity(
                    id = "doc_mira_shah",
                    name = "Dr Mira Shah",
                    email = "mira.shah@docdirect.app",
                    passwordHash = defaultPwd,
                    role = UserRole.DOCTOR
                )
            )
        }

        // Pre-registered Patient account
        if (userDao.getUserByEmail("patient@docdirect.app") == null) {
            userDao.insertUser(
                UserEntity(
                    id = "user_aarav_mehta",
                    name = "Aarav Mehta",
                    email = "patient@docdirect.app",
                    passwordHash = defaultPwd,
                    role = UserRole.PATIENT
                )
            )
        }
        if (userDao.getUserByEmail("aarav.mehta@docdirect.app") == null) {
            userDao.insertUser(
                UserEntity(
                    id = "user_aarav_mehta",
                    name = "Aarav Mehta",
                    email = "aarav.mehta@docdirect.app",
                    passwordHash = defaultPwd,
                    role = UserRole.PATIENT
                )
            )
        }

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

        if (recordDao.getRecordCount() == 0) {
            val patientId = getCurrentUserId() ?: "user_aarav_mehta"
            recordDao.insertRecord(RecordEntity("rec_1", patientId, "Blood-test-report.pdf", "Lab report", "2.4 MB", "18 Sep 2026", true, "Dr Mira Shah", "27 Sep 2026"))
            recordDao.insertRecord(RecordEntity("rec_2", patientId, "Dermatology visit summary", "Consultation note", "1.1 MB", "25 Sep 2026", true, "Dr Mira Shah", "27 Sep 2026"))
            recordDao.insertRecord(RecordEntity("rec_3", patientId, "Lipid Profile & HbA1c.pdf", "Diagnostic panel", "3.2 MB", "12 Aug 2026", false, "", ""))
            recordDao.insertRecord(RecordEntity("rec_4", patientId, "Vaccination Certificate.pdf", "Immunization", "850 KB", "05 Jan 2026", false, "", ""))
        }

        if (familyDao.getFamilyCount() == 0) {
            val patientId = getCurrentUserId() ?: "user_aarav_mehta"
            familyDao.insertFamilyMember(FamilyMemberEntity("fam_1", patientId, "Nisha Mehta", "Parent", 56, "Female", "12 February 1970"))
        }

        if (invoiceDao.getInvoiceCount() == 0) {
            invoiceDao.insertInvoice(
                InvoiceEntity(
                    id = "AV-INV-0925-1042",
                    appointmentId = "apt_aven_0925",
                    patientName = "Aarav Mehta",
                    doctorName = "Dr Mira Shah",
                    specialty = "Dermatologist",
                    consultationFee = 1200.0,
                    platformFee = 49.0,
                    totalAmount = 1249.0,
                    paymentMethod = "UPI (Google Pay)",
                    status = "PAID",
                    transactionId = "TXN-9821443",
                    date = "25 Sep 2026"
                )
            )
        }

        if (reviewDao.getReviewCount() == 0) {
            reviewDao.insertReview(ReviewEntity("rev_1", "doc_mira_shah", "Rhea P.", 5, "I felt listened to and had time to ask my questions. The treatment protocol worked wonders within three weeks.", "Verified video visit", "14 Sep 2026"))
            reviewDao.insertReview(ReviewEntity("rev_2", "doc_mira_shah", "Arjun S.", 5, "Very clear explanation of the prescription and follow-up care. Didn't have to wait in a crowded clinic.", "Verified video visit", "02 Sep 2026"))
            reviewDao.insertReview(ReviewEntity("rev_3", "doc_mira_shah", "Tanya M.", 5, "Dr Shah answered all my skin barrier questions patiently.", "Verified in-person visit", "28 Aug 2026"))
        }

        if (notificationDao.getNotificationCount() == 0) {
            val patientId = getCurrentUserId() ?: "user_aarav_mehta"
            notificationDao.insertNotification(NotificationEntity("notif_1", patientId, "Your appointment is tomorrow", "Video consultation with Dr Mira Shah on 25 Sep at 3:30 PM IST", "Today, 9:00 AM", false, "aven_visits"))
            notificationDao.insertNotification(NotificationEntity("notif_2", patientId, "Blood-test-report.pdf shared", "Access granted to Dr Mira Shah until 27 Sep 2026", "Yesterday, 4:30 PM", true, "aven_records"))
            notificationDao.insertNotification(NotificationEntity("notif_3", patientId, "Booking confirmed", "Receipt AV-INV-0925-1042 generated for ₹1,249", "24 Sep, 10:15 AM", true, "aven_profile"))
        }

        if (vitalDao.getVitalCount() == 0) {
            val patientId = getCurrentUserId() ?: "user_aarav_mehta"
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            vitalDao.insertVital(VitalEntity("vital_bp_1", patientId, "Blood Pressure", 124.0, 82.0, "mmHg", "Morning reading, after rest", "21 Sep 2026", now - 4 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bp_2", patientId, "Blood Pressure", 122.0, 80.0, "mmHg", "Normal baseline", "22 Sep 2026", now - 3 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bp_3", patientId, "Blood Pressure", 128.0, 84.0, "mmHg", "Post-work stress", "23 Sep 2026", now - 2 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bp_4", patientId, "Blood Pressure", 120.0, 78.0, "mmHg", "Optimal post morning walk", "24 Sep 2026", now - 1 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bp_5", patientId, "Blood Pressure", 118.0, 76.0, "mmHg", "Resting reading", "Today", now))

            vitalDao.insertVital(VitalEntity("vital_bs_1", patientId, "Blood Sugar", 98.0, 0.0, "mg/dL", "Fasting (8 hrs)", "21 Sep 2026", now - 4 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bs_2", patientId, "Blood Sugar", 102.0, 0.0, "mg/dL", "Fasting", "22 Sep 2026", now - 3 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bs_3", patientId, "Blood Sugar", 115.0, 0.0, "mg/dL", "Post-prandial (2 hrs post lunch)", "23 Sep 2026", now - 2 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bs_4", patientId, "Blood Sugar", 95.0, 0.0, "mg/dL", "Fasting baseline", "24 Sep 2026", now - 1 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_bs_5", patientId, "Blood Sugar", 92.0, 0.0, "mg/dL", "Fasting morning", "Today", now))

            vitalDao.insertVital(VitalEntity("vital_wt_1", patientId, "Weight", 71.8, 0.0, "kg", "Morning weigh-in", "21 Sep 2026", now - 4 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_wt_2", patientId, "Weight", 71.5, 0.0, "kg", "Pre-breakfast", "22 Sep 2026", now - 3 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_wt_3", patientId, "Weight", 71.3, 0.0, "kg", "Consistent hydration", "23 Sep 2026", now - 2 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_wt_4", patientId, "Weight", 71.0, 0.0, "kg", "Post workout", "24 Sep 2026", now - 1 * dayMillis))
            vitalDao.insertVital(VitalEntity("vital_wt_5", patientId, "Weight", 70.8, 0.0, "kg", "Weekly target reached", "Today", now))
        }
    }

    // Records Flow
    fun getRecordsForPatient(patientId: String): Flow<List<RecordEntity>> = recordDao.getRecordsForPatient(patientId)
    fun getRecordsForAppointment(appointmentId: String): Flow<List<RecordEntity>> = recordDao.getRecordsForAppointment(appointmentId)

    suspend fun addRecord(
        title: String,
        category: String,
        fileSize: String,
        patientId: String,
        appointmentId: String = "",
        fileUri: String = ""
    ) {
        val record = RecordEntity(
            id = "rec_${System.currentTimeMillis()}",
            patientId = patientId,
            title = title,
            category = category,
            fileSize = fileSize,
            date = "Today",
            isSharedWithDoctor = true,
            appointmentId = appointmentId,
            fileUri = fileUri
        )
        recordDao.insertRecord(record)
    }
    suspend fun updateRecordSharing(recordId: String, isShared: Boolean) = recordDao.updateSharingStatus(recordId, isShared)
    suspend fun deleteRecord(recordId: String) = recordDao.deleteRecord(recordId)

    // Vitals Flow
    fun getVitalsForPatient(patientId: String): Flow<List<VitalEntity>> = vitalDao.getVitalsForPatient(patientId)
    fun getAllVitals(): Flow<List<VitalEntity>> = vitalDao.getAllVitals()
    suspend fun addVital(
        patientId: String,
        type: String,
        valuePrimary: Double,
        valueSecondary: Double = 0.0,
        unit: String,
        notes: String = "",
        date: String = "Today"
    ) {
        val vital = VitalEntity(
            id = "vital_${System.currentTimeMillis()}",
            patientId = patientId,
            type = type,
            valuePrimary = valuePrimary,
            valueSecondary = valueSecondary,
            unit = unit,
            notes = notes,
            date = date,
            timestamp = System.currentTimeMillis()
        )
        vitalDao.insertVital(vital)
    }
    suspend fun deleteVital(vitalId: String) = vitalDao.deleteVital(vitalId)

    // Family Members Flow
    fun getFamilyMembers(patientId: String): Flow<List<FamilyMemberEntity>> = familyDao.getFamilyMembers(patientId)
    suspend fun addFamilyMember(patientId: String, name: String, relationship: String, age: Int, gender: String, dob: String) {
        val member = FamilyMemberEntity(
            id = "fam_${System.currentTimeMillis()}",
            patientId = patientId,
            name = name,
            relationship = relationship,
            age = age,
            gender = gender,
            dob = dob
        )
        familyDao.insertFamilyMember(member)
    }

    // Invoices Flow
    fun getAllInvoices(): Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()
    fun getInvoiceForAppointment(appointmentId: String): Flow<InvoiceEntity?> = invoiceDao.getInvoiceForAppointment(appointmentId)
    suspend fun createInvoice(
        appointmentId: String,
        patientName: String,
        doctorName: String,
        specialty: String,
        fee: Double,
        platformFee: Double,
        paymentMethod: String,
        transactionId: String = "",
        status: String = "PAID",
        date: String = "Today"
    ): InvoiceEntity {
        val invoice = InvoiceEntity(
            id = "AV-INV-${System.currentTimeMillis().toString().takeLast(8)}",
            appointmentId = appointmentId,
            patientName = patientName,
            doctorName = doctorName,
            specialty = specialty,
            consultationFee = fee,
            platformFee = platformFee,
            totalAmount = fee + platformFee,
            paymentMethod = paymentMethod,
            status = status,
            transactionId = transactionId.ifBlank { "TXN-${System.currentTimeMillis().toString().takeLast(7)}" },
            date = date
        )
        invoiceDao.insertInvoice(invoice)
        return invoice
    }

    suspend fun retryInvoicePayment(invoiceId: String, newTransactionId: String, newMethod: String) {
        invoiceDao.updateInvoicePayment(invoiceId, "PAID", newTransactionId, newMethod)
    }

    suspend fun recordFailedInvoice(
        doctorName: String,
        specialty: String,
        totalAmount: Double,
        paymentMethod: String,
        reason: String
    ): InvoiceEntity {
        val patientName = getCurrentUserName().ifBlank { "You" }
        return createInvoice(
            appointmentId = "apt_failed_${System.currentTimeMillis().toString().takeLast(6)}",
            patientName = patientName,
            doctorName = doctorName,
            specialty = specialty,
            fee = maxOf(0.0, totalAmount - 49.0),
            platformFee = 49.0,
            paymentMethod = paymentMethod,
            transactionId = "FAIL-${System.currentTimeMillis().toString().takeLast(6)}",
            status = "FAILED",
            date = "Today"
        )
    }

    // Reviews Flow
    fun getReviewsForDoctor(doctorId: String): Flow<List<ReviewEntity>> = reviewDao.getReviewsForDoctor(doctorId)
    suspend fun addReview(doctorId: String, patientName: String, rating: Int, text: String, visitType: String = "Verified video visit") {
        val review = ReviewEntity(
            id = "rev_${System.currentTimeMillis()}",
            doctorId = doctorId,
            patientName = patientName,
            rating = rating,
            reviewText = text,
            visitType = visitType,
            date = "Today"
        )
        reviewDao.insertReview(review)
    }

    // Notifications Flow
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> = notificationDao.getNotificationsForUser(userId)
    suspend fun markNotificationRead(id: String) = notificationDao.markAsRead(id)
    suspend fun markAllNotificationsRead(userId: String) = notificationDao.markAllAsRead(userId)

    // Chat Flow
    fun getMessagesForAppointment(appointmentId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForAppointment(appointmentId)
            .distinctUntilChanged()
            .map { entities ->
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
            id = "msg_${System.currentTimeMillis()}_${(100..999).random()}",
            appointmentId = appointmentId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            messageText = messageText.trim(),
            isPrescription = isPrescription,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(msgEntity)
        com.docdirect.app.data.firebase.FirebaseSyncManager.syncChatMessageToCloud(msgEntity)
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
            paymentMethod = entity.paymentMethod,
            createdAt = entity.createdAt
        )
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        val trimmed = email.lowercase().trim()
        val user = userDao.getUserByEmail(trimmed)
        val newHash = hashPassword(newPassword)
        if (user != null) {
            userDao.insertUser(user.copy(passwordHash = newHash))
        }
        return Result.success(Unit)
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
