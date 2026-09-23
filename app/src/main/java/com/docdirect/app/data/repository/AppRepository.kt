package com.docdirect.app.data.repository

import com.docdirect.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppRepository private constructor() {

    private val _currentRole = MutableStateFlow(UserRole.DOCTOR)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentDoctor = MutableStateFlow(
        DoctorProfile(
            id = "doc_1",
            name = "Dr. Sarah Jenkins",
            medicalLicense = "MED-884920",
            specialty = "Cardiology",
            qualification = "MD, MBBS - Harvard Medical School",
            experienceYears = 12,
            consultationFee = 65.0,
            bio = "Senior Cardiologist specializing in preventive heart health, hypertension management, and echocardiography.",
            rating = 4.9,
            reviewCount = 142,
            isAvailable = true,
            hospitalAffiliation = "St. Jude Heart Institute",
            availableSlots = listOf(
                TimeSlot("s1", "Today", "09:00 AM", isBooked = true),
                TimeSlot("s2", "Today", "10:30 AM", isBooked = false),
                TimeSlot("s3", "Today", "02:00 PM", isBooked = false),
                TimeSlot("s4", "Today", "04:30 PM", isBooked = false),
                TimeSlot("s5", "Tomorrow", "11:00 AM", isBooked = false),
                TimeSlot("s6", "Tomorrow", "03:00 PM", isBooked = false)
            )
        )
    )
    val currentDoctor: StateFlow<DoctorProfile> = _currentDoctor.asStateFlow()

    private val _doctors = MutableStateFlow<List<DoctorProfile>>(
        listOf(
            _currentDoctor.value,
            DoctorProfile(
                id = "doc_2",
                name = "Dr. Michael Chen",
                medicalLicense = "MED-772910",
                specialty = "Dermatology",
                qualification = "MD - Johns Hopkins University",
                experienceYears = 8,
                consultationFee = 50.0,
                bio = "Expert Dermatologist treating acne, eczema, psoriasis, and cosmetic skincare solutions.",
                rating = 4.8,
                reviewCount = 98,
                isAvailable = true,
                hospitalAffiliation = "DermaCare Clinic",
                availableSlots = listOf(
                    TimeSlot("s21", "Today", "11:00 AM", isBooked = false),
                    TimeSlot("s22", "Today", "03:30 PM", isBooked = false)
                )
            ),
            DoctorProfile(
                id = "doc_3",
                name = "Dr. Emily Rodriguez",
                medicalLicense = "MED-910244",
                specialty = "Pediatrics",
                qualification = "MD, DCH - Stanford Medicine",
                experienceYears = 10,
                consultationFee = 55.0,
                bio = "Compassionate Pediatrician devoted to child growth, vaccination schedules, and infant care.",
                rating = 4.95,
                reviewCount = 210,
                isAvailable = true,
                hospitalAffiliation = "Children's General Hospital",
                availableSlots = listOf(
                    TimeSlot("s31", "Tomorrow", "09:30 AM", isBooked = false),
                    TimeSlot("s32", "Tomorrow", "01:00 PM", isBooked = false)
                )
            ),
            DoctorProfile(
                id = "doc_4",
                name = "Dr. James Wilson",
                medicalLicense = "MED-334182",
                specialty = "Neurology",
                qualification = "MD, PhD - Columbia University",
                experienceYears = 15,
                consultationFee = 85.0,
                bio = "Neurology specialist addressing migraine headaches, sleep disorders, and nerve conditions.",
                rating = 4.7,
                reviewCount = 76,
                isAvailable = false,
                hospitalAffiliation = "Brain & Spine Medical Center",
                availableSlots = emptyList()
            )
        )
    )
    val doctors: StateFlow<List<DoctorProfile>> = _doctors.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(
        listOf(
            Appointment(
                id = "apt_101",
                patientId = "pat_1",
                patientName = "Alex Rivera",
                doctorId = "doc_1",
                doctorName = "Dr. Sarah Jenkins",
                doctorSpecialty = "Cardiology",
                appointmentDate = "Today",
                appointmentTime = "09:00 AM",
                symptoms = "Mild chest pressure after running and elevated blood pressure readings (138/88).",
                feePaid = 65.0,
                status = AppointmentStatus.UPCOMING,
                transactionId = "TXN-8829104",
                prescription = ""
            ),
            Appointment(
                id = "apt_102",
                patientId = "pat_2",
                patientName = "Sophia Martinez",
                doctorId = "doc_1",
                doctorName = "Dr. Sarah Jenkins",
                doctorSpecialty = "Cardiology",
                appointmentDate = "Yesterday",
                appointmentTime = "04:00 PM",
                symptoms = "Follow-up consultation for cholesterol medication adjustment.",
                feePaid = 65.0,
                status = AppointmentStatus.COMPLETED,
                transactionId = "TXN-7739102",
                prescription = "Atorvastatin 10mg once daily at bedtime. Repeat lipid profile in 6 weeks."
            )
        )
    )
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "msg_1",
                appointmentId = "apt_101",
                senderId = "pat_1",
                senderName = "Alex Rivera",
                senderRole = UserRole.PATIENT,
                messageText = "Hello Dr. Jenkins! I booked this appointment to review my recent blood pressure logs.",
                timestamp = System.currentTimeMillis() - 600000
            ),
            ChatMessage(
                id = "msg_2",
                appointmentId = "apt_101",
                senderId = "doc_1",
                senderName = "Dr. Sarah Jenkins",
                senderRole = UserRole.DOCTOR,
                messageText = "Good morning Alex! Thank you for reaching out. Please share your recent BP readings.",
                timestamp = System.currentTimeMillis() - 300000
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    // Doctor management actions
    fun toggleDoctorAvailability(isAvailable: Boolean) {
        val updatedDoctor = _currentDoctor.value.copy(isAvailable = isAvailable)
        _currentDoctor.value = updatedDoctor
        _doctors.value = _doctors.value.map { if (it.id == updatedDoctor.id) updatedDoctor else it }
    }

    fun updateDoctorFee(newFee: Double) {
        val updatedDoctor = _currentDoctor.value.copy(consultationFee = newFee)
        _currentDoctor.value = updatedDoctor
        _doctors.value = _doctors.value.map { if (it.id == updatedDoctor.id) updatedDoctor else it }
    }

    fun updateDoctorProfile(specialty: String, qualification: String, bio: String, hospital: String) {
        val updatedDoctor = _currentDoctor.value.copy(
            specialty = specialty,
            qualification = qualification,
            bio = bio,
            hospitalAffiliation = hospital
        )
        _currentDoctor.value = updatedDoctor
        _doctors.value = _doctors.value.map { if (it.id == updatedDoctor.id) updatedDoctor else it }
    }

    fun addTimeSlot(date: String, time: String) {
        val newSlot = TimeSlot(id = "slot_${System.currentTimeMillis()}", date = date, time = time, isBooked = false)
        val updatedSlots = _currentDoctor.value.availableSlots + newSlot
        val updatedDoctor = _currentDoctor.value.copy(availableSlots = updatedSlots)
        _currentDoctor.value = updatedDoctor
        _doctors.value = _doctors.value.map { if (it.id == updatedDoctor.id) updatedDoctor else it }
    }

    fun deleteTimeSlot(slotId: String) {
        val updatedSlots = _currentDoctor.value.availableSlots.filter { it.id != slotId }
        val updatedDoctor = _currentDoctor.value.copy(availableSlots = updatedSlots)
        _currentDoctor.value = updatedDoctor
        _doctors.value = _doctors.value.map { if (it.id == updatedDoctor.id) updatedDoctor else it }
    }

    // Appointment actions
    fun bookAppointment(
        doctorId: String,
        patientName: String,
        date: String,
        time: String,
        symptoms: String,
        fee: Double
    ): Appointment {
        val doctor = _doctors.value.find { it.id == doctorId } ?: _currentDoctor.value
        val txnId = "TXN-${System.currentTimeMillis().toString().takeLast(7)}"
        val newApt = Appointment(
            id = "apt_${System.currentTimeMillis()}",
            patientId = "pat_current",
            patientName = patientName,
            doctorId = doctorId,
            doctorName = doctor.name,
            doctorSpecialty = doctor.specialty,
            appointmentDate = date,
            appointmentTime = time,
            symptoms = symptoms,
            feePaid = fee,
            status = AppointmentStatus.UPCOMING,
            transactionId = txnId
        )
        _appointments.value = listOf(newApt) + _appointments.value
        return newApt
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) it.copy(status = newStatus) else it
        }
    }

    fun addPrescription(appointmentId: String, prescriptionText: String) {
        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) it.copy(prescription = prescriptionText, status = AppointmentStatus.COMPLETED) else it
        }
        val currentApt = _appointments.value.find { it.id == appointmentId }
        val docName = currentApt?.doctorName ?: "Doctor"
        sendMessage(
            appointmentId = appointmentId,
            senderId = currentApt?.doctorId ?: "doc_1",
            senderName = docName,
            senderRole = UserRole.DOCTOR,
            messageText = "📋 PRESCRIPTION:\n$prescriptionText",
            isPrescription = true
        )
    }

    // Chat actions
    fun sendMessage(
        appointmentId: String,
        senderId: String,
        senderName: String,
        senderRole: UserRole,
        messageText: String,
        isPrescription: Boolean = false
    ) {
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            appointmentId = appointmentId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            messageText = messageText,
            isPrescription = isPrescription
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(): AppRepository {
            return instance ?: synchronized(this) {
                instance ?: AppRepository().also { instance = it }
            }
        }
    }
}
