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
import com.docdirect.app.ui.aven.*
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
            if (authViewModel.getCurrentUserRole() == UserRole.DOCTOR) "aven_clinician" else "aven_home"
        } else {
            "aven_welcome"
        }
    }

    DocDirectTheme {
        NavHost(
            navController = navController,
            startDestination = startDest
        ) {
            // ===== AVEN CORE SCREENS =====

            // Screen 0: Aven Welcome / Direction Entry Point
            composable("aven_welcome") {
                AvenWelcomeScreen(
                    onPatientEnter = {
                        navController.navigate("aven_home") {
                            popUpTo(0)
                        }
                    },
                    onClinicianEnter = {
                        navController.navigate("aven_clinician")
                    },
                    onOperationsEnter = {
                        navController.navigate("aven_operations")
                    }
                )
            }

            // Screen 1: Aven Patient Home
            composable("aven_home") {
                val userName = patientViewModel.currentUserName.ifBlank { "Aarav Mehta" }
                AvenHomeScreen(
                    patientViewModel = patientViewModel,
                    userName = userName,
                    onTabSelected = { tab ->
                        when (tab) {
                            "home" -> {}
                            "explore" -> navController.navigate("aven_explore")
                            "visits" -> navController.navigate("aven_visits")
                            "records" -> navController.navigate("aven_records")
                            "you" -> navController.navigate("aven_profile")
                        }
                    },
                    onDoctorSelected = { doctorId ->
                        navController.navigate("aven_doctor_profile/$doctorId")
                    },
                    onBookDoctor = { doctorId ->
                        navController.navigate("aven_booking/$doctorId")
                    },
                    onStartVideoCall = { aptId, doctorName ->
                        val encodedName = URLEncoder.encode(doctorName, "UTF-8")
                        navController.navigate("video_call/$aptId/$encodedName")
                    },
                    onOpenChat = { aptId ->
                        navController.navigate("chat/$aptId")
                    },
                    onOpenSearch = {
                        navController.navigate("aven_explore")
                    }
                )
            }

            // Screen 2: Aven Explore & Search Clinicians
            composable("aven_explore") {
                AvenExploreScreen(
                    patientViewModel = patientViewModel,
                    onTabSelected = { tab ->
                        when (tab) {
                            "home" -> navController.navigate("aven_home") { popUpTo("aven_home") { inclusive = true } }
                            "explore" -> {}
                            "visits" -> navController.navigate("aven_visits")
                            "records" -> navController.navigate("aven_records")
                            "you" -> navController.navigate("aven_profile")
                        }
                    },
                    onDoctorSelected = { doctorId ->
                        navController.navigate("aven_doctor_profile/$doctorId")
                    },
                    onBookDoctor = { doctorId ->
                        navController.navigate("aven_booking/$doctorId")
                    }
                )
            }

            // Screen 3: Clinician Profile (Dr Mira Shah)
            composable(
                "aven_doctor_profile/{doctorId}",
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: "doc_mira_shah"
                AvenDoctorProfileScreen(
                    doctorId = doctorId,
                    patientViewModel = patientViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onProceedToBook = { docId ->
                        navController.navigate("aven_booking/$docId")
                    }
                )
            }

            // Screen 4: 4-Step Booking, Intake & Payment Flow
            composable(
                "aven_booking/{doctorId}",
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: "doc_mira_shah"
                AvenBookingFlowScreen(
                    doctorId = doctorId,
                    patientViewModel = patientViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onBookingComplete = { aptId ->
                        navController.navigate("aven_visits") {
                            popUpTo("aven_home")
                        }
                    }
                )
            }

            // Screen 5: Appointments & Visits Hub
            composable("aven_visits") {
                AvenVisitsScreen(
                    patientViewModel = patientViewModel,
                    onTabSelected = { tab ->
                        when (tab) {
                            "home" -> navController.navigate("aven_home") { popUpTo("aven_home") { inclusive = true } }
                            "explore" -> navController.navigate("aven_explore")
                            "visits" -> {}
                            "records" -> navController.navigate("aven_records")
                            "you" -> navController.navigate("aven_profile")
                        }
                    },
                    onStartVideoCall = { aptId, doctorName ->
                        val encodedName = URLEncoder.encode(doctorName, "UTF-8")
                        navController.navigate("video_call/$aptId/$encodedName")
                    },
                    onOpenChat = { aptId ->
                        navController.navigate("chat/$aptId")
                    },
                    onBookNewVisit = {
                        navController.navigate("aven_explore")
                    }
                )
            }

            // Screen 6: Health Records & Consent Sharing Vault
            composable("aven_records") {
                AvenRecordsScreen(
                    onTabSelected = { tab ->
                        when (tab) {
                            "home" -> navController.navigate("aven_home") { popUpTo("aven_home") { inclusive = true } }
                            "explore" -> navController.navigate("aven_explore")
                            "visits" -> navController.navigate("aven_visits")
                            "records" -> {}
                            "you" -> navController.navigate("aven_profile")
                        }
                    }
                )
            }

            // Screen 7: Profile, Family, Billing & Settings ("You")
            composable("aven_profile") {
                AvenProfileScreen(
                    onTabSelected = { tab ->
                        when (tab) {
                            "home" -> navController.navigate("aven_home") { popUpTo("aven_home") { inclusive = true } }
                            "explore" -> navController.navigate("aven_explore")
                            "visits" -> navController.navigate("aven_visits")
                            "records" -> navController.navigate("aven_records")
                            "you" -> {}
                        }
                    },
                    onNavigateToClinicianWorkspace = {
                        navController.navigate("aven_clinician")
                    },
                    onNavigateToOperations = {
                        navController.navigate("aven_operations")
                    },
                    onSignOut = {
                        authViewModel.logout()
                        navController.navigate("aven_welcome") {
                            popUpTo(0)
                        }
                    }
                )
            }

            // Screen 8: Clinician Workspace (Dr Mira Shah)
            composable("aven_clinician") {
                AvenClinicianWorkspaceScreen(
                    doctorViewModel = doctorViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartVideoCall = { aptId, patientName ->
                        val encodedName = URLEncoder.encode(patientName, "UTF-8")
                        navController.navigate("video_call/$aptId/$encodedName")
                    },
                    onOpenChat = { aptId ->
                        navController.navigate("chat/$aptId")
                    }
                )
            }

            // Screen 9: Admin Operations & Audit Log
            composable("aven_operations") {
                AvenOperationsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ===== REAL-TIME AGORA RTC VIDEO CALL =====
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
                val localUserId = authViewModel.getCurrentUserId().ifBlank { "user_aarav_mehta" }
                val localUserName = authViewModel.getCurrentUserName().ifBlank { "Aarav Mehta" }

                AgoraVideoCallScreen(
                    appointmentId = aptId,
                    localUserId = localUserId,
                    localUserName = localUserName,
                    remoteUserName = remoteName,
                    isDoctor = isDoctor,
                    onCallEnded = { navController.popBackStack() }
                )
            }

            // ===== REAL-TIME ONLINE CHAT =====
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
