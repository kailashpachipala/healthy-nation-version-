package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        VitalEntity::class,
        DoctorEntity::class,
        AppointmentEntity::class,
        MedicationEntity::class,
        LabReportEntity::class,
        MedicineEntity::class,
        PharmacyOrderEntity::class,
        NotificationEntity::class,
        EmergencyContactEntity::class,
        InsurancePolicyEntity::class,
        ConnectedDeviceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HealthyNationDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vitalDao(): VitalDao
    abstract fun doctorDao(): DoctorDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun medicationDao(): MedicationDao
    abstract fun labReportDao(): LabReportDao
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun orderDao(): OrderDao
    abstract fun notificationDao(): NotificationDao
    abstract fun emergencyDao(): EmergencyDao
    abstract fun insuranceDao(): InsuranceDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: HealthyNationDatabase? = null

        fun getInstance(context: Context): HealthyNationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthyNationDatabase::class.java,
                    "healthy_nation.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed initial data on first creation
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).seedInitialData()
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        // User Profile
        userDao().insertOrUpdate(UserEntity())

        // Initial Vitals
        val now = System.currentTimeMillis()
        val oneHour = 3600000L
        vitalDao().insertAll(
            listOf(
                VitalEntity(type = "HEART_RATE", primaryValue = 72.0, unit = "bpm", status = "NORMAL", timestamp = now),
                VitalEntity(type = "HEART_RATE", primaryValue = 76.0, unit = "bpm", status = "NORMAL", timestamp = now - oneHour * 4),
                VitalEntity(type = "HEART_RATE", primaryValue = 82.0, unit = "bpm", status = "NORMAL", timestamp = now - oneHour * 8),
                VitalEntity(type = "BLOOD_PRESSURE", primaryValue = 118.0, secondaryValue = 78.0, unit = "mmHg", status = "NORMAL", timestamp = now),
                VitalEntity(type = "BLOOD_PRESSURE", primaryValue = 122.0, secondaryValue = 80.0, unit = "mmHg", status = "NORMAL", timestamp = now - oneHour * 12),
                VitalEntity(type = "SPO2", primaryValue = 99.0, unit = "%", status = "NORMAL", timestamp = now),
                VitalEntity(type = "SPO2", primaryValue = 98.0, unit = "%", status = "NORMAL", timestamp = now - oneHour * 6),
                VitalEntity(type = "TEMPERATURE", primaryValue = 98.6, unit = "°F", status = "NORMAL", timestamp = now),
                VitalEntity(type = "STEPS", primaryValue = 8420.0, unit = "steps", status = "NORMAL", timestamp = now)
            )
        )

        // Doctors
        doctorDao().insertAll(
            listOf(
                DoctorEntity(
                    id = "doc_1",
                    name = "Dr. Ananya Sharma",
                    specialty = "Cardiologist",
                    qualification = "MD, DM (Cardiology), FACC",
                    experienceYears = 14,
                    rating = 4.9,
                    reviewCount = 312,
                    consultationFee = 65.0,
                    hospitalName = "Metro Heart & Vascular Institute",
                    nextAvailableSlot = "Today, 4:30 PM",
                    about = "Senior Consultant Cardiologist specializing in preventive cardiology, echocardiography, and hypertension management."
                ),
                DoctorEntity(
                    id = "doc_2",
                    name = "Dr. Marcus Vance",
                    specialty = "General Physician",
                    qualification = "MD (Internal Medicine)",
                    experienceYears = 11,
                    rating = 4.8,
                    reviewCount = 245,
                    consultationFee = 45.0,
                    hospitalName = "St. Jude Wellness Center",
                    nextAvailableSlot = "Tomorrow, 10:00 AM",
                    about = "Primary care specialist with expertise in metabolic health, preventive screenings, and chronic illness management."
                ),
                DoctorEntity(
                    id = "doc_3",
                    name = "Dr. Priya Patel",
                    specialty = "Dermatologist",
                    qualification = "MD, DVD (Dermatology)",
                    experienceYears = 9,
                    rating = 4.9,
                    reviewCount = 189,
                    consultationFee = 50.0,
                    hospitalName = "Aesthetic Care Clinic",
                    nextAvailableSlot = "Tomorrow, 2:15 PM",
                    about = "Expert in allergic skin disorders, autoimmune dermatitis, and advanced medical dermatology treatments."
                ),
                DoctorEntity(
                    id = "doc_4",
                    name = "Dr. Robert Chen",
                    specialty = "Pulmonologist",
                    qualification = "MBBS, FCCP (USA)",
                    experienceYears = 16,
                    rating = 4.7,
                    reviewCount = 178,
                    consultationFee = 60.0,
                    hospitalName = "Apex Respiratory Clinic",
                    nextAvailableSlot = "Wed, 11:30 AM",
                    about = "Pioneering chest physician focused on adult asthma, post-viral respiratory rehab, and sleep apnea."
                )
            )
        )

        // Upcoming Appointment
        appointmentDao().insert(
            AppointmentEntity(
                doctorId = "doc_1",
                doctorName = "Dr. Ananya Sharma",
                specialty = "Cardiology Routine Checkup",
                appointmentDate = "Tomorrow, Oct 1",
                timeSlot = "04:30 PM",
                consultType = "VIDEO",
                status = "UPCOMING",
                notes = "Follow up on resting ECG and cholesterol profile",
                meetingUrl = "https://telehealth.healthynation.org/room/HN-4928"
            )
        )

        // Medications
        medicationDao().insertAll(
            listOf(
                MedicationEntity(
                    name = "Atorvastatin",
                    dosage = "10mg",
                    frequency = "Once daily",
                    instructions = "Take with water after dinner",
                    timeOfDay = "09:00 PM",
                    remainingCount = 18,
                    totalCount = 30,
                    isPrescription = true,
                    isTakenToday = false
                ),
                MedicationEntity(
                    name = "Montelukast",
                    dosage = "10mg",
                    frequency = "Once daily",
                    instructions = "Take at night for airway support",
                    timeOfDay = "10:00 PM",
                    remainingCount = 12,
                    totalCount = 30,
                    isPrescription = true,
                    isTakenToday = false
                ),
                MedicationEntity(
                    name = "Vitamin D3 + K2",
                    dosage = "2000 IU",
                    frequency = "Once daily",
                    instructions = "Take with morning meal",
                    timeOfDay = "08:30 AM",
                    remainingCount = 24,
                    totalCount = 60,
                    isPrescription = false,
                    isTakenToday = true
                )
            )
        )

        // Lab Reports
        labReportDao().insertAll(
            listOf(
                LabReportEntity(
                    testName = "Comprehensive Metabolic & Lipid Panel",
                    category = "Biochemistry",
                    labName = "NationQuest Diagnostic Labs",
                    testDate = "Sep 22, 2026",
                    status = "NORMAL",
                    summary = "All metabolic markers within normal parameters. Total Cholesterol: 184 mg/dL, HDL: 56 mg/dL, Fasting Glucose: 92 mg/dL.",
                    doctorName = "Dr. Ananya Sharma",
                    keyFindings = "Normal kidney function, liver enzymes optimal, lipid ratio in healthy range."
                ),
                LabReportEntity(
                    testName = "Complete Blood Count (CBC) with Differential",
                    category = "Hematology",
                    labName = "Apex PathLab Services",
                    testDate = "Aug 15, 2026",
                    status = "NORMAL",
                    summary = "Hemoglobin 14.8 g/dL, Platelets 260,000/uL, WBC 6,400/uL. Normal cellular morphology.",
                    doctorName = "Dr. Marcus Vance",
                    keyFindings = "All red and white cell lines within reference standard."
                ),
                LabReportEntity(
                    testName = "High-Sensitivity C-Reactive Protein (hs-CRP)",
                    category = "Cardiac Markers",
                    labName = "Metro Heart Diagnostic Center",
                    testDate = "Jun 10, 2026",
                    status = "ATTENTION",
                    summary = "hs-CRP level measured at 2.1 mg/L (slightly elevated systemic inflammation baseline).",
                    doctorName = "Dr. Ananya Sharma",
                    keyFindings = "Borderline moderate cardiovascular inflammation risk. Advised lifestyle and dietary modification."
                )
            )
        )

        // Pharmacy Medicines Catalog
        pharmacyDao().insertAll(
            listOf(
                MedicineEntity(
                    id = "med_1",
                    name = "Atorvastatin Calcium",
                    brand = "Lipistat 10",
                    category = "Chronic Care",
                    price = 14.50,
                    dosageForm = "Tablet",
                    strength = "10 mg",
                    description = "Used alongside diet and exercise to lower LDL cholesterol and triglycerides in blood.",
                    requiresPrescription = true
                ),
                MedicineEntity(
                    id = "med_2",
                    name = "Paracetamol Rapid Relief",
                    brand = "Panacare 500",
                    category = "Pain Relief",
                    price = 6.20,
                    dosageForm = "Tablet",
                    strength = "500 mg",
                    description = "Fast-acting analgesic and antipyretic for fever, headache, and muscular body ache.",
                    requiresPrescription = false
                ),
                MedicineEntity(
                    id = "med_3",
                    name = "Coenzyme Q10 + Omega-3",
                    brand = "CardioVital CoQ10",
                    category = "Vitamins & Supplements",
                    price = 28.00,
                    dosageForm = "Softgel",
                    strength = "100 mg",
                    description = "Cardiovascular and cellular cellular energy support antioxidant supplement.",
                    requiresPrescription = false
                ),
                MedicineEntity(
                    id = "med_4",
                    name = "Amoxicillin Trihydrate",
                    brand = "Moxikind 500",
                    category = "Antibiotics",
                    price = 18.90,
                    dosageForm = "Capsule",
                    strength = "500 mg",
                    description = "Broad-spectrum penicillin antibiotic for bacterial respiratory and ENT infections.",
                    requiresPrescription = true
                ),
                MedicineEntity(
                    id = "med_5",
                    name = "Vitamin C + Zinc Chewables",
                    brand = "ImmunoShield 1000",
                    category = "Vitamins & Supplements",
                    price = 9.99,
                    dosageForm = "Chewable",
                    strength = "1000 mg",
                    description = "High-potency immune resilience formula with citrus bioflavonoids.",
                    requiresPrescription = false
                )
            )
        )

        // Orders
        orderDao().insertOrder(
            PharmacyOrderEntity(
                id = "ord_101",
                orderNumber = "HN-ORD-92811",
                itemsSummary = "Atorvastatin 10mg (1x), Vitamin D3 (1x)",
                itemCount = 2,
                totalAmount = 24.49,
                paymentMethod = "Apple Pay / Card",
                orderStatus = "OUT_FOR_DELIVERY",
                deliveryAddress = "742 Evergreen Terrace, Springfield",
                estimatedDelivery = "Today, by 6:00 PM"
            )
        )

        // Notifications
        notificationDao().insertAll(
            listOf(
                NotificationEntity(
                    title = "Upcoming Consultation",
                    message = "Reminder: Your video consultation with Dr. Ananya Sharma starts tomorrow at 4:30 PM.",
                    category = "APPOINTMENT",
                    timestamp = now - 1800000L,
                    isRead = false
                ),
                NotificationEntity(
                    title = "Medicine Delivery On the Way",
                    message = "Courier has picked up order #HN-ORD-92811. Estimated delivery is today by 6:00 PM.",
                    category = "ORDER",
                    timestamp = now - 3600000L * 2,
                    isRead = false
                ),
                NotificationEntity(
                    title = "Evening Dose Reminder",
                    message = "Time to take Atorvastatin 10mg after your dinner.",
                    category = "MEDICATION",
                    timestamp = now - 3600000L * 14,
                    isRead = true
                )
            )
        )

        // Emergency Contacts
        emergencyDao().insertAll(
            listOf(
                EmergencyContactEntity(name = "Sarah Morgan", relationship = "Spouse", phoneNumber = "+1 (555) 902-4411", isPrimary = true),
                EmergencyContactEntity(name = "David Morgan", relationship = "Brother", phoneNumber = "+1 (555) 819-2044", isPrimary = false)
            )
        )

        // Insurance Policy
        insuranceDao().insert(
            InsurancePolicyEntity(
                policyNumber = "HN-CARE-882914-X",
                providerName = "Star Health & CareShield",
                planName = "Family Health Platinum Comprehensive",
                coverageAmount = 100000.0,
                usedAmount = 14200.0,
                validTill = "Dec 31, 2027",
                status = "ACTIVE",
                networkHospitalsCount = 4850
            )
        )

        // Devices
        deviceDao().insertAll(
            listOf(
                ConnectedDeviceEntity(id = "dev_1", name = "Apple Watch Series 9", type = "SMARTWATCH", isConnected = true, batteryPercent = 84, lastSyncText = "Synced 3m ago"),
                ConnectedDeviceEntity(id = "dev_2", name = "Omron Evolv Wireless BP", type = "BP_MONITOR", isConnected = true, batteryPercent = 92, lastSyncText = "Synced today 8:00 AM"),
                ConnectedDeviceEntity(id = "dev_3", name = "Accu-Chek Instant Glucometer", type = "GLUCOMETER", isConnected = false, batteryPercent = 45, lastSyncText = "Disconnected")
            )
        )
    }
}
