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
import com.docdirect.app.ui.consultation.TelehealthConsultationScreen
import com.docdirect.app.ui.doctor.DoctorAppointmentsScreen
import com.docdirect.app.ui.doctor.DoctorDashboardScreen
import com.docdirect.app.ui.doctor.DoctorViewModel
import com.docdirect.app.ui.doctor.SlotManagerScreen
import com.docdirect.app.ui.patient.*
import com.docdirect.app.ui.theme.DocDirectTheme

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
                    onNavigateToTelehealthCall = { navController.navigate("telehealth_call/Dr. Julian Vance, MD") },
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

            // Screen 2: Telehealth Video Consultation & Biometrics HUD
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

            // Doctor Portal Screens
            composable("doctor_dashboard") {
                DoctorDashboardScreen(
                    viewModel = doctorViewModel,
                    onNavigateToSlots = { navController.navigate("doctor_slots") },
                    onNavigateToAppointments = { navController.navigate("doctor_appointments") },
                    onOpenChat = { aptId -> navController.navigate("chat/$aptId") },
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
                    onOpenChat = { aptId -> navController.navigate("chat/$aptId") }
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
                        navController.navigate("telehealth_call/Dr. Julian Vance, MD") {
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
        }
    }
}
