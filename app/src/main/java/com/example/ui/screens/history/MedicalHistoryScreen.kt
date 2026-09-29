package com.example.ui.screens.history

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistoryScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null
) {
    val user by viewModel.user.collectAsState()
    val medications by viewModel.medications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medical History & Health Profile",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Header Card
            item {
                HNCard(backgroundColor = DarkSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(TealDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.bloodGroup ?: "O+",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = user?.name ?: "Alex Morgan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Age ${user?.age ?: 32} • ${user?.gender ?: "Male"} • Height ${user?.heightCm?.toInt() ?: 178}cm • Weight ${user?.weightKg?.toInt() ?: 72}kg",
                                fontSize = 12.sp,
                                color = DarkTextSecondary
                            )
                        }
                    }
                }
            }

            // Chronic Conditions Section
            item {
                HistorySectionHeader(title = "Chronic & Active Conditions", icon = Icons.Default.Favorite)
                HNCard {
                    ConditionItem(name = "Mild Bronchial Asthma", diagnosed = "Diagnosed 2021", status = "Managed", severity = "Mild")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ConditionItem(name = "Borderline Dyslipidemia", diagnosed = "Diagnosed 2024", status = "Lifestyle Monitoring", severity = "Moderate")
                }
            }

            // Active Medications Section
            item {
                HistorySectionHeader(title = "Current Prescribed Medications", icon = Icons.Default.Medication)
                HNCard {
                    medications.forEachIndexed { index, med ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${med.name} ${med.dosage}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${med.frequency} • ${med.instructions}", fontSize = 12.sp, color = TextSecondary)
                            }
                            HNStatusBadge(status = if (med.isPrescription) "Rx" else "OTC")
                        }
                        if (index < medications.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        }
                    }
                }
            }

            // Allergies Section
            item {
                HistorySectionHeader(title = "Known Allergies & Adverse Reactions", icon = Icons.Default.Warning)
                HNCard {
                    AllergyPillRow(allergen = "Penicillin", reaction = "Severe anaphylactoid hives", isSevere = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    AllergyPillRow(allergen = "Peanuts & Tree Nuts", reaction = "Oral swelling & respiratory distress", isSevere = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    AllergyPillRow(allergen = "Dust Mite Inhalant", reaction = "Sneezing, allergic rhinitis", isSevere = false)
                }
            }

            // Past Surgeries & Procedures
            item {
                HistorySectionHeader(title = "Surgical & Hospitalization History", icon = Icons.Default.LocalHospital)
                HNCard {
                    ProcedureItem(procedure = "Laparoscopic Appendectomy", hospital = "Mercy General Hospital", year = "2018", outcome = "Resolved successfully without complications")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ProcedureItem(procedure = "Arthroscopic Knee Meniscectomy", hospital = "Orthopedic Specialty Center", year = "2022", outcome = "Full range of motion recovered")
                }
            }

            // Family Medical History
            item {
                HistorySectionHeader(title = "Family Medical History", icon = Icons.Default.FamilyRestroom)
                HNCard {
                    FamilyHistoryItem(relation = "Father", condition = "Type 2 Diabetes Mellitus, Hypertension", onsetAge = "52 yrs")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    FamilyHistoryItem(relation = "Maternal Grandmother", condition = "Cardiovascular Disease (CAD)", onsetAge = "68 yrs")
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HistorySectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConditionItem(name: String, diagnosed: String, status: String, severity: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = diagnosed, fontSize = 12.sp, color = TextMuted)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            HNStatusBadge(status = status)
        }
    }
}

@Composable
private fun AllergyPillRow(allergen: String, reaction: String, isSevere: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = allergen, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isSevere) RoseEmergencyDark else TextPrimary)
            Text(text = reaction, fontSize = 12.sp, color = TextSecondary)
        }
        HNStatusBadge(status = if (isSevere) "SEVERE" else "MILD", isCritical = isSevere)
    }
}

@Composable
private fun ProcedureItem(procedure: String, hospital: String, year: String, outcome: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = procedure, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = year, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = OrangePrimary)
        }
        Text(text = hospital, fontSize = 12.sp, color = TextSecondary)
        Text(text = outcome, fontSize = 11.sp, color = TextMuted)
    }
}

@Composable
private fun FamilyHistoryItem(relation: String, condition: String, onsetAge: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = relation, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = condition, fontSize = 12.sp, color = TextSecondary)
        }
        Text(text = "Onset: $onsetAge", fontSize = 11.sp, color = TextMuted)
    }
}
