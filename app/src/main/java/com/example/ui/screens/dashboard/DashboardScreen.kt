package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

enum class DashboardViewMode {
    ON_CALL_TEMPLATE,
    CLINICAL_TELEMETRY
}

@Composable
fun DashboardScreen(
    viewModel: HealthyNationViewModel,
    onNavigateToVitals: () -> Unit,
    onNavigateToTriage: () -> Unit,
    onNavigateToDoctors: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToMedications: () -> Unit,
    onNavigateToLabReports: () -> Unit,
    onNavigateToPharmacy: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToMedicalId: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val vitals by viewModel.vitals.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val medications by viewModel.medications.collectAsState()
    val labReports by viewModel.labReports.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    var viewMode by remember { mutableStateOf(DashboardViewMode.ON_CALL_TEMPLATE) }

    val latestHeartRate = vitals.firstOrNull { it.type == "HEART_RATE" }
    val latestBP = vitals.firstOrNull { it.type == "BLOOD_PRESSURE" }
    val latestSpO2 = vitals.firstOrNull { it.type == "SPO2" }
    val upcomingAppt = appointments.firstOrNull { it.status == "UPCOMING" }
    val pendingMed = medications.firstOrNull { !it.isTakenToday }
    val latestReport = labReports.firstOrNull()

    Scaffold(
        topBar = {
            RootlyHeaderBar(
                userName = user?.name ?: "Alex Morgan",
                unreadCount = unreadCount,
                onNotificationClick = onNavigateToNotifications,
                onAvatarClick = onNavigateToMedicalId,
                onSosClick = onNavigateToEmergency
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // View Mode Selector Segmented Pill
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            // On-Call Template Tab
                            Surface(
                                onClick = { viewMode = DashboardViewMode.ON_CALL_TEMPLATE },
                                shape = RoundedCornerShape(50),
                                color = if (viewMode == DashboardViewMode.ON_CALL_TEMPLATE) RootlyObsidian else Color.Transparent,
                                modifier = Modifier.testTag("tab_on_call_template")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (viewMode == DashboardViewMode.ON_CALL_TEMPLATE) RootlyNeonGreen else Color(0xFF64748B))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "On-Call Guard",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewMode == DashboardViewMode.ON_CALL_TEMPLATE) Color.White else Color(0xFF64748B)
                                    )
                                }
                            }

                            // Clinical Telemetry Tab
                            Surface(
                                onClick = { viewMode = DashboardViewMode.CLINICAL_TELEMETRY },
                                shape = RoundedCornerShape(50),
                                color = if (viewMode == DashboardViewMode.CLINICAL_TELEMETRY) RootlyObsidian else Color.Transparent,
                                modifier = Modifier.testTag("tab_clinical_telemetry")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonitorHeart,
                                        contentDescription = null,
                                        tint = if (viewMode == DashboardViewMode.CLINICAL_TELEMETRY) Color(0xFFFFB800) else Color(0xFF64748B),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Clinical Telemetry",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewMode == DashboardViewMode.CLINICAL_TELEMETRY) Color.White else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (viewMode == DashboardViewMode.ON_CALL_TEMPLATE) {
                // ==================== ROOTLY TEMPLATE VIEW ====================

                // 1. Ethereal Mountain Sunset Hero Banner
                item {
                    RootlyHeroHeader(
                        userName = user?.name ?: "Alex Morgan",
                        onBookDoctor = onNavigateToDoctors,
                        onStartTriage = onNavigateToTriage,
                        onEmergencySos = onNavigateToEmergency
                    )
                }

                // 2. Signature Rootly "You're On-Call" Dark Phone Card Mockup
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        RootlyOnCallPhoneCard(
                            userName = user?.name ?: "Alex",
                            physicianName = upcomingAppt?.doctorName ?: "Dr. Sarah Jenkins",
                            specialty = upcomingAppt?.specialty ?: "Cardiology & Telemetry",
                            shiftEndsText = "Today, 11:00 PM EDT",
                            nextShiftText = if (upcomingAppt != null) "${upcomingAppt.appointmentDate}, ${upcomingAppt.timeSlot}" else "Tomorrow, 9:00 AM EDT",
                            onPrimaryAction = onNavigateToDoctors
                        )
                    }
                }

                // 3. Desktop Mockup: On-Call Schedules Multi-Column Pastel Matrix
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        RootlyScheduleBoard(
                            onDaySelected = { /* selected */ }
                        )
                    }
                }

                // 4. Quick Access Shortcuts
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Incident & Care Quick Access",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickActionItem(
                                title = "AI Triage",
                                icon = Icons.Default.SmartToy,
                                color = OrangePrimary,
                                onClick = onNavigateToTriage
                            )
                            QuickActionItem(
                                title = "Doctors",
                                icon = Icons.Default.MedicalServices,
                                color = TealHealth,
                                onClick = onNavigateToDoctors
                            )
                            QuickActionItem(
                                title = "Pharmacy",
                                icon = Icons.Default.LocalPharmacy,
                                color = BlueInfo,
                                onClick = onNavigateToPharmacy
                            )
                            QuickActionItem(
                                title = "SOS Alert",
                                icon = Icons.Default.Emergency,
                                color = RoseEmergency,
                                onClick = onNavigateToEmergency
                            )
                        }
                    }
                }

                // 5. Live Telemetry Snapshot
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HNSectionHeader(
                            title = "Telemetry Stream",
                            subtitle = "Continuous biometric feed",
                            actionText = "All Vitals",
                            onActionClick = onNavigateToVitals
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HNMetricCard(
                                title = "Heart Rate",
                                value = "${latestHeartRate?.primaryValue?.toInt() ?: 72}",
                                unit = "bpm",
                                statusText = latestHeartRate?.status ?: "NORMAL",
                                icon = Icons.Default.Favorite,
                                accentColor = RoseEmergency,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToVitals
                            )
                            HNMetricCard(
                                title = "BP",
                                value = "${latestBP?.primaryValue?.toInt() ?: 118}/${latestBP?.secondaryValue?.toInt() ?: 78}",
                                unit = "mmHg",
                                statusText = latestBP?.status ?: "NORMAL",
                                icon = Icons.Default.Speed,
                                accentColor = TealHealth,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToVitals
                            )
                            HNMetricCard(
                                title = "SpO2",
                                value = "${latestSpO2?.primaryValue?.toInt() ?: 99}",
                                unit = "%",
                                statusText = latestSpO2?.status ?: "NORMAL",
                                icon = Icons.Default.Air,
                                accentColor = BlueInfo,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToVitals
                            )
                        }
                    }
                }

                // 6. Feature Trio Showcase (Multi-Sensor Telemetry, Doctor Collaboration, Automated Incident Escalation)
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        RootlyFeatureTrio(
                            onNavigateToVitals = onNavigateToVitals,
                            onNavigateToTriage = onNavigateToTriage,
                            onNavigateToEmergency = onNavigateToEmergency
                        )
                    }
                }

            } else {
                // ==================== CLINICAL TELEMETRY VIEW ====================

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        WellnessHeroCard(
                            score = user?.wellnessScore ?: 88,
                            statusText = "Cardiovascular & Metabolic Metrics Optimal",
                            onViewDetails = onNavigateToVitals
                        )
                    }
                }

                // Live Vitals Snapshot
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HNSectionHeader(
                            title = "Today's Vitals",
                            subtitle = "Continuous sensor sync",
                            actionText = "All Vitals",
                            onActionClick = onNavigateToVitals
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HNMetricCard(
                                title = "Heart Rate",
                                value = "${latestHeartRate?.primaryValue?.toInt() ?: 72}",
                                unit = "bpm",
                                statusText = latestHeartRate?.status ?: "NORMAL",
                                icon = Icons.Default.Favorite,
                                accentColor = RoseEmergency,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToVitals
                            )
                            HNMetricCard(
                                title = "Blood Pressure",
                                value = "${latestBP?.primaryValue?.toInt() ?: 118}/${latestBP?.secondaryValue?.toInt() ?: 78}",
                                unit = "mmHg",
                                statusText = latestBP?.status ?: "NORMAL",
                                icon = Icons.Default.Speed,
                                accentColor = TealHealth,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToVitals
                            )
                            HNMetricCard(
                                title = "Blood Oxygen",
                                value = "${latestSpO2?.primaryValue?.toInt() ?: 99}",
                                unit = "%",
                                statusText = latestSpO2?.status ?: "NORMAL",
                                icon = Icons.Default.Air,
                                accentColor = BlueInfo,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToVitals
                            )
                        }
                    }
                }

                // Upcoming Appointment Card
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HNSectionHeader(
                            title = "Upcoming Consultation",
                            actionText = "View All",
                            onActionClick = onNavigateToAppointments
                        )
                        if (upcomingAppt != null) {
                            HNCard(onClick = onNavigateToAppointments) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(TealContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (upcomingAppt.consultType == "VIDEO") Icons.Default.Videocam else Icons.Default.LocalHospital,
                                            contentDescription = null,
                                            tint = TealDark,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = upcomingAppt.doctorName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = upcomingAppt.specialty,
                                            fontSize = 13.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = OrangePrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${upcomingAppt.appointmentDate} • ${upcomingAppt.timeSlot}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = OrangePrimary
                                            )
                                        }
                                    }
                                    HNStatusBadge(status = upcomingAppt.consultType)
                                }
                            }
                        } else {
                            EmptyStateView(
                                title = "No upcoming appointments",
                                message = "Book a video or in-clinic consult with our specialists.",
                                actionButtonText = "Find Doctors",
                                onActionClick = onNavigateToDoctors
                            )
                        }
                    }
                }

                // Next Dose / Medication Reminder
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HNSectionHeader(
                            title = "Medication Schedule",
                            actionText = "Manage",
                            onActionClick = onNavigateToMedications
                        )
                        if (pendingMed != null) {
                            HNCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(OrangeContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Medication,
                                            contentDescription = null,
                                            tint = OnOrangeContainer,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${pendingMed.name} (${pendingMed.dosage})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "${pendingMed.instructions} • Due at ${pendingMed.timeOfDay}",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    HNButton(
                                        text = "Take Dose",
                                        isSecondary = true,
                                        onClick = { viewModel.toggleMedicationTaken(pendingMed.id, true) }
                                    )
                                }
                            }
                        } else {
                            HNCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "All scheduled doses taken for today!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Latest Diagnostic Lab Report
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HNSectionHeader(
                            title = "Latest Lab Report",
                            actionText = "All Reports",
                            onActionClick = onNavigateToLabReports
                        )
                        if (latestReport != null) {
                            HNCard(onClick = onNavigateToLabReports) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(BlueLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                                            contentDescription = null,
                                            tint = BlueInfo,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = latestReport.testName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${latestReport.labName} • ${latestReport.testDate}",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    HNStatusBadge(status = latestReport.status)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RootlyHeaderBar(
    userName: String,
    unreadCount: Int,
    onNotificationClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onSosClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Status Pill (inspired by Rootly top-left brand mark)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onAvatarClick)
                    .padding(vertical = 4.dp)
            ) {
                // Gradient Brand Mark
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(Color(0xFFFF7A00), Color(0xFFFFB800))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "rootly",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "health",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary,
                            letterSpacing = (-0.3).sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(RootlyNeonGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clinical Guard • Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Right Actions: SOS + Notification Bell + User Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Emergency SOS Pill
                Surface(
                    onClick = onSosClick,
                    shape = RoundedCornerShape(50),
                    color = RoseEmergency,
                    shadowElevation = 2.dp,
                    modifier = Modifier.testTag("top_bar_sos_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "Emergency SOS",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SOS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Notifications Bell
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = OrangePrimary) {
                                    Text("$unreadCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Profile Avatar Initial
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(TealDark)
                        .clickable(onClick = onAvatarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WellnessHeroCard(
    score: Int,
    statusText: String,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onViewDetails),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HEALTH INDEX STATUS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealLight,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(OrangePrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "EXCELLENT",
                        color = OrangePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$score",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = " / 100",
                            fontSize = 16.sp,
                            color = DarkTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        color = DarkTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(TealDark.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MonitorHeart,
                        contentDescription = null,
                        tint = TealLight,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

