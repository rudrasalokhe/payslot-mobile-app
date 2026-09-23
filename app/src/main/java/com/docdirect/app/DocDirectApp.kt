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
import com.docdirect.app.data.repository.AppRepository
import com.docdirect.app.ui.auth.RoleSelectionScreen
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
    val doctorViewModel: DoctorViewModel = viewModel()
    val patientViewModel: PatientViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()

    var activeRole by remember { mutableStateOf(UserRole.DOCTOR) }

    DocDirectTheme {
        Scaffold(
            bottomBar = {
                // Quick Role Switcher Navigation Bar
                Surface(
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = activeRole == UserRole.DOCTOR,
                            onClick = {
                                activeRole = UserRole.DOCTOR
                                AppRepository.getInstance().setRole(UserRole.DOCTOR)
                                navController.navigate("doctor_dashboard") {
                                    popUpTo(0)
                                }
                            },
                            label = { Text("🩺 Doctor Portal") },
                            leadingIcon = {
                                if (activeRole == UserRole.DOCTOR) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )

                        FilterChip(
                            selected = activeRole == UserRole.PATIENT,
                            onClick = {
                                activeRole = UserRole.PATIENT
                                AppRepository.getInstance().setRole(UserRole.PATIENT)
                                navController.navigate("patient_home") {
                                    popUpTo(0)
                                }
                            },
                            label = { Text("👤 Patient Portal") },
                            leadingIcon = {
                                if (activeRole == UserRole.PATIENT) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = "doctor_dashboard"
                ) {
                    // Role Selector
                    composable("role_select") {
                        RoleSelectionScreen(
                            onSelectRole = { role ->
                                activeRole = role
                                if (role == UserRole.DOCTOR) {
                                    navController.navigate("doctor_dashboard")
                                } else {
                                    navController.navigate("patient_home")
                                }
                            }
                        )
                    }

                    // Doctor Interface Screens
                    composable("doctor_dashboard") {
                        DoctorDashboardScreen(
                            viewModel = doctorViewModel,
                            onNavigateToSlots = { navController.navigate("doctor_slots") },
                            onNavigateToAppointments = { navController.navigate("doctor_appointments") },
                            onOpenChat = { aptId -> navController.navigate("chat/$aptId") }
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

                    // Patient Interface Screens
                    composable("patient_home") {
                        DoctorSearchHomeScreen(
                            viewModel = patientViewModel,
                            onSelectDoctor = { docId -> navController.navigate("doctor_detail/$docId") },
                            onViewMyAppointments = { navController.navigate("patient_appointments") }
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

                    // Shared Online Chat Room Screen
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
    }
}
