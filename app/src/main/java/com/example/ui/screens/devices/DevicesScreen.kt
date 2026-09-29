package com.example.ui.screens.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.local.ConnectedDeviceEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null
) {
    val devices by viewModel.connectedDevices.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    val isScanning by viewModel.isScanningDevices.collectAsState()
    var showScanSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Connected Health Devices",
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
                        viewModel.startDeviceScan()
                        showScanSheet = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Device", tint = OrangePrimary)
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
                    title = "BLE Health Telemetry Gateway",
                    message = "Simulated Bluetooth Smart connection & metrics exchange for wearable biometric sensors."
                )
            }

            item {
                HNSectionHeader(
                    title = "Paired Devices (${devices.size})",
                    actionText = "Scan New",
                    onActionClick = {
                        viewModel.startDeviceScan()
                        showScanSheet = true
                    }
                )
            }

            items(devices) { device ->
                ConnectedDeviceCard(
                    device = device,
                    onSync = { viewModel.syncDeviceTelemetry(device) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showScanSheet) {
        ModalBottomSheet(
            onDismissRequest = { showScanSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Scan Nearby Health Devices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Scanning Bluetooth Low Energy (BLE) channels for medical peripherals...",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                if (isScanning) {
                    LoadingStateView(message = "Scanning BLE frequencies...")
                } else {
                    scannedDevices.forEach { dev ->
                        HNCard(contentPadding = 12.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(dev.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${dev.type} • Signal: ${dev.signalStrengthRssi} dBm", fontSize = 12.sp, color = TextMuted)
                                }
                                HNButton(
                                    text = "Pair Sensor",
                                    isSecondary = true,
                                    onClick = { showScanSheet = false }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun ConnectedDeviceCard(
    device: ConnectedDeviceEntity,
    onSync: () -> Unit
) {
    HNCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (device.isConnected) TealContainer else SurfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (device.type) {
                        "SMARTWATCH" -> Icons.Default.Watch
                        "BP_MONITOR" -> Icons.Default.Speed
                        "GLUCOMETER" -> Icons.Default.Bloodtype
                        else -> Icons.Default.Bluetooth
                    },
                    contentDescription = null,
                    tint = if (device.isConnected) TealDark else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${device.lastSyncText} • Battery ${device.batteryPercent}%",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                HNStatusBadge(
                    status = if (device.isConnected) "CONNECTED" else "OFFLINE",
                    isSuccess = device.isConnected
                )
                if (device.isConnected) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = onSync,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Sync Now", color = OrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
