package com.example.ui.screens.labreports

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.data.local.LabReportEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabReportsScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null
) {
    val reports by viewModel.labReports.collectAsState()
    var selectedReportForInspection by remember { mutableStateOf<LabReportEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diagnostic Lab Reports",
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
            item {
                PrototypeDisclaimerBanner(
                    title = "Simulated Diagnostic Repository",
                    message = "Certified NABL / CAP standard reports linked directly from partner hospital laboratories."
                )
            }

            if (reports.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Lab Reports Found",
                        message = "Your pathology and radiology diagnostic reports will automatically appear here once processed."
                    )
                }
            } else {
                items(reports) { report ->
                    LabReportCardItem(
                        report = report,
                        onClick = { selectedReportForInspection = report }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (selectedReportForInspection != null) {
        LabReportInspectionSheet(
            report = selectedReportForInspection!!,
            onDismiss = { selectedReportForInspection = null }
        )
    }
}

@Composable
private fun LabReportCardItem(
    report: LabReportEntity,
    onClick: () -> Unit
) {
    val isAttention = report.status == "ATTENTION"

    HNCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isAttention) AmberLight else BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isAttention) AmberWarning else BlueInfo,
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
                        text = report.testName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    HNStatusBadge(status = report.status, isWarning = isAttention)
                }

                Text(
                    text = "${report.category} • ${report.labName}",
                    fontSize = 12.sp,
                    color = TealDark,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = report.summary,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prescribed by ${report.doctorName}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = report.testDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabReportInspectionSheet(
    report: LabReportEntity,
    onDismiss: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HNStatusBadge(status = report.status)
                Text(
                    text = report.testDate,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
            }

            Text(
                text = report.testName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HNCard(backgroundColor = SurfaceSubtle, contentPadding = 12.dp) {
                Text(text = "Lab Provider: ${report.labName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Referring Physician: ${report.doctorName}", fontSize = 12.sp, color = TextSecondary)
                Text(text = "Specimen Category: ${report.category}", fontSize = 12.sp, color = TextMuted)
            }

            Text(
                text = "Clinical Findings Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = report.summary,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Text(
                text = "Key Observations & Reference Values",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = report.keyFindings,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            HNButton(
                text = "Download Official PDF (Simulated)",
                icon = Icons.Default.Download,
                isSecondary = true,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
