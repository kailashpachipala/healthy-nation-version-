package com.example.ui.screens.doctors

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
import com.example.data.local.DoctorEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null,
    onSelectDoctorForBooking: (DoctorEntity) -> Unit
) {
    val doctors by viewModel.doctors.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecialty by remember { mutableStateOf("All") }
    var selectedDoctorDetail by remember { mutableStateOf<DoctorEntity?>(null) }

    val specialties = listOf("All", "Cardiologist", "General Physician", "Dermatologist", "Pulmonologist", "Pediatrician")

    val filteredDoctors = remember(doctors, searchQuery, selectedSpecialty) {
        doctors.filter { doc ->
            (selectedSpecialty == "All" || doc.specialty.contains(selectedSpecialty, ignoreCase = true)) &&
            (searchQuery.isBlank() || doc.name.contains(searchQuery, ignoreCase = true) || doc.hospitalName.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Verified Medical Specialists",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by doctor name or hospital...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Specialty Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(specialties) { spec ->
                        val isSelected = selectedSpecialty == spec
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSpecialty = spec },
                            label = { Text(spec, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealContainer,
                                selectedLabelColor = TealDark
                            )
                        )
                    }
                }
            }

            if (filteredDoctors.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Doctors Found",
                        message = "No specialists match your search criteria. Try a different specialty filter."
                    )
                }
            } else {
                items(filteredDoctors) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        onViewProfile = { selectedDoctorDetail = doctor },
                        onBookAppointment = { onSelectDoctorForBooking(doctor) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Doctor Profile Bottom Sheet
    if (selectedDoctorDetail != null) {
        DoctorProfileBottomSheet(
            doctor = selectedDoctorDetail!!,
            onDismiss = { selectedDoctorDetail = null },
            onBook = {
                val doc = selectedDoctorDetail!!
                selectedDoctorDetail = null
                onSelectDoctorForBooking(doc)
            }
        )
    }
}

@Composable
private fun DoctorCard(
    doctor: DoctorEntity,
    onViewProfile: () -> Unit,
    onBookAppointment: () -> Unit
) {
    HNCard(onClick = onViewProfile) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar Initials
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(TealContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = doctor.name.replace("Dr. ", "").split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                    color = TealDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
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
                        text = doctor.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${doctor.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Text(
                    text = doctor.specialty,
                    fontSize = 13.sp,
                    color = TealDark,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${doctor.qualification} • ${doctor.experienceYears} yrs exp",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = doctor.hospitalName,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Next Slot",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = doctor.nextAvailableSlot,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldSuccess
                        )
                    }

                    HNButton(
                        text = "Book ($${doctor.consultationFee.toInt()})",
                        onClick = onBookAppointment
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoctorProfileBottomSheet(
    doctor: DoctorEntity,
    onDismiss: () -> Unit,
    onBook: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(TealContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = doctor.name.replace("Dr. ", "").split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                    color = TealDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = doctor.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = doctor.specialty,
                fontSize = 14.sp,
                color = TealHealth,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${doctor.hospitalName} • ${doctor.experienceYears} Years Practice",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating & Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(title = "Rating", value = "${doctor.rating} ★")
                ProfileStat(title = "Patients", value = "${doctor.reviewCount}+")
                ProfileStat(title = "Fee", value = "$${doctor.consultationFee.toInt()}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "About Doctor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = doctor.about,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            HNButton(
                text = "Proceed to Booking",
                icon = Icons.Default.CalendarMonth,
                onClick = onBook,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProfileStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OrangePrimary)
        Text(text = title, fontSize = 12.sp, color = TextMuted)
    }
}
