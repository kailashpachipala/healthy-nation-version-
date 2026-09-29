package com.example.services.payment

import kotlinx.coroutines.delay
import java.util.UUID

enum class PaymentMethodType(val displayName: String, val iconName: String) {
    UPI("Instant UPI", "upi"),
    CREDIT_DEBIT_CARD("Credit / Debit Card", "credit_card"),
    HEALTH_WALLET("Healthy Nation Wallet", "account_balance_wallet"),
    INSURANCE_CASHLESS("Direct Insurance Cashless", "verified_user"),
    CASH_ON_DELIVERY("Pay on Delivery", "local_shipping")
}

data class PaymentRequest(
    val amount: Double,
    val currency: String = "USD",
    val description: String,
    val method: PaymentMethodType,
    val metadata: Map<String, String> = emptyMap()
)

data class PaymentTransaction(
    val transactionId: String,
    val orderId: String,
    val amount: Double,
    val currency: String,
    val method: PaymentMethodType,
    val status: PaymentStatus,
    val timestamp: Long,
    val receiptNumber: String,
    val failureReason: String? = null
)

enum class PaymentStatus {
    SUCCESS,
    PENDING,
    FAILED
}

interface PaymentProvider {
    val providerName: String
    val isPrototype: Boolean
    suspend fun processPayment(request: PaymentRequest): Result<PaymentTransaction>
}

class MockPaymentProvider : PaymentProvider {
    override val providerName: String = "Healthy Nation Mock Payment Gateway"
    override val isPrototype: Boolean = true

    override suspend fun processPayment(request: PaymentRequest): Result<PaymentTransaction> {
        // Simulate realistic network gateway latency
        delay(900)

        val txId = "TXN-" + UUID.randomUUID().toString().take(8).uppercase()
        val receiptId = "HN-RCP-" + System.currentTimeMillis().toString().takeLast(6)

        return Result.success(
            PaymentTransaction(
                transactionId = txId,
                orderId = request.metadata["orderId"] ?: "ORD-${System.currentTimeMillis().toString().takeLast(5)}",
                amount = request.amount,
                currency = request.currency,
                method = request.method,
                status = PaymentStatus.SUCCESS,
                timestamp = System.currentTimeMillis(),
                receiptNumber = receiptId
            )
        )
    }
}

class PaymentService(private val provider: PaymentProvider = MockPaymentProvider()) {
    val activeProviderName: String = provider.providerName
    val isPrototype: Boolean = provider.isPrototype

    suspend fun pay(request: PaymentRequest): Result<PaymentTransaction> {
        return provider.processPayment(request)
    }
}
