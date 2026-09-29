package com.example.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.data.local.AppointmentEntity
import com.example.data.local.DoctorEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: HealthyNationViewModel,
    preselectedDoctor: DoctorEntity? = null,
    onBack: (() -> Unit)? = null,
    onFindDoctors: () -> Unit
) {
    val appointments by viewModel.appointments.collectAsState()
    val doctors by viewModel.doctors.collectAsState()

    var showBookingSheet by remember { mutableStateOf(preselectedDoctor != null) }
    var selectedDoctor by remember { mutableStateOf(preselectedDoctor ?: doctors.firstOrNull()) }
    var selectedAppointmentDetails by remember { mutableStateOf<AppointmentEntity?>(null) }

    val upcoming = remember(appointments) { appointments.filter { it.status == "UPCOMING" } }
    val past = remember(appointments) { appointments.filter { it.status != "UPCOMING" } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Consultations",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        selectedDoctor = doctors.firstOrNull()
                        showBookingSheet = true
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "New Booking", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upcoming Header
            item {
                HNSectionHeader(
                    title = "Upcoming Appointments",
                    subtitle = "${upcoming.size} scheduled consultation" + if (upcoming.size != 1) "s" else ""
                )
            }

            if (upcoming.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Upcoming Visits",
                        message = "Book a consultation with certified doctors via secure HD video or in-person clinic visit.",
                        actionButtonText = "Schedule Consult",
                        onActionClick = {
                            selectedDoctor = doctors.firstOrNull()
                            showBookingSheet = true
                        }
                    )
                }
            } else {
                items(upcoming) { appt ->
                    AppointmentCard(
                        appointment = appt,
                        onClick = { selectedAppointmentDetails = appt },
                        onCancel = { viewModel.cancelAppointment(appt.id) }
                    )
                }
            }

            // Past Appointments
            item {
                HNSectionHeader(
                    title = "Consultation History",
                    subtitle = "Past reviews & clinical records"
                )
            }

            if (past.isEmpty()) {
                item {
                    Text(
                        text = "No previous consultation records found.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(past) { appt ->
                    AppointmentCard(
                        appointment = appt,
                        onClick = { selectedAppointmentDetails = appt },
                        onCancel = null
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Booking Flow Bottom Sheet
    if (showBookingSheet && selectedDoctor != null) {
        BookingModalBottomSheet(
            doctor = selectedDoctor!!,
            allDoctors = doctors,
            onSelectDoctor = { selectedDoctor = it },
            onDismiss = { showBookingSheet = false },
            onConfirmBooking = { date, time, type, notes ->
                viewModel.bookAppointment(
                    doctor = selectedDoctor!!,
                    date = date,
                    timeSlot = time,
                    type = type,
                    notes = notes
                )
                showBookingSheet = false
            }
        )
    }

    // Appointment Detail Modal
    if (selectedAppointmentDetails != null) {
        AppointmentDetailModal(
            appointment = selectedAppointmentDetails!!,
            onDismiss = { selectedAppointmentDetails = null },
            onCancel = {
                viewModel.cancelAppointment(selectedAppointmentDetails!!.id)
                selectedAppointmentDetails = null
            }
        )
    }
}

@Composable
private fun AppointmentCard(
    appointment: AppointmentEntity,
    onClick: () -> Unit,
    onCancel: (() -> Unit)?
) {
    val isVideo = appointment.consultType == "VIDEO"
    val isUpcoming = appointment.status == "UPCOMING"

    HNCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isVideo) TealContainer else OrangeContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = if (isVideo) TealDark else OnOrangeContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = appointment.doctorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HNStatusBadge(status = appointment.status)
                }

                Text(
                    text = appointment.specialty,
                    fontSize = 13.sp,
                    color = TealDark,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${appointment.appointmentDate} at ${appointment.timeSlot}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isVideo && appointment.meetingUrl != null && isUpcoming) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HNButton(
                        text = "Join Video Call",
                        icon = Icons.Default.Videocam,
                        isSecondary = true,
                        onClick = { /* Simulated telehealth launcher */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isUpcoming && onCancel != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Cancel Appointment", color = RoseEmergency, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingModalBottomSheet(
    doctor: DoctorEntity,
    allDoctors: List<DoctorEntity>,
    onSelectDoctor: (DoctorEntity) -> Unit,
    onDismiss: () -> Unit,
    onConfirmBooking: (date: String, time: String, type: String, notes: String) -> Unit
) {
    var selectedDate by remember { mutableStateOf("Tomorrow, Oct 1") }
    var selectedTime by remember { mutableStateOf("04:30 PM") }
    var consultType by remember { mutableStateOf("VIDEO") } // VIDEO or IN_PERSON
    var notes by remember { mutableStateOf("") }

    val dateOptions = listOf("Today, Sep 30", "Tomorrow, Oct 1", "Wed, Oct 2", "Thu, Oct 3")
    val timeOptions = listOf("10:00 AM", "11:30 AM", "02:15 PM", "04:30 PM", "06:00 PM")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Book Consultation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Doctor selector preview
            HNCard(backgroundColor = SurfaceSubtle, contentPadding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(TealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = doctor.name.take(2),
                            color = TealDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${doctor.specialty} • $${doctor.consultationFee.toInt()}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // Consult Mode
            Text("Consultation Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = consultType == "VIDEO",
                    onClick = { consultType = "VIDEO" },
                    label = { Text("Video Consult (Telehealth)") },
                    leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = consultType == "IN_PERSON",
                    onClick = { consultType = "IN_PERSON" },
                    label = { Text("In-Clinic Visit") },
                    leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Date Selection
            Text("Select Date", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dateOptions) { date ->
                    FilterChip(
                        selected = selectedDate == date,
                        onClick = { selectedDate = date },
                        label = { Text(date) }
                    )
                }
            }

            // Time Selection
            Text("Select Time Slot", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(timeOptions) { slot ->
                    FilterChip(
                        selected = selectedTime == slot,
                        onClick = { selectedTime = slot },
                        label = { Text(slot) }
                    )
                }
            }

            // Additional clinical notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Reason for visit / symptoms (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            HNButton(
                text = "Confirm & Schedule ($${doctor.consultationFee.toInt()})",
                onClick = { onConfirmBooking(selectedDate, selectedTime, consultType, notes) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AppointmentDetailModal(
    appointment: AppointmentEntity,
    onDismiss: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(appointment.doctorName, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Specialty: ${appointment.specialty}", fontWeight = FontWeight.Medium)
                Text(text = "Date: ${appointment.appointmentDate} at ${appointment.timeSlot}")
                Text(text = "Mode: ${appointment.consultType}")
                Text(text = "Status: ${appointment.status}")
                if (appointment.notes.isNotBlank()) {
                    Text(text = "Notes: ${appointment.notes}", color = TextSecondary)
                }
                if (appointment.meetingUrl != null) {
                    Text(text = "Virtual Link: ${appointment.meetingUrl}", color = TealHealth, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            if (appointment.status == "UPCOMING") {
                TextButton(onClick = onCancel) {
                    Text("Cancel Visit", color = RoseEmergency, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
