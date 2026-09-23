package com.docdirect.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.docdirect.app.data.model.UserRole
import com.docdirect.app.ui.auth.AuthViewModel
import com.docdirect.app.ui.auth.DoctorSignupScreen
import com.docdirect.app.ui.auth.LoginScreen
import com.docdirect.app.ui.auth.PatientSignupScreen
import com.docdirect.app.ui.chat.ChatViewModel
import com.docdirect.app.ui.chat.OnlineChatScreen
import com.docdirect.app.ui.doctor.DoctorAppointmentsScreen
import com.docdirect.app.ui.doctor.DoctorDashboardScreen
import com.docdirect.app.ui.doctor.DoctorViewModel
import com.docdirect.app.ui.doctor.SlotManagerScreen
import com.docdirect.app.ui.patient.*
import com.docdirect.app.ui.theme.DocDirectTheme
import com.docdirect.app.ui.theme.TealPrimary

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
            if (authViewModel.getCurrentUserRole() == UserRole.DOCTOR) "doctor_dashboard" else "patient_home"
        } else {
            "login"
        }
    }

    DocDirectTheme {
        NavHost(
            navController = navController,
            startDestination = startDest
        ) {
            // Auth Routes
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToDoctorSignup = { navController.navigate("doctor_signup") },
                    onNavigateToPatientSignup = { navController.navigate("patient_signup") },
                    onLoginSuccess = { role ->
                        val dest = if (role == UserRole.DOCTOR) "doctor_dashboard" else "patient_home"
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

            composable("patient_signup") {
                PatientSignupScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSignupSuccess = {
                        navController.navigate("patient_home") {
                            popUpTo(0)
                        }
                    }
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
                        navController.navigate("login") {
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

            // Patient Portal Screens
            composable("patient_home") {
                DoctorSearchHomeScreen(
                    viewModel = patientViewModel,
                    onSelectDoctor = { docId -> navController.navigate("doctor_detail/$docId") },
                    onViewMyAppointments = { navController.navigate("patient_appointments") },
                    onSignOut = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }

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
                        navController.navigate("chat/$aptId") {
                            popUpTo("patient_home")
                        }
                    }
                )
            }

            composable("patient_appointments") {
                PatientAppointmentsScreen(
                    viewModel = patientViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenChat = { aptId -> navController.navigate("chat/$aptId") }
                )
            }

            // Shared Online Consultation Chat
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
