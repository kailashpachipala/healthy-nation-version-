package com.example

import com.example.services.payment.MockPaymentProvider
import com.example.services.payment.PaymentMethodType
import com.example.services.payment.PaymentRequest
import com.example.services.payment.PaymentStatus
import com.example.services.triage.RuleBasedTriageProvider
import com.example.services.triage.TriageLevel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class HealthyNationUnitTest {

    @Test
    fun testRuleBasedTriageEmergencyDetection() = runBlocking {
        val provider = RuleBasedTriageProvider()
        val result = provider.evaluate(
            symptoms = listOf("Chest Pain"),
            freeTextDescription = "Severe tightness radiating to my left arm"
        )
        assertEquals(TriageLevel.EMERGENCY_RED, result.level)
        assertEquals(10, result.urgencyScore)
        assertTrue(result.recommendedActions.isNotEmpty())
    }

    @Test
    fun testRuleBasedTriageRoutineDetection() = runBlocking {
        val provider = RuleBasedTriageProvider()
        val result = provider.evaluate(
            symptoms = listOf("Skin Rash"),
            freeTextDescription = "Mild itchy red spots on forearm"
        )
        assertEquals(TriageLevel.ROUTINE_YELLOW, result.level)
        assertTrue(result.urgencyScore <= 5)
    }

    @Test
    fun testMockPaymentProviderProcessesSuccessfully() = runBlocking {
        val provider = MockPaymentProvider()
        val request = PaymentRequest(
            amount = 24.50,
            currency = "USD",
            description = "Test Pharmacy Order",
            method = PaymentMethodType.UPI
        )
        val result = provider.processPayment(request)
        assertTrue(result.isSuccess)
        val txn = result.getOrNull()
        assertNotNull(txn)
        assertEquals(PaymentStatus.SUCCESS, txn?.status)
        assertEquals(24.50, txn?.amount ?: 0.0, 0.001)
    }
}
