package com.docdirect.app.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a8\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 n2\u00020\u0001:\u0001nB\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010!J&\u0010\"\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u001f2\u0006\u0010$\u001a\u00020\u001f2\u0006\u0010%\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010&J^\u0010\'\u001a\u00020(2\u0006\u0010#\u001a\u00020\u001f2\u0006\u0010)\u001a\u00020\u001f2\u0006\u0010*\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020\u001f2\u0006\u0010,\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u001f2\u0006\u0010$\u001a\u00020\u001f2\u0006\u0010%\u001a\u00020\u001f2\u0006\u0010.\u001a\u00020\u001f2\u0006\u0010/\u001a\u000200H\u0086@\u00a2\u0006\u0002\u00101J\u0016\u00102\u001a\u00020\u001d2\u0006\u0010-\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u00103J\u0016\u00104\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0\u00132\u0006\u0010\u001e\u001a\u00020\u001fJ\u001a\u00105\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u00140\u00132\u0006\u0010#\u001a\u00020\u001fJ\u001a\u00106\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u00140\u00132\u0006\u0010+\u001a\u00020\u001fJ\u0016\u00107\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00150\u00132\u0006\u00108\u001a\u00020\u001fJ\b\u00109\u001a\u0004\u0018\u00010\u001fJ\u0006\u0010:\u001a\u00020\u001fJ\u0006\u0010;\u001a\u00020\u000bJ\u001a\u0010<\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020=0\u00140\u00132\u0006\u0010\u001e\u001a\u00020\u001fJ\u001a\u0010>\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020?0\u00140\u00132\u0006\u0010#\u001a\u00020\u001fJ\u0010\u0010@\u001a\u00020\u001f2\u0006\u0010A\u001a\u00020\u001fH\u0002J\u0006\u0010B\u001a\u00020CJ,\u0010D\u001a\b\u0012\u0004\u0012\u00020F0E2\u0006\u0010G\u001a\u00020\u001f2\u0006\u0010A\u001a\u00020\u001fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bH\u0010!J\u0006\u0010I\u001a\u00020\u001dJV\u0010J\u001a\u00020\u001d2\u0006\u00108\u001a\u00020\u001f2\u0006\u0010K\u001a\u00020\u001f2\u0006\u0010L\u001a\u00020\u001f2\u0006\u0010M\u001a\u00020\u001f2\u0006\u0010N\u001a\u00020\u001f2\u0006\u0010O\u001a\u00020P2\u0006\u0010/\u001a\u0002002\u0006\u0010Q\u001a\u00020\u001f2\u0006\u0010R\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010SJ<\u0010T\u001a\b\u0012\u0004\u0012\u00020F0E2\u0006\u0010K\u001a\u00020\u001f2\u0006\u0010G\u001a\u00020\u001f2\u0006\u0010A\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u000bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bV\u0010WJ@\u0010X\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010Y\u001a\u00020\u001f2\u0006\u0010Z\u001a\u00020\u001f2\u0006\u0010[\u001a\u00020\u000b2\u0006\u0010\\\u001a\u00020\u001f2\b\b\u0002\u0010]\u001a\u00020CH\u0086@\u00a2\u0006\u0002\u0010^J\u0010\u0010_\u001a\u00020(2\u0006\u0010`\u001a\u00020aH\u0002J\u001e\u0010b\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u001f2\u0006\u0010c\u001a\u00020CH\u0086@\u00a2\u0006\u0002\u0010dJ\u001e\u0010e\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010f\u001a\u00020gH\u0086@\u00a2\u0006\u0002\u0010hJ\u001e\u0010i\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u001f2\u0006\u0010j\u001a\u000200H\u0086@\u00a2\u0006\u0002\u0010kJ6\u0010l\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u001f2\u0006\u0010M\u001a\u00020\u001f2\u0006\u0010N\u001a\u00020\u001f2\u0006\u0010Q\u001a\u00020\u001f2\u0006\u0010R\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010mR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006o"}, d2 = {"Lcom/docdirect/app/data/repository/AppRepository;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "appointmentDao", "Lcom/docdirect/app/data/local/dao/AppointmentDao;", "chatDao", "Lcom/docdirect/app/data/local/dao/ChatDao;", "currentRole", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/docdirect/app/data/model/UserRole;", "getCurrentRole", "()Lkotlinx/coroutines/flow/StateFlow;", "db", "Lcom/docdirect/app/data/local/AppDatabase;", "doctorDao", "Lcom/docdirect/app/data/local/dao/DoctorDao;", "doctors", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/docdirect/app/data/model/DoctorProfile;", "getDoctors", "()Lkotlinx/coroutines/flow/Flow;", "sessionManager", "Lcom/docdirect/app/data/auth/SessionManager;", "userDao", "Lcom/docdirect/app/data/local/dao/UserDao;", "addPrescription", "", "appointmentId", "", "prescriptionText", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addTimeSlot", "doctorId", "date", "time", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "bookAppointment", "Lcom/docdirect/app/data/model/Appointment;", "doctorName", "doctorSpecialty", "patientId", "patientName", "slotId", "symptoms", "fee", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTimeSlot", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAppointmentById", "getAppointmentsForDoctor", "getAppointmentsForPatient", "getCurrentDoctorProfile", "userId", "getCurrentUserId", "getCurrentUserName", "getCurrentUserRole", "getMessagesForAppointment", "Lcom/docdirect/app/data/model/ChatMessage;", "getSlotsForDoctor", "Lcom/docdirect/app/data/model/TimeSlot;", "hashPassword", "password", "isLoggedIn", "", "login", "Lkotlin/Result;", "Lcom/docdirect/app/data/local/entity/UserEntity;", "email", "login-0E7RQCE", "logout", "registerDoctorProfile", "name", "license", "specialty", "qualification", "experienceYears", "", "bio", "hospital", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IDLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerUser", "role", "registerUser-yxL6bBk", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/docdirect/app/data/model/UserRole;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendMessage", "senderId", "senderName", "senderRole", "messageText", "isPrescription", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/docdirect/app/data/model/UserRole;Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toAppointment", "entity", "Lcom/docdirect/app/data/local/entity/AppointmentEntity;", "toggleDoctorAvailability", "isAvailable", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAppointmentStatus", "status", "Lcom/docdirect/app/data/model/AppointmentStatus;", "(Ljava/lang/String;Lcom/docdirect/app/data/model/AppointmentStatus;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateDoctorFee", "newFee", "(Ljava/lang/String;DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateDoctorProfile", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class AppRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.local.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.local.dao.UserDao userDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.local.dao.DoctorDao doctorDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.local.dao.AppointmentDao appointmentDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.local.dao.ChatDao chatDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.auth.SessionManager sessionManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.docdirect.app.data.model.UserRole> currentRole = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.model.DoctorProfile>> doctors = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.docdirect.app.data.repository.AppRepository instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.docdirect.app.data.repository.AppRepository.Companion Companion = null;
    
    private AppRepository(android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.docdirect.app.data.model.UserRole> getCurrentRole() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.model.DoctorProfile>> getDoctors() {
        return null;
    }
    
    public final boolean isLoggedIn() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUserId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentUserName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.docdirect.app.data.model.UserRole getCurrentUserRole() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object registerDoctorProfile(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String license, @org.jetbrains.annotations.NotNull()
    java.lang.String specialty, @org.jetbrains.annotations.NotNull()
    java.lang.String qualification, int experienceYears, double fee, @org.jetbrains.annotations.NotNull()
    java.lang.String bio, @org.jetbrains.annotations.NotNull()
    java.lang.String hospital, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void logout() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.docdirect.app.data.model.DoctorProfile> getCurrentDoctorProfile(@org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.model.TimeSlot>> getSlotsForDoctor(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addTimeSlot(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String time, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteTimeSlot(@org.jetbrains.annotations.NotNull()
    java.lang.String slotId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleDoctorAvailability(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, boolean isAvailable, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateDoctorFee(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, double newFee, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateDoctorProfile(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, @org.jetbrains.annotations.NotNull()
    java.lang.String specialty, @org.jetbrains.annotations.NotNull()
    java.lang.String qualification, @org.jetbrains.annotations.NotNull()
    java.lang.String bio, @org.jetbrains.annotations.NotNull()
    java.lang.String hospital, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.model.Appointment>> getAppointmentsForDoctor(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.model.Appointment>> getAppointmentsForPatient(@org.jetbrains.annotations.NotNull()
    java.lang.String patientId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.docdirect.app.data.model.Appointment> getAppointmentById(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object bookAppointment(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, @org.jetbrains.annotations.NotNull()
    java.lang.String doctorName, @org.jetbrains.annotations.NotNull()
    java.lang.String doctorSpecialty, @org.jetbrains.annotations.NotNull()
    java.lang.String patientId, @org.jetbrains.annotations.NotNull()
    java.lang.String patientName, @org.jetbrains.annotations.NotNull()
    java.lang.String slotId, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String time, @org.jetbrains.annotations.NotNull()
    java.lang.String symptoms, double fee, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.docdirect.app.data.model.Appointment> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateAppointmentStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId, @org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.model.AppointmentStatus status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addPrescription(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId, @org.jetbrains.annotations.NotNull()
    java.lang.String prescriptionText, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.model.ChatMessage>> getMessagesForAppointment(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object sendMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId, @org.jetbrains.annotations.NotNull()
    java.lang.String senderId, @org.jetbrains.annotations.NotNull()
    java.lang.String senderName, @org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.model.UserRole senderRole, @org.jetbrains.annotations.NotNull()
    java.lang.String messageText, boolean isPrescription, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final com.docdirect.app.data.model.Appointment toAppointment(com.docdirect.app.data.local.entity.AppointmentEntity entity) {
        return null;
    }
    
    private final java.lang.String hashPassword(java.lang.String password) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/docdirect/app/data/repository/AppRepository$Companion;", "", "()V", "instance", "Lcom/docdirect/app/data/repository/AppRepository;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.docdirect.app.data.repository.AppRepository getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}