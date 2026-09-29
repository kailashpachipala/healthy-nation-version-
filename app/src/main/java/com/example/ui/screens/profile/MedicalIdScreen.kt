package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalIdScreen(
    viewModel: HealthyNationViewModel,
    onBack: () -> Unit
) {
    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Emergency Medical ID",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            item {
                PrototypeDisclaimerBanner(
                    title = "Lock Screen Medical ID Standard",
                    message = "Accessible by first responders and paramedics without unlocking device pin."
                )
            }

            // Official Emergency Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = RoseEmergencyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Emergency, contentDescription = null, tint = RoseEmergencyDark, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "MEDICAL ID",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                            HNStatusBadge(status = "VERIFIED", isSuccess = true)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = user?.name ?: "Alex Morgan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Text(
                            text = "DOB: June 14, 1994 (Age ${user?.age ?: 32}) • ${user?.gender ?: "Male"}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("BLOOD GROUP", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(user?.bloodGroup ?: "O+", color = Color.White, fontWeight = FontWeight.Black, fontSize = 26.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ORGAN DONOR", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(if (user?.organDonor == true) "YES" else "NO", color = EmeraldLight, fontWeight = FontWeight.Black, fontSize = 22.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("WEIGHT / HEIGHT", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${user?.weightKg?.toInt() ?: 72}kg / ${user?.heightCm?.toInt() ?: 178}cm", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // Allergies & Reactions
            item {
                HNCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = RoseEmergencyDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Critical Allergies & Warnings", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user?.allergies ?: "Penicillin, Peanuts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RoseEmergencyDark
                    )
                    Text(
                        text = "History of severe airway reaction. Administer Epinephrine and contact emergency contacts.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Chronic Conditions
            item {
                HNCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Healing, contentDescription = null, tint = TealDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Medical Conditions", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user?.chronicConditions ?: "Mild Asthma, Borderline Dyslipidemia",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Patient carries rescue Albuterol inhaler for acute bronchospasm.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Emergency Contacts
            item {
                HNCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContactPhone, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Emergency Next of Kin", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${user?.emergencyContactName ?: "Sarah Morgan"} (${user?.emergencyContactRelation ?: "Spouse"})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user?.emergencyContactPhone ?: "+1 (555) 902-4411",
                        fontSize = 13.sp,
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
