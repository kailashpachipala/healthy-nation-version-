package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class HealthyNationRepository(private val db: HealthyNationDatabase) {
    val user: Flow<UserEntity?> = db.userDao().getUser()
    val vitals: Flow<List<VitalEntity>> = db.vitalDao().getAllVitals()
    val doctors: Flow<List<DoctorEntity>> = db.doctorDao().getAllDoctors()
    val appointments: Flow<List<AppointmentEntity>> = db.appointmentDao().getAllAppointments()
    val upcomingAppointments: Flow<List<AppointmentEntity>> = db.appointmentDao().getUpcomingAppointments()
    val medications: Flow<List<MedicationEntity>> = db.medicationDao().getAllMedications()
    val labReports: Flow<List<LabReportEntity>> = db.labReportDao().getAllReports()
    val medicines: Flow<List<MedicineEntity>> = db.pharmacyDao().getAllMedicines()
    val orders: Flow<List<PharmacyOrderEntity>> = db.orderDao().getAllOrders()
    val notifications: Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = db.notificationDao().getUnreadCount()
    val emergencyContacts: Flow<List<EmergencyContactEntity>> = db.emergencyDao().getContacts()
    val insurancePolicy: Flow<InsurancePolicyEntity?> = db.insuranceDao().getPolicy()
    val connectedDevices: Flow<List<ConnectedDeviceEntity>> = db.deviceDao().getDevices()

    fun getOrder(orderId: String): Flow<PharmacyOrderEntity?> = db.orderDao().getOrderById(orderId)

    suspend fun updateUser(user: UserEntity) = db.userDao().insertOrUpdate(user)

    suspend fun addVital(vital: VitalEntity) = db.vitalDao().insertVital(vital)

    suspend fun bookAppointment(appointment: AppointmentEntity) = db.appointmentDao().insert(appointment)

    suspend fun cancelAppointment(id: Long) = db.appointmentDao().updateStatus(id, "CANCELLED")

    suspend fun toggleMedicationTaken(id: Long, taken: Boolean) = db.medicationDao().setTakenToday(id, taken)

    suspend fun createOrder(order: PharmacyOrderEntity) = db.orderDao().insertOrder(order)

    suspend fun updateOrderStatus(orderId: String, status: String) = db.orderDao().updateOrderStatus(orderId, status)

    suspend fun markNotificationRead(id: Long) = db.notificationDao().markAsRead(id)

    suspend fun markAllNotificationsRead() = db.notificationDao().markAllAsRead()

    suspend fun addEmergencyContact(contact: EmergencyContactEntity) = db.emergencyDao().insertContact(contact)

    suspend fun deleteEmergencyContact(contact: EmergencyContactEntity) = db.emergencyDao().deleteContact(contact)

    suspend fun toggleDeviceConnection(id: String, connected: Boolean) = db.deviceDao().setConnectionState(id, connected)

    suspend fun seedIfEmpty() {
        db.seedInitialData()
    }
}
