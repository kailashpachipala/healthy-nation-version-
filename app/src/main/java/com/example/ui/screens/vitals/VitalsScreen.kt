package com.example.ui.screens.vitals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.data.local.VitalEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null
) {
    val vitals by viewModel.vitals.collectAsState()
    var showLogDialog by remember { mutableStateOf(false) }
    var selectedVitalTab by remember { mutableStateOf("ALL") }

    val heartRateReadings = remember(vitals) { vitals.filter { it.type == "HEART_RATE" } }
    val bpReadings = remember(vitals) { vitals.filter { it.type == "BLOOD_PRESSURE" } }
    val spo2Readings = remember(vitals) { vitals.filter { it.type == "SPO2" } }
    val tempReadings = remember(vitals) { vitals.filter { it.type == "TEMPERATURE" } }
    val stepReadings = remember(vitals) { vitals.filter { it.type == "STEPS" } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vitals & Biometrics",
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
                    FilledTonalButton(
                        onClick = { showLogDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = OrangeContainer),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = OnOrangeContainer, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Vital", color = OnOrangeContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            // Live Status Banner
            item {
                HNCard(backgroundColor = DarkSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REAL-TIME TELEMETRY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealLight,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Continuous Sensor Sync Active",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TealDark.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BluetoothConnected, contentDescription = null, tint = TealLight, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // Trend Chart for Heart Rate
            item {
                HNCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Heart Rate Trend (BPM)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Latest: ${heartRateReadings.firstOrNull()?.primaryValue?.toInt() ?: 72} bpm • Resting average: 74 bpm",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        HNStatusBadge(status = "NORMAL")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val samplePoints = remember(heartRateReadings) {
                        if (heartRateReadings.isNotEmpty()) {
                            heartRateReadings.take(8).map { it.primaryValue.toFloat() }.reversed()
                        } else {
                            listOf(70f, 74f, 72f, 78f, 75f, 72f, 76f, 72f)
                        }
                    }

                    VitalsSparklineChart(
                        dataPoints = samplePoints,
                        lineColor = RoseEmergency,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("12 hrs ago", fontSize = 11.sp, color = TextMuted)
                        Text("6 hrs ago", fontSize = 11.sp, color = TextMuted)
                        Text("Just now", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            // Vitals Metric Cards Grid
            item {
                Text(
                    text = "Key Biometric Indicators",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HNMetricCard(
                        title = "Blood Pressure",
                        value = "${bpReadings.firstOrNull()?.primaryValue?.toInt() ?: 118}/${bpReadings.firstOrNull()?.secondaryValue?.toInt() ?: 78}",
                        unit = "mmHg",
                        statusText = "OPTIMAL",
                        icon = Icons.Default.Speed,
                        accentColor = TealHealth,
                        modifier = Modifier.weight(1f)
                    )
                    HNMetricCard(
                        title = "Blood Oxygen (SpO2)",
                        value = "${spo2Readings.firstOrNull()?.primaryValue?.toInt() ?: 99}",
                        unit = "%",
                        statusText = "NORMAL",
                        icon = Icons.Default.Air,
                        accentColor = BlueInfo,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HNMetricCard(
                        title = "Body Temp",
                        value = "${tempReadings.firstOrNull()?.primaryValue ?: 98.6}",
                        unit = "°F",
                        statusText = "NORMAL",
                        icon = Icons.Default.Thermostat,
                        accentColor = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                    HNMetricCard(
                        title = "Today's Steps",
                        value = "${stepReadings.firstOrNull()?.primaryValue?.toInt() ?: 8420}",
                        unit = "steps",
                        statusText = "GOAL 84%",
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        accentColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Logs List
            item {
                HNSectionHeader(title = "Recent Biometric Logs")
            }

            if (vitals.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Vitals Logged",
                        message = "Start tracking your daily vitals to receive automated health alerts.",
                        actionButtonText = "Log First Reading",
                        onActionClick = { showLogDialog = true }
                    )
                }
            } else {
                items(vitals.size) { index ->
                    val item = vitals[index]
                    VitalRowItem(item = item)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showLogDialog) {
        LogVitalDialog(
            onDismiss = { showLogDialog = false },
            onConfirm = { type, primary, secondary, unit ->
                viewModel.logVital(type, primary, secondary, unit)
                showLogDialog = false
            }
        )
    }
}

@Composable
fun VitalsSparklineChart(
    dataPoints: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (dataPoints.size < 2) return@Canvas

        val minVal = (dataPoints.minOrNull() ?: 60f) - 5f
        val maxVal = (dataPoints.maxOrNull() ?: 100f) + 5f
        val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

        val stepX = size.width / (dataPoints.size - 1)
        val path = Path()

        dataPoints.forEachIndexed { i, pt ->
            val x = i * stepX
            val y = size.height - ((pt - minVal) / range) * size.height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw point dots
        dataPoints.forEachIndexed { i, pt ->
            val x = i * stepX
            val y = size.height - ((pt - minVal) / range) * size.height
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun VitalRowItem(item: VitalEntity) {
    val dateStr = remember(item.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    HNCard(contentPadding = 12.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.type) {
                            "HEART_RATE" -> RoseLight
                            "BLOOD_PRESSURE" -> TealContainer
                            "SPO2" -> BlueLight
                            "STEPS" -> EmeraldLight
                            else -> AmberLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.type) {
                        "HEART_RATE" -> Icons.Default.Favorite
                        "BLOOD_PRESSURE" -> Icons.Default.Speed
                        "SPO2" -> Icons.Default.Air
                        "STEPS" -> Icons.AutoMirrored.Filled.DirectionsWalk
                        else -> Icons.Default.Thermostat
                    },
                    contentDescription = null,
                    tint = when (item.type) {
                        "HEART_RATE" -> RoseEmergencyDark
                        "BLOOD_PRESSURE" -> TealDark
                        "SPO2" -> BlueInfo
                        "STEPS" -> EmeraldSuccess
                        else -> AmberWarning
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (item.secondaryValue != null) "${item.primaryValue.toInt()}/${item.secondaryValue.toInt()} ${item.unit}"
                    else "${item.primaryValue} ${item.unit}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                HNStatusBadge(status = item.status)
            }
        }
    }
}

@Composable
private fun LogVitalDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: String, primary: Double, secondary: Double?, unit: String) -> Unit
) {
    var selectedType by remember { mutableStateOf("HEART_RATE") }
    var primaryVal by remember { mutableStateOf("72") }
    var secondaryVal by remember { mutableStateOf("80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Log Vital Reading", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "HEART_RATE",
                        onClick = { selectedType = "HEART_RATE" },
                        label = { Text("Heart Rate") }
                    )
                    FilterChip(
                        selected = selectedType == "BLOOD_PRESSURE",
                        onClick = { selectedType = "BLOOD_PRESSURE" },
                        label = { Text("Blood Pressure") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "SPO2",
                        onClick = { selectedType = "SPO2" },
                        label = { Text("SpO2") }
                    )
                    FilterChip(
                        selected = selectedType == "TEMPERATURE",
                        onClick = { selectedType = "TEMPERATURE" },
                        label = { Text("Temperature") }
                    )
                }

                OutlinedTextField(
                    value = primaryVal,
                    onValueChange = { primaryVal = it },
                    label = {
                        Text(if (selectedType == "BLOOD_PRESSURE") "Systolic (mmHg)" else "Reading Value")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedType == "BLOOD_PRESSURE") {
                    OutlinedTextField(
                        value = secondaryVal,
                        onValueChange = { secondaryVal = it },
                        label = { Text("Diastolic (mmHg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            HNButton(
                text = "Save Reading",
                onClick = {
                    val p = primaryVal.toDoubleOrNull() ?: 70.0
                    val s = if (selectedType == "BLOOD_PRESSURE") secondaryVal.toDoubleOrNull() ?: 80.0 else null
                    val unit = when (selectedType) {
                        "HEART_RATE" -> "bpm"
                        "BLOOD_PRESSURE" -> "mmHg"
                        "SPO2" -> "%"
                        "TEMPERATURE" -> "°F"
                        else -> "units"
                    }
                    onConfirm(selectedType, p, s, unit)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
