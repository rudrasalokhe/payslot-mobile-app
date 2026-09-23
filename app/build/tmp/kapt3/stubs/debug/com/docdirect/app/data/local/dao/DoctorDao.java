package com.docdirect.app.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bH\'J\u0018\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\b2\u0006\u0010\f\u001a\u00020\u0005H\'J\u0018\u0010\r\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\b2\u0006\u0010\u000e\u001a\u00020\u0005H\'J\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\t0\b2\u0006\u0010\f\u001a\u00020\u0005H\'J\u0016\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u0016\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u001a"}, d2 = {"Lcom/docdirect/app/data/local/dao/DoctorDao;", "", "deleteSlot", "", "slotId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllDoctors", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/docdirect/app/data/local/entity/DoctorEntity;", "getDoctorByIdFlow", "doctorId", "getDoctorByUserId", "userId", "getDoctorByUserIdFlow", "getSlotsForDoctor", "Lcom/docdirect/app/data/local/entity/SlotEntity;", "insertDoctor", "doctor", "(Lcom/docdirect/app/data/local/entity/DoctorEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertSlot", "slot", "(Lcom/docdirect/app/data/local/entity/SlotEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markSlotBooked", "updateDoctor", "app_debug"})
@androidx.room.Dao()
public abstract interface DoctorDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertDoctor(@org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.local.entity.DoctorEntity doctor, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateDoctor(@org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.local.entity.DoctorEntity doctor, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM doctors")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.local.entity.DoctorEntity>> getAllDoctors();
    
    @androidx.room.Query(value = "SELECT * FROM doctors WHERE id = :doctorId LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.docdirect.app.data.local.entity.DoctorEntity> getDoctorByIdFlow(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId);
    
    @androidx.room.Query(value = "SELECT * FROM doctors WHERE userId = :userId LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.docdirect.app.data.local.entity.DoctorEntity> getDoctorByUserIdFlow(@org.jetbrains.annotations.NotNull()
    java.lang.String userId);
    
    @androidx.room.Query(value = "SELECT * FROM doctors WHERE userId = :userId LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDoctorByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.docdirect.app.data.local.entity.DoctorEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertSlot(@org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.local.entity.SlotEntity slot, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM slots WHERE id = :slotId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteSlot(@org.jetbrains.annotations.NotNull()
    java.lang.String slotId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM slots WHERE doctorId = :doctorId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.local.entity.SlotEntity>> getSlotsForDoctor(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId);
    
    @androidx.room.Query(value = "UPDATE slots SET isBooked = 1 WHERE id = :slotId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object markSlotBooked(@org.jetbrains.annotations.NotNull()
    java.lang.String slotId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}