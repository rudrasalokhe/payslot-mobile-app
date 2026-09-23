package com.docdirect.app.ui.doctor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u000f2\u0006\u0010\u001c\u001a\u00020\u000fJ\u000e\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u000fJ\u000e\u0010\u001f\u001a\u00020\u001a2\u0006\u0010 \u001a\u00020\u000fJ\u0016\u0010!\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\"\u001a\u00020\u000fJ\u000e\u0010#\u001a\u00020\u001a2\u0006\u0010$\u001a\u00020%J\u000e\u0010&\u001a\u00020\u001a2\u0006\u0010\'\u001a\u00020(J&\u0010)\u001a\u00020\u001a2\u0006\u0010*\u001a\u00020\u000f2\u0006\u0010+\u001a\u00020\u000f2\u0006\u0010,\u001a\u00020\u000f2\u0006\u0010-\u001a\u00020\u000fR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\nR\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\n\u00a8\u0006."}, d2 = {"Lcom/docdirect/app/ui/doctor/DoctorViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "appointments", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/docdirect/app/data/model/Appointment;", "getAppointments", "()Lkotlinx/coroutines/flow/StateFlow;", "availableSlots", "Lcom/docdirect/app/data/model/TimeSlot;", "getAvailableSlots", "currentUserId", "", "getCurrentUserId", "()Ljava/lang/String;", "doctorProfile", "Lcom/docdirect/app/data/model/DoctorProfile;", "getDoctorProfile", "repository", "Lcom/docdirect/app/data/repository/AppRepository;", "todayAppointments", "getTodayAppointments", "addSlot", "", "date", "time", "completeAppointment", "appointmentId", "deleteSlot", "slotId", "issuePrescription", "prescriptionText", "toggleAvailability", "isAvailable", "", "updateFee", "newFee", "", "updateProfile", "specialty", "qualification", "bio", "hospital", "app_debug"})
public final class DoctorViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.repository.AppRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentUserId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.docdirect.app.data.model.DoctorProfile> doctorProfile = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.TimeSlot>> availableSlots = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.Appointment>> appointments = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.Appointment>> todayAppointments = null;
    
    public DoctorViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentUserId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.docdirect.app.data.model.DoctorProfile> getDoctorProfile() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.TimeSlot>> getAvailableSlots() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.Appointment>> getAppointments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.Appointment>> getTodayAppointments() {
        return null;
    }
    
    public final void toggleAvailability(boolean isAvailable) {
    }
    
    public final void addSlot(@org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String time) {
    }
    
    public final void deleteSlot(@org.jetbrains.annotations.NotNull()
    java.lang.String slotId) {
    }
    
    public final void updateFee(double newFee) {
    }
    
    public final void updateProfile(@org.jetbrains.annotations.NotNull()
    java.lang.String specialty, @org.jetbrains.annotations.NotNull()
    java.lang.String qualification, @org.jetbrains.annotations.NotNull()
    java.lang.String bio, @org.jetbrains.annotations.NotNull()
    java.lang.String hospital) {
    }
    
    public final void completeAppointment(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId) {
    }
    
    public final void issuePrescription(@org.jetbrains.annotations.NotNull()
    java.lang.String appointmentId, @org.jetbrains.annotations.NotNull()
    java.lang.String prescriptionText) {
    }
}