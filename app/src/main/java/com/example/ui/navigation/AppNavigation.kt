package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DoctorEntity
import com.example.ui.screens.appointments.AppointmentsScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.devices.DevicesScreen
import com.example.ui.screens.doctors.DoctorsScreen
import com.example.ui.screens.emergency.EmergencyScreen
import com.example.ui.screens.history.MedicalHistoryScreen
import com.example.ui.screens.insurance.InsuranceScreen
import com.example.ui.screens.labreports.LabReportsScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.orders.OrdersScreen
import com.example.ui.screens.payments.CheckoutScreen
import com.example.ui.screens.pharmacy.PharmacyScreen
import com.example.ui.screens.profile.MedicalIdScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.triage.TriageScreen
import com.example.ui.screens.vitals.VitalsScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

enum class MainTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    HEALTH("Health", Icons.Default.Favorite),
    CARE("Care", Icons.Default.MedicalServices),
    PHARMACY("Pharmacy", Icons.Default.LocalPharmacy),
    MORE("More", Icons.Default.AccountCircle)
}

enum class SubScreen {
    NONE,
    TRIAGE,
    DOCTORS,
    APPOINTMENTS,
    VITALS,
    MEDICAL_HISTORY,
    LAB_REPORTS,
    CART_CHECKOUT,
    ORDERS,
    EMERGENCY_SOS,
    INSURANCE,
    DEVICES,
    NOTIFICATIONS,
    MEDICAL_ID
}

@Composable
fun AppNavigation(viewModel: HealthyNationViewModel) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }
    var preselectedDoctorForBooking by remember { mutableStateOf<DoctorEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiNotice.collect { notice ->
            snackbarHostState.showSnackbar(notice.message)
        }
    }

    if (!isOnboardingCompleted) {
        OnboardingScreen(
            onGetStarted = { viewModel.completeOnboarding() },
            onSignIn = { viewModel.signIn("alex.morgan@healthynation.org", "pass") }
        )
        return
    }

    if (!isAuthenticated) {
        AuthScreen(
            onAuthSuccess = { email, pass -> viewModel.signIn(email, pass) },
            onBackToOnboarding = { /* staying on auth */ }
        )
        return
    }

    // Secondary screen back handler
    BackHandler(enabled = currentSubScreen != SubScreen.NONE) {
        currentSubScreen = SubScreen.NONE
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentSubScreen == SubScreen.NONE) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    MainTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OrangePrimary,
                                selectedTextColor = OrangePrimary,
                                indicatorColor = OrangeContainer.copy(alpha = 0.5f),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentSubScreen) {
                SubScreen.TRIAGE -> TriageScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE },
                    onBookDoctor = { spec ->
                        currentSubScreen = SubScreen.DOCTORS
                    },
                    onEmergencySos = { currentSubScreen = SubScreen.EMERGENCY_SOS }
                )
                SubScreen.DOCTORS -> DoctorsScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE },
                    onSelectDoctorForBooking = { doc ->
                        preselectedDoctorForBooking = doc
                        currentSubScreen = SubScreen.APPOINTMENTS
                    }
                )
                SubScreen.APPOINTMENTS -> AppointmentsScreen(
                    viewModel = viewModel,
                    preselectedDoctor = preselectedDoctorForBooking,
                    onBack = {
                        preselectedDoctorForBooking = null
                        currentSubScreen = SubScreen.NONE
                    },
                    onFindDoctors = { currentSubScreen = SubScreen.DOCTORS }
                )
                SubScreen.VITALS -> VitalsScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE }
                )
                SubScreen.MEDICAL_HISTORY -> MedicalHistoryScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE }
                )
                SubScreen.LAB_REPORTS -> LabReportsScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE }
                )
                SubScreen.CART_CHECKOUT -> CheckoutScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE },
                    onOrderPlaced = { orderId ->
                        currentSubScreen = SubScreen.ORDERS
                    }
                )
                SubScreen.ORDERS -> OrdersScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE },
                    onShopPharmacy = {
                        currentSubScreen = SubScreen.NONE
                        currentTab = MainTab.PHARMACY
                    }
                )
                SubScreen.EMERGENCY_SOS -> EmergencyScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE },
                    onOpenMedicalId = { currentSubScreen = SubScreen.MEDICAL_ID }
                )
                SubScreen.INSURANCE -> InsuranceScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE }
                )
                SubScreen.DEVICES -> DevicesScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE }
                )
                SubScreen.NOTIFICATIONS -> NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE }
                )
                SubScreen.MEDICAL_ID -> MedicalIdScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = SubScreen.NONE }
                )
                SubScreen.NONE -> {
                    // Render Tab Screens
                    when (currentTab) {
                        MainTab.HOME -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToVitals = { currentSubScreen = SubScreen.VITALS },
                            onNavigateToTriage = { currentSubScreen = SubScreen.TRIAGE },
                            onNavigateToDoctors = { currentSubScreen = SubScreen.DOCTORS },
                            onNavigateToAppointments = { currentSubScreen = SubScreen.APPOINTMENTS },
                            onNavigateToMedications = { currentSubScreen = SubScreen.MEDICAL_HISTORY },
                            onNavigateToLabReports = { currentSubScreen = SubScreen.LAB_REPORTS },
                            onNavigateToPharmacy = {
                                currentTab = MainTab.PHARMACY
                            },
                            onNavigateToEmergency = { currentSubScreen = SubScreen.EMERGENCY_SOS },
                            onNavigateToNotifications = { currentSubScreen = SubScreen.NOTIFICATIONS },
                            onNavigateToMedicalId = { currentSubScreen = SubScreen.MEDICAL_ID }
                        )
                        MainTab.HEALTH -> VitalsScreen(
                            viewModel = viewModel,
                            onBack = null
                        )
                        MainTab.CARE -> DoctorsScreen(
                            viewModel = viewModel,
                            onBack = null,
                            onSelectDoctorForBooking = { doc ->
                                preselectedDoctorForBooking = doc
                                currentSubScreen = SubScreen.APPOINTMENTS
                            }
                        )
                        MainTab.PHARMACY -> PharmacyScreen(
                            viewModel = viewModel,
                            onBack = null,
                            onNavigateToCart = { currentSubScreen = SubScreen.CART_CHECKOUT },
                            onNavigateToOrders = { currentSubScreen = SubScreen.ORDERS }
                        )
                        MainTab.MORE -> ProfileScreen(
                            viewModel = viewModel,
                            onNavigateToMedicalId = { currentSubScreen = SubScreen.MEDICAL_ID },
                            onNavigateToInsurance = { currentSubScreen = SubScreen.INSURANCE },
                            onNavigateToDevices = { currentSubScreen = SubScreen.DEVICES },
                            onNavigateToMedicalHistory = { currentSubScreen = SubScreen.MEDICAL_HISTORY },
                            onNavigateToOrders = { currentSubScreen = SubScreen.ORDERS },
                            onNavigateToEmergency = { currentSubScreen = SubScreen.EMERGENCY_SOS }
                        )
                    }
                }
            }
        }
    }
}
