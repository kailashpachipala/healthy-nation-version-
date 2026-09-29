package com.example.ui.screens.insurance

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
fun InsuranceScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null
) {
    val policy by viewModel.insurancePolicy.collectAsState()
    var showClaimModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Health Insurance & Claims",
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
            item {
                PrototypeDisclaimerBanner(
                    title = "Direct Cashless TPA Integration",
                    message = "Policy and claim verification adapter connected to Star Health & CareShield sandbox."
                )
            }

            // Digital Health Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = TealDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = policy?.providerName ?: "CareShield Health Insurance",
                                color = TealLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            HNStatusBadge(status = policy?.status ?: "ACTIVE", isSuccess = true)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = policy?.planName ?: "Family Health Platinum Comprehensive",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Text(
                            text = "Policy No: ${policy?.policyNumber ?: "HN-CARE-882914-X"}",
                            color = TealLight.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Sum Insured", color = TealLight.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text("$${policy?.coverageAmount?.toInt() ?: 100000}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Valid Through", color = TealLight.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text(policy?.validTill ?: "Dec 31, 2027", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // Coverage Usage Breakdown
            item {
                HNCard {
                    Text("Coverage Usage This Period", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.14f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = OrangePrimary,
                        trackColor = SurfaceBorder
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Used: $14,200", fontSize = 12.sp, color = TextSecondary)
                        Text("Remaining: $85,800", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
                    }
                }
            }

            // Actions: New Claim & Network Hospitals
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HNButton(
                        text = "File Reimbursement",
                        icon = Icons.Default.PostAdd,
                        onClick = { showClaimModal = true },
                        modifier = Modifier.weight(1f)
                    )
                    HNButton(
                        text = "Cashless Hospitals",
                        icon = Icons.Default.LocalHospital,
                        isSecondary = true,
                        onClick = { /* simulated hospital finder */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Claims History
            item {
                HNSectionHeader(title = "Recent Claims & Pre-Authorizations")
                HNCard {
                    ClaimHistoryItem(
                        claimId = "CLM-2026-9481",
                        hospital = "Metro Heart Institute",
                        amount = "$2,400.00",
                        date = "Aug 18, 2026",
                        status = "SETTLED"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ClaimHistoryItem(
                        claimId = "CLM-2026-7120",
                        hospital = "Apex Diagnostics Center",
                        amount = "$350.00",
                        date = "Jun 12, 2026",
                        status = "APPROVED"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showClaimModal) {
        AlertDialog(
            onDismissRequest = { showClaimModal = false },
            title = { Text("File Insurance Claim", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "In production mode, you can upload hospital discharge summaries and pharmacy invoices. For this prototype, a cashless claim ticket has been generated under #CLM-${(1000..9999).random()}.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                HNButton(text = "Submit Claim Ticket", onClick = { showClaimModal = false })
            },
            dismissButton = {
                TextButton(onClick = { showClaimModal = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ClaimHistoryItem(
    claimId: String,
    hospital: String,
    amount: String,
    date: String,
    status: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(claimId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(hospital, fontSize = 12.sp, color = TextSecondary)
            Text(date, fontSize = 11.sp, color = TextMuted)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OrangePrimary)
            HNStatusBadge(status = status, isSuccess = status == "SETTLED")
        }
    }
}
