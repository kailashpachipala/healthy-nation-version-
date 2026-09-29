package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Morgan",
    val email: String = "alex.morgan@healthynation.org",
    val phone: String = "+1 (555) 349-2810",
    val age: Int = 32,
    val gender: String = "Male",
    val bloodGroup: String = "O+",
    val allergies: String = "Penicillin, Peanuts",
    val chronicConditions: String = "Mild Asthma",
    val organDonor: Boolean = true,
    val emergencyContactName: String = "Sarah Morgan",
    val emergencyContactPhone: String = "+1 (555) 902-4411",
    val emergencyContactRelation: String = "Spouse",
    val wellnessScore: Int = 88,
    val heightCm: Double = 178.0,
    val weightKg: Double = 72.5
)

@Entity(tableName = "vitals")
data class VitalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // HEART_RATE, BLOOD_PRESSURE, SPO2, TEMPERATURE, STEPS
    val primaryValue: Double,
    val secondaryValue: Double? = null, // e.g. diastolic BP
    val unit: String,
    val status: String, // NORMAL, ATTENTION, CRITICAL
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "doctors")
data class DoctorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val specialty: String,
    val qualification: String,
    val experienceYears: Int,
    val rating: Double,
    val reviewCount: Int,
    val consultationFee: Double,
    val hospitalName: String,
    val nextAvailableSlot: String,
    val about: String
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val doctorId: String,
    val doctorName: String,
    val specialty: String,
    val appointmentDate: String,
    val timeSlot: String,
    val consultType: String, // VIDEO, IN_PERSON
    val status: String = "UPCOMING", // UPCOMING, COMPLETED, CANCELLED
    val notes: String = "",
    val meetingUrl: String? = null
)

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val frequency: String,
    val instructions: String, // e.g., "After breakfast"
    val timeOfDay: String, // e.g., "08:00 AM"
    val remainingCount: Int,
    val totalCount: Int,
    val isPrescription: Boolean = true,
    val isTakenToday: Boolean = false
)

@Entity(tableName = "lab_reports")
data class LabReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testName: String,
    val category: String,
    val labName: String,
    val testDate: String,
    val status: String, // NORMAL, ATTENTION, PENDING
    val summary: String,
    val doctorName: String,
    val keyFindings: String
)

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String,
    val category: String, // Pain Relief, Vitamins, Chronic Care, Antibiotics
    val price: Double,
    val dosageForm: String, // Tablet, Capsule, Syrup, Cream
    val strength: String,
    val description: String,
    val requiresPrescription: Boolean,
    val inStock: Boolean = true
)

@Entity(tableName = "pharmacy_orders")
data class PharmacyOrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val itemsSummary: String,
    val itemCount: Int,
    val totalAmount: Double,
    val paymentMethod: String,
    val orderStatus: String, // PLACED, CONFIRMED, PACKED, OUT_FOR_DELIVERY, DELIVERED
    val deliveryAddress: String,
    val estimatedDelivery: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String, // APPOINTMENT, MEDICATION, VITAL, ORDER, SOS
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val isPrimary: Boolean = false
)

@Entity(tableName = "insurance_policies")
data class InsurancePolicyEntity(
    @PrimaryKey val policyNumber: String,
    val providerName: String,
    val planName: String,
    val coverageAmount: Double,
    val usedAmount: Double,
    val validTill: String,
    val status: String, // ACTIVE, EXPIRING_SOON, LAPSED
    val networkHospitalsCount: Int
)

@Entity(tableName = "connected_devices")
data class ConnectedDeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // SMARTWATCH, BP_MONITOR, GLUCOMETER, PULSE_OXIMETER
    val isConnected: Boolean,
    val batteryPercent: Int,
    val lastSyncText: String
)
