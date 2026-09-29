package com.example.ui.screens.emergency

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.services.emergency.NearbyEmergencyFacility
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null,
    onOpenMedicalId: () -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val emergencyContacts by viewModel.emergencyContacts.collectAsState()
    val facilities = remember { viewModel.emergencyService.getNearbyEmergencyFacilities() }

    // Pulsing animation for the SOS button
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Emergency Response (SOS)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RoseEmergencyDark)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PrototypeDisclaimerBanner(
                    title = "Emergency Dispatch Simulator",
                    message = "Tapping Ambulance initiates an actual telephone dialer intent to local emergency dispatch (911)."
                )
            }

            // Big SOS Pulse Button
            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .scale(pulseScale)
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(RoseEmergency)
                        .clickable {
                            viewModel.emergencyService.dialEmergencyAmbulance(context, "911")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "Trigger Emergency",
                            tint = Color.White,
                            modifier = Modifier.size(52.dp)
                        )
                        Text(
                            text = "CALL 911",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Quick Actions: Ambulance & Medical ID
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.emergencyService.dialEmergencyAmbulance(context, "911") },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseEmergencyDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dial Ambulance", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onOpenMedicalId,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, TealHealth),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = TealHealth)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Medical ID", fontWeight = FontWeight.Bold, color = TealHealth)
                    }
                }
            }

            // Emergency Medical Snapshot
            item {
                HNCard(backgroundColor = SurfaceSubtle) {
                    Text(
                        text = "Emergency Medical Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Blood Type", fontSize = 11.sp, color = TextMuted)
                            Text(user?.bloodGroup ?: "O+", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoseEmergencyDark)
                        }
                        Column {
                            Text("Organ Donor", fontSize = 11.sp, color = TextMuted)
                            Text(if (user?.organDonor == true) "YES" else "NO", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EmeraldSuccess)
                        }
                        Column {
                            Text("Critical Allergies", fontSize = 11.sp, color = TextMuted)
                            Text(user?.allergies?.take(20) ?: "Penicillin", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // Emergency Contacts
            item {
                HNSectionHeader(title = "Primary Emergency Contacts")
            }

            items(emergencyContacts) { contact ->
                HNCard(contentPadding = 12.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${contact.relationship} • ${contact.phoneNumber}", fontSize = 12.sp, color = TextSecondary)
                        }
                        IconButton(
                            onClick = { viewModel.emergencyService.dialEmergencyAmbulance(context, contact.phoneNumber) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TealContainer)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = TealDark)
                        }
                    }
                }
            }

            // Nearest Trauma Centers & ER Hospitals
            item {
                HNSectionHeader(
                    title = "Nearby Emergency Facilities",
                    subtitle = "Real-time distance and trauma capability"
                )
            }

            items(facilities) { fac ->
                HospitalFacilityCard(facility = fac, onCall = { viewModel.emergencyService.dialEmergencyAmbulance(context, fac.phone) })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HospitalFacilityCard(
    facility: NearbyEmergencyFacility,
    onCall: () -> Unit
) {
    HNCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(facility.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(facility.address, fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (facility.hasTraumaCenter) {
                        HNStatusBadge(status = "Level 1 Trauma", isCritical = true)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (facility.has24HrPharmacy) {
                        HNStatusBadge(status = "24hr Pharmacy")
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${facility.distanceKm} km",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = OrangePrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoseLight)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call ER", tint = RoseEmergencyDark, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
