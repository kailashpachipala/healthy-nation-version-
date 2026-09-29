package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)
}

@Dao
interface VitalDao {
    @Query("SELECT * FROM vitals ORDER BY timestamp DESC")
    fun getAllVitals(): Flow<List<VitalEntity>>

    @Query("SELECT * FROM vitals WHERE type = :type ORDER BY timestamp DESC")
    fun getVitalsByType(type: String): Flow<List<VitalEntity>>

    @Query("SELECT * FROM vitals WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    fun getLatestVitalByType(type: String): Flow<VitalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVital(vital: VitalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vitals: List<VitalEntity>)
}

@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors WHERE specialty = :specialty")
    fun getDoctorsBySpecialty(specialty: String): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors WHERE id = :id LIMIT 1")
    suspend fun getDoctorById(id: String): DoctorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(doctors: List<DoctorEntity>)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE status = 'UPCOMING' ORDER BY id ASC")
    fun getUpcomingAppointments(): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: AppointmentEntity)

    @Update
    suspend fun update(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String)

    @Delete
    suspend fun delete(appointment: AppointmentEntity)
}

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications ORDER BY id ASC")
    fun getAllMedications(): Flow<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medication: MedicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medications: List<MedicationEntity>)

    @Update
    suspend fun update(medication: MedicationEntity)

    @Query("UPDATE medications SET isTakenToday = :taken WHERE id = :id")
    suspend fun setTakenToday(id: Long, taken: Boolean)
}

@Dao
interface LabReportDao {
    @Query("SELECT * FROM lab_reports ORDER BY id DESC")
    fun getAllReports(): Flow<List<LabReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<LabReportEntity>)
}

@Dao
interface PharmacyDao {
    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE category = :category")
    fun getMedicinesByCategory(category: String): Flow<List<MedicineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medicines: List<MedicineEntity>)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM pharmacy_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<PharmacyOrderEntity>>

    @Query("SELECT * FROM pharmacy_orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<PharmacyOrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: PharmacyOrderEntity)

    @Query("UPDATE pharmacy_orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface EmergencyDao {
    @Query("SELECT * FROM emergency_contacts")
    fun getContacts(): Flow<List<EmergencyContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<EmergencyContactEntity>)

    @Delete
    suspend fun deleteContact(contact: EmergencyContactEntity)
}

@Dao
interface InsuranceDao {
    @Query("SELECT * FROM insurance_policies LIMIT 1")
    fun getPolicy(): Flow<InsurancePolicyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(policy: InsurancePolicyEntity)
}

@Dao
interface DeviceDao {
    @Query("SELECT * FROM connected_devices")
    fun getDevices(): Flow<List<ConnectedDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<ConnectedDeviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: ConnectedDeviceEntity)

    @Query("UPDATE connected_devices SET isConnected = :connected WHERE id = :id")
    suspend fun setConnectionState(id: String, connected: Boolean)
}
