package com.example.services.device

import kotlinx.coroutines.delay

data class ScannedDevice(
    val id: String,
    val name: String,
    val type: String,
    val signalStrengthRssi: Int,
    val isPaired: Boolean = false
)

interface DeviceProvider {
    val providerName: String
    val isMock: Boolean
    suspend fun scanForNearbyHealthDevices(): List<ScannedDevice>
    suspend fun syncTelemetry(deviceId: String): Map<String, Any>
}

class MockBluetoothHealthDeviceProvider : DeviceProvider {
    override val providerName: String = "Simulated BLE Health Device Gateway"
    override val isMock: Boolean = true

    override suspend fun scanForNearbyHealthDevices(): List<ScannedDevice> {
        delay(800)
        return listOf(
            ScannedDevice("dev_ble_01", "Withings ScanWatch 2", "SMARTWATCH", -62),
            ScannedDevice("dev_ble_02", "Omron Complete Wireless ECG+BP", "BP_MONITOR", -55),
            ScannedDevice("dev_ble_03", "Accu-Chek Guide Me BLE", "GLUCOMETER", -78),
            ScannedDevice("dev_ble_04", "Nonin Onyx II Fingertip Oximeter", "PULSE_OXIMETER", -82)
        )
    }

    override suspend fun syncTelemetry(deviceId: String): Map<String, Any> {
        delay(600)
        return mapOf(
            "heartRate" to (68..84).random(),
            "bloodPressureSys" to (115..125).random(),
            "bloodPressureDia" to (75..82).random(),
            "spo2" to (97..99).random(),
            "syncTime" to System.currentTimeMillis()
        )
    }
}

class DeviceService(private val provider: DeviceProvider = MockBluetoothHealthDeviceProvider()) {
    val isPrototype: Boolean = provider.isMock
    suspend fun scan() = provider.scanForNearbyHealthDevices()
    suspend fun sync(deviceId: String) = provider.syncTelemetry(deviceId)
}
