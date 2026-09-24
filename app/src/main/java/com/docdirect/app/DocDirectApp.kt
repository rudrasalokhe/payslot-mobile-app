package com.docdirect.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.ui.auth.AuthViewModel
import com.docdirect.app.ui.auth.ClinicalAccessAuthScreen
import com.docdirect.app.ui.auth.DoctorSignupScreen
import com.docdirect.app.ui.chat.ChatViewModel
import com.docdirect.app.ui.chat.OnlineChatScreen
import com.docdirect.app.ui.consultation.AgoraVideoCallScreen
import com.docdirect.app.ui.consultation.TelehealthConsultationScreen
import com.docdirect.app.ui.doctor.DoctorAppointmentsScreen
import com.docdirect.app.ui.doctor.DoctorDashboardScreen
import com.docdirect.app.ui.doctor.DoctorViewModel
import com.docdirect.app.ui.doctor.SlotManagerScreen
import com.docdirect.app.ui.patient.*
import com.docdirect.app.ui.theme.DocDirectTheme
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocDirectApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val doctorViewModel: DoctorViewModel = viewModel()
    val patientViewModel: PatientViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()

    val startDest = remember {
        if (authViewModel.isLoggedIn()) {
            if (authViewModel.getCurrentUserRole() == UserRole.DOCTOR) "doctor_dashboard" else "aura_home"
        } else {
            "clinical_auth"
        }
    }

    DocDirectTheme {
        NavHost(
            navController = navController,
            startDestination = startDest
        ) {
            // Screen 1: Clinical Access & Intake Portal
            composable("clinical_auth") {
                ClinicalAccessAuthScreen(
                    authViewModel = authViewModel,
                    onNavigateToDoctorSignup = { navController.navigate("doctor_signup") },
                    onLoginSuccess = { role ->
                        val dest = if (role == UserRole.DOCTOR) "doctor_dashboard" else "aura_home"
                        navController.navigate(dest) {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("doctor_signup") {
                DoctorSignupScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSignupSuccess = {
                        navController.navigate("doctor_dashboard") {
                            popUpTo(0)
                        }
                    }
                )
            }

            // Screen 3: Patient Overview & Vitals Dashboard
            composable("aura_home") {
                AuraHomeScreen(
                    userName = patientViewModel.currentUserName.ifBlank { "Alex" },
                    onNavigateToClinicians = { navController.navigate("clinicians") },
                    onNavigateToMyAppointments = { navController.navigate("patient_appointments") },
                    onNavigateToTelehealthCall = { navController.navigate("patient_appointments") },
                    onOpenChat = { navController.navigate("chat/general_care") },
                    onSignOut = {
                        authViewModel.logout()
                        navController.navigate("clinical_auth") {
                            popUpTo(0)
                        }
                    }
                )
            }

            // Screen 4: Clinicians Directory & Fast Booking
            composable("clinicians") {
                CliniciansDirectoryScreen(
                    patientViewModel = patientViewModel,
                    onNavigateToHome = { navController.navigate("aura_home") },
                    onNavigateToTelehealthCall = { docName -> navController.navigate("telehealth_call/$docName") },
                    onOpenChat = { navController.navigate("chat/general_care") },
                    onSelectDoctor = { docId -> navController.navigate("doctor_detail/$docId") }
                )
            }

            // Screen 2: Telehealth Video Consultation & Biometrics HUD (UI mockup)
            composable(
                "telehealth_call/{doctorName}",
                arguments = listOf(navArgument("doctorName") { type = NavType.StringType })
            ) { backStackEntry ->
                val docName = backStackEntry.arguments?.getString("doctorName") ?: "Dr. Julian Vance, MD"
                TelehealthConsultationScreen(
                    doctorName = docName,
                    specialty = "Longevity & Preventive Medicine",
                    onNavigateBack = { navController.popBackStack() },
                    onOpenChat = { navController.navigate("chat/call_session") }
                )
            }

            // ===== AGORA VIDEO CALL =====
            composable(
                "video_call/{appointmentId}/{remoteUserName}",
                arguments = listOf(
                    navArgument("appointmentId") { type = NavType.StringType },
                    navArgument("remoteUserName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val aptId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                val remoteNameEncoded = backStackEntry.arguments?.getString("remoteUserName") ?: ""
                val remoteName = try { URLDecoder.decode(remoteNameEncoded, "UTF-8") } catch (e: Exception) { remoteNameEncoded }
                val isDoctor = authViewModel.getCurrentUserRole() == UserRole.DOCTOR
                val localUserId = authViewModel.getCurrentUserId()
                val localUserName = authViewModel.getCurrentUserName()

                AgoraVideoCallScreen(
                    appointmentId = aptId,
                    localUserId = localUserId,
                    localUserName = localUserName,
                    remoteUserName = remoteName,
                    isDoctor = isDoctor,
                    onCallEnded = { navController.popBackStack() }
                )
            }

            // Doctor Portal Screens
            composable("doctor_dashboard") {
                DoctorDashboardScreen(
                    viewModel = doctorViewModel,
                    onNavigateToSlots = { navController.navigate("doctor_slots") },
                    onNavigateToAppointments = { navController.navigate("doctor_appointments") },
                    onOpenChat = { aptId -> navController.navigate("chat/$aptId") },
                    onStartVideoCall = { aptId, patientName ->
                        val encodedName = URLEncoder.encode(patientName, "UTF-8")
                        navController.navigate("video_call/$aptId/$encodedName")
                    },
                    onSignOut = {
                        authViewModel.logout()
                        navController.navigate("clinical_auth") {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("doctor_slots") {
                SlotManagerScreen(
                    viewModel = doctorViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("doctor_appointments") {
                DoctorAppointmentsScreen(
                    viewModel = doctorViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenChat = { aptId -> navController.navigate("chat/$aptId") },
                    onStartVideoCall = { aptId, patientName ->
                        val encodedName = URLEncoder.encode(patientName, "UTF-8")
                        navController.navigate("video_call/$aptId/$encodedName")
                    }
                )
            }

            // Slot Booking & Checkout
            composable(
                "doctor_detail/{doctorId}",
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getString("doctorId") ?: ""
                DoctorDetailScreen(
                    doctorId = docId,
                    viewModel = patientViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onProceedToPayment = { slotId, symptoms ->
                        navController.navigate("checkout/$docId/$slotId?symptoms=$symptoms")
                    }
                )
            }

            composable(
                "checkout/{doctorId}/{slotId}?symptoms={symptoms}",
                arguments = listOf(
                    navArgument("doctorId") { type = NavType.StringType },
                    navArgument("slotId") { type = NavType.StringType },
                    navArgument("symptoms") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getString("doctorId") ?: ""
                val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
                val symptoms = backStackEntry.arguments?.getString("symptoms") ?: ""
                PaymentCheckoutScreen(
                    doctorId = docId,
                    slotId = slotId,
                    symptoms = symptoms,
                    viewModel = patientViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onBookingSuccess = { aptId ->
                        navController.navigate("patient_appointments") {
                            popUpTo("aura_home")
                        }
                    }
                )
            }

            // Real-time Chat
            composable(
                "chat/{appointmentId}",
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val aptId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                OnlineChatScreen(
                    appointmentId = aptId,
                    viewModel = chatViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Patient Appointments
            composable("patient_appointments") {
                PatientAppointmentsScreen(
                    viewModel = patientViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenChat = { aptId -> navController.navigate("chat/$aptId") },
                    onStartVideoCall = { aptId, doctorName ->
                        val encodedName = URLEncoder.encode(doctorName, "UTF-8")
                        navController.navigate("video_call/$aptId/$encodedName")
                    }
                )
            }
        }
    }
}
