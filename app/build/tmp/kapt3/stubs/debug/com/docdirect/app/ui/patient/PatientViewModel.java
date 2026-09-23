package com.docdirect.app.ui.patient;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J>\u0010\u001f\u001a\u00020\f2\u0006\u0010 \u001a\u00020\u00072\u0006\u0010!\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020\u00072\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\'H\u0086@\u00a2\u0006\u0002\u0010(J\u0010\u0010)\u001a\u0004\u0018\u00010\u00152\u0006\u0010 \u001a\u00020\u0007J\u001a\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020$0\u000b0+2\u0006\u0010 \u001a\u00020\u0007J\u000e\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020\u0007J\u000e\u0010/\u001a\u00020-2\u0006\u00100\u001a\u00020\u0007R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u001d\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000eR\u001d\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000eR\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u000eR\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u000e\u00a8\u00061"}, d2 = {"Lcom/docdirect/app/ui/patient/PatientViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_searchQuery", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_selectedCategory", "appointments", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/docdirect/app/data/model/Appointment;", "getAppointments", "()Lkotlinx/coroutines/flow/StateFlow;", "currentUserId", "getCurrentUserId", "()Ljava/lang/String;", "currentUserName", "getCurrentUserName", "doctors", "Lcom/docdirect/app/data/model/DoctorProfile;", "getDoctors", "filteredDoctors", "getFilteredDoctors", "repository", "Lcom/docdirect/app/data/repository/AppRepository;", "searchQuery", "getSearchQuery", "selectedCategory", "getSelectedCategory", "bookAppointment", "doctorId", "doctorName", "doctorSpecialty", "slot", "Lcom/docdirect/app/data/model/TimeSlot;", "symptoms", "fee", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/docdirect/app/data/model/TimeSlot;Ljava/lang/String;DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDoctorById", "getSlotsForDoctor", "Lkotlinx/coroutines/flow/Flow;", "setSearchQuery", "", "query", "setSelectedCategory", "category", "app_debug"})
public final class PatientViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.docdirect.app.data.repository.AppRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentUserId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentUserName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.DoctorProfile>> doctors = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.Appointment>> appointments = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _selectedCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> selectedCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.DoctorProfile>> filteredDoctors = null;
    
    public PatientViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentUserId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentUserName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.DoctorProfile>> getDoctors() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.Appointment>> getAppointments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSearchQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSelectedCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.docdirect.app.data.model.DoctorProfile>> getFilteredDoctors() {
        return null;
    }
    
    public final void setSearchQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void setSelectedCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.docdirect.app.data.model.DoctorProfile getDoctorById(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.docdirect.app.data.model.TimeSlot>> getSlotsForDoctor(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object bookAppointment(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, @org.jetbrains.annotations.NotNull()
    java.lang.String doctorName, @org.jetbrains.annotations.NotNull()
    java.lang.String doctorSpecialty, @org.jetbrains.annotations.NotNull()
    com.docdirect.app.data.model.TimeSlot slot, @org.jetbrains.annotations.NotNull()
    java.lang.String symptoms, double fee, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.docdirect.app.data.model.Appointment> $completion) {
        return null;
    }
}