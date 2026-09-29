package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.HealthyNationRepository
import com.example.services.device.DeviceService
import com.example.services.device.ScannedDevice
import com.example.services.emergency.EmergencyService
import com.example.services.gemini.GeminiClient
import com.example.services.payment.*
import com.example.services.triage.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItem(
    val medicine: MedicineEntity,
    val quantity: Int
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UiNotification(
    val message: String,
    val isError: Boolean = false
)

class HealthyNationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HealthyNationDatabase.getInstance(application)
    val repository = HealthyNationRepository(db)

    val triageService = TriageService()
    val paymentService = PaymentService()
    val deviceService = DeviceService()
    val emergencyService = EmergencyService()

    // Auth State
    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(true)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    // Global Snackbar/Toast notice
    private val _uiNotice = MutableSharedFlow<UiNotification>()
    val uiNotice: SharedFlow<UiNotification> = _uiNotice.asSharedFlow()

    // Repository Flows
    val user: StateFlow<UserEntity?> = repository.user.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val vitals: StateFlow<List<VitalEntity>> = repository.vitals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val doctors: StateFlow<List<DoctorEntity>> = repository.doctors.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val appointments: StateFlow<List<AppointmentEntity>> = repository.appointments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val medications: StateFlow<List<MedicationEntity>> = repository.medications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val labReports: StateFlow<List<LabReportEntity>> = repository.labReports.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val medicines: StateFlow<List<MedicineEntity>> = repository.medicines.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val orders: StateFlow<List<PharmacyOrderEntity>> = repository.orders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unreadCount: StateFlow<Int> = repository.unreadNotificationsCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val emergencyContacts: StateFlow<List<EmergencyContactEntity>> = repository.emergencyContacts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val insurancePolicy: StateFlow<InsurancePolicyEntity?> = repository.insurancePolicy.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val connectedDevices: StateFlow<List<ConnectedDeviceEntity>> = repository.connectedDevices.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    val cartTotal: StateFlow<Double> = _cart.map { items ->
        items.sumOf { it.medicine.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Triage State
    private val _selectedSymptoms = MutableStateFlow<Set<String>>(emptySet())
    val selectedSymptoms: StateFlow<Set<String>> = _selectedSymptoms.asStateFlow()

    private val _triageResult = MutableStateFlow<TriageResult?>(null)
    val triageResult: StateFlow<TriageResult?> = _triageResult.asStateFlow()

    private val _isTriageEvaluating = MutableStateFlow(false)
    val isTriageEvaluating: StateFlow<Boolean> = _isTriageEvaluating.asStateFlow()

    // AI Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "assistant",
                text = "Hello! I am your Healthy Nation Health Assistant. You can ask me about symptoms, medications, lab tests, or wellness habits. How can I assist you today?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatbotResponding = MutableStateFlow(false)
    val isChatbotResponding: StateFlow<Boolean> = _isChatbotResponding.asStateFlow()

    // Bluetooth Scanning State
    private val _scannedDevices = MutableStateFlow<List<ScannedDevice>>(emptyList())
    val scannedDevices: StateFlow<List<ScannedDevice>> = _scannedDevices.asStateFlow()

    private val _isScanningDevices = MutableStateFlow(false)
    val isScanningDevices: StateFlow<Boolean> = _isScanningDevices.asStateFlow()

    // Transaction History
    private val _transactions = MutableStateFlow<List<PaymentTransaction>>(
        listOf(
            PaymentTransaction(
                transactionId = "TXN-892401",
                orderId = "HN-ORD-92811",
                amount = 24.49,
                currency = "USD",
                method = PaymentMethodType.CREDIT_DEBIT_CARD,
                status = PaymentStatus.SUCCESS,
                timestamp = System.currentTimeMillis() - 7200000L,
                receiptNumber = "RCP-77402"
            )
        )
    )
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    // Auth actions
    fun signIn(email: String, pass: String) {
        _isAuthenticated.value = true
        _isOnboardingCompleted.value = true
        postNotice("Signed in as Alex Morgan")
    }

    fun signOut() {
        _isAuthenticated.value = false
        postNotice("Signed out")
    }

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
    }

    // Vitals Actions
    fun logVital(type: String, primary: Double, secondary: Double? = null, unit: String, status: String = "NORMAL") {
        viewModelScope.launch {
            val vital = VitalEntity(
                type = type,
                primaryValue = primary,
                secondaryValue = secondary,
                unit = unit,
                status = status
            )
            repository.addVital(vital)
            postNotice("Logged $type reading: $primary $unit")
        }
    }

    // Appointment Actions
    fun bookAppointment(
        doctor: DoctorEntity,
        date: String,
        timeSlot: String,
        type: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val appt = AppointmentEntity(
                doctorId = doctor.id,
                doctorName = doctor.name,
                specialty = doctor.specialty,
                appointmentDate = date,
                timeSlot = timeSlot,
                consultType = type,
                status = "UPCOMING",
                notes = notes,
                meetingUrl = if (type == "VIDEO") "https://telehealth.healthynation.org/room/${System.currentTimeMillis().toString().takeLast(6)}" else null
            )
            repository.bookAppointment(appt)
            postNotice("Consultation booked with ${doctor.name}")
        }
    }

    fun cancelAppointment(id: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
            postNotice("Appointment cancelled")
        }
    }

    // Medication Actions
    fun toggleMedicationTaken(id: Long, taken: Boolean) {
        viewModelScope.launch {
            repository.toggleMedicationTaken(id, taken)
        }
    }

    // Pharmacy Cart Actions
    fun addToCart(medicine: MedicineEntity) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.medicine.id == medicine.id }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(medicine = medicine, quantity = 1))
        }
        _cart.value = current
        postNotice("Added ${medicine.name} to cart")
    }

    fun removeFromCart(medicineId: String) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.medicine.id == medicineId }
        if (index >= 0) {
            if (current[index].quantity > 1) {
                current[index] = current[index].copy(quantity = current[index].quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cart.value = current
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Checkout & Payment
    fun processCheckout(paymentMethod: PaymentMethodType, deliveryAddress: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val amount = cartTotal.value
            val itemsDesc = _cart.value.joinToString(", ") { "${it.medicine.name} (${it.quantity}x)" }
            val itemCount = _cart.value.sumOf { it.quantity }
            val orderNum = "HN-ORD-" + (10000..99999).random()

            val paymentRequest = PaymentRequest(
                amount = amount,
                description = "Pharmacy Order $orderNum",
                method = paymentMethod,
                metadata = mapOf("orderId" to orderNum)
            )

            val paymentResult = paymentService.pay(paymentRequest)

            paymentResult.onSuccess { txn ->
                _transactions.value = listOf(txn) + _transactions.value
                val newOrder = PharmacyOrderEntity(
                    id = "ord_${System.currentTimeMillis()}",
                    orderNumber = orderNum,
                    itemsSummary = itemsDesc,
                    itemCount = itemCount,
                    totalAmount = amount,
                    paymentMethod = paymentMethod.displayName,
                    orderStatus = "CONFIRMED",
                    deliveryAddress = deliveryAddress,
                    estimatedDelivery = "Tomorrow by 2:00 PM"
                )
                repository.createOrder(newOrder)
                clearCart()
                postNotice("Order $orderNum placed successfully!")
                onComplete(newOrder.id)
            }.onFailure { err ->
                postNotice("Payment failed: ${err.message}", isError = true)
            }
        }
    }

    // Triage Evaluation
    fun toggleSymptom(symptom: String) {
        val set = _selectedSymptoms.value.toMutableSet()
        if (set.contains(symptom)) set.remove(symptom) else set.add(symptom)
        _selectedSymptoms.value = set
    }

    fun evaluateTriage(freeText: String, enableThinking: Boolean = false, enableSearch: Boolean = false) {
        viewModelScope.launch {
            _isTriageEvaluating.value = true
            val result = triageService.evaluateSymptoms(
                symptoms = _selectedSymptoms.value.toList(),
                freeText = freeText,
                deepThinking = enableThinking,
                searchGrounding = enableSearch
            )
            _triageResult.value = result
            _isTriageEvaluating.value = false
        }
    }

    fun resetTriage() {
        _selectedSymptoms.value = emptySet()
        _triageResult.value = null
    }

    // AI Chatbot
    fun sendChatMessage(userText: String, useThinking: Boolean = false) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(sender = "user", text = userText)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isChatbotResponding.value = true
            val prompt = buildChatPrompt(userText)
            val result = GeminiClient.generateContent(
                prompt = prompt,
                systemInstruction = "You are Healthy Nation's empathetic, certified health chatbot. Provide medically sound, accessible guidance on symptoms, diet, vitals, and wellness. Remind users you are an educational prototype assistant and not a medical doctor.",
                model = if (useThinking) "gemini-3.1-pro-preview" else "gemini-3.5-flash",
                enableHighThinking = useThinking
            )

            result.fold(
                onSuccess = { reply ->
                    _chatMessages.value = _chatMessages.value + ChatMessage(sender = "assistant", text = reply)
                },
                onFailure = {
                    // Fallback response for prototype offline mode
                    val fallback = getLocalChatbotFallback(userText)
                    _chatMessages.value = _chatMessages.value + ChatMessage(sender = "assistant", text = fallback)
                }
            )
            _isChatbotResponding.value = false
        }
    }

    private fun buildChatPrompt(latestText: String): String {
        val history = _chatMessages.value.takeLast(6).joinToString("\n") {
            "${it.sender.uppercase()}: ${it.text}"
        }
        return "Recent Conversation History:\n$history\n\nUSER QUESTION: $latestText"
    }

    private fun getLocalChatbotFallback(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("fever") -> "For mild fever, rest, hydration, and cool compresses are recommended. If temperature exceeds 102°F (38.9°C) or lasts more than 3 days, please schedule an appointment with a primary care physician."
            q.contains("headache") -> "Tension headaches often respond to hydration, dim lighting, and stress management. Seek immediate care if the headache is sudden, unusually severe, or accompanied by vision changes."
            q.contains("blood pressure") || q.contains("bp") -> "Standard optimal blood pressure is generally below 120/80 mmHg. Ensure you are seated quietly for 5 minutes prior to measuring."
            q.contains("diet") || q.contains("nutrition") -> "A balanced Mediterranean-style diet rich in leafy greens, berries, lean proteins, and unsaturated fats supports cardiovascular and metabolic longevity."
            else -> "Thank you for asking. Based on healthy living standards, maintaining consistent sleep, physical activity, and hydration are essential pillars. You can also explore our AI Symptom Triage tool or consult one of our verified specialists in the Care tab."
        }
    }

    // Devices BLE Scan & Sync
    fun startDeviceScan() {
        viewModelScope.launch {
            _isScanningDevices.value = true
            _scannedDevices.value = deviceService.scan()
            _isScanningDevices.value = false
            postNotice("Found ${_scannedDevices.value.size} nearby health sensors")
        }
    }

    fun syncDeviceTelemetry(device: ConnectedDeviceEntity) {
        viewModelScope.launch {
            postNotice("Syncing with ${device.name}...")
            val telemetry = deviceService.sync(device.id)
            val hr = telemetry["heartRate"] as? Int ?: 74
            logVital("HEART_RATE", hr.toDouble(), unit = "bpm", status = "NORMAL")
            postNotice("Received updated metrics from ${device.name}")
        }
    }

    // Notification Actions
    fun markNotificationRead(id: Long) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch { repository.markAllNotificationsRead() }
    }

    // Profile Updates
    fun updateProfile(name: String, phone: String, bloodGroup: String, allergies: String) {
        viewModelScope.launch {
            val current = user.value ?: UserEntity()
            val updated = current.copy(
                name = name,
                phone = phone,
                bloodGroup = bloodGroup,
                allergies = allergies
            )
            repository.updateUser(updated)
            postNotice("Medical profile updated")
        }
    }

    private fun postNotice(msg: String, isError: Boolean = false) {
        viewModelScope.launch {
            _uiNotice.emit(UiNotification(msg, isError))
        }
    }
}
