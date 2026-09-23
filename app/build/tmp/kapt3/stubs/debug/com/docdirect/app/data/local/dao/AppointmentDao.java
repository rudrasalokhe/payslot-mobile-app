package com.docdirect.app.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0018\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\f0\t2\u0006\u0010\r\u001a\u00020\u0005H\'J\u001c\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\f0\t2\u0006\u0010\u000f\u001a\u00020\u0005H\'J\u0016\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/docdirect/app/data/local/dao/AppointmentDao;", "", "addPrescription", "", "appointmentId", "", "prescription", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAppointmentById", "Lkotlinx/coroutines/flow/Flow;", "Lcom/docdirect/app/data/local/entity/AppointmentEntity;", "getAppointmentsForDoctor", "", "doctorId", "getAppointmentsForPatient", "patientId", "insertAppointment", "appointment", "(Lcom/docdirect/app/data/local/entity/AppointmentEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAppointmentStatus", "status", "Lcom/docdirect/app/data/model/AppointmentStatus;", "(Ljava/lang/String;Lcom/docdirect/app/data/model/AppointmentStatus;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface AppointmentDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAppointment(@org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.local.entity.AppointmentEntity appointment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.local.entity.AppointmentEntity>> getAppointmentsForDoctor(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId);
    
    @androidx.room.Query(value = "SELECT * FROM appointments WHERE patientId = :patientId ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.local.entity.AppointmentEntity>> getAppointmentsForPatient(@org.jetbrains.annotations.NotNull()
    java.lang.String patientId);
    
    @androidx.room.Query(value = "SELECT * FROM appointments WHERE id = :appointmentId LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.docdirect.app.data.local.entity.AppointmentEntity> getAppointmentById(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId);
    
    @androidx.room.Query(value = "UPDATE appointments SET status = :status WHERE id = :appointmentId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateAppointmentStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId, @org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.model.AppointmentStatus status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE appointments SET prescription = :prescription, status = \'COMPLETED\' WHERE id = :appointmentId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addPrescription(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId, @org.jetbrains.annotations.NotNull()
    java.lang.String prescription, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}