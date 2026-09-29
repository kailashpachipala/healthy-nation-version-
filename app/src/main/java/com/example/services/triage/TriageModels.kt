package com.example.services.triage

enum class TriageLevel(val label: String, val severityColorHex: Long) {
    EMERGENCY_RED("Critical / Emergency", 0xFFEF4444),
    URGENT_ORANGE("Urgent / Same-Day Care", 0xFFF97316),
    ROUTINE_YELLOW("Routine Doctor Visit", 0xFFEAB308),
    SELF_CARE_GREEN("Self-Care & Monitoring", 0xFF10B981)
}

data class TriageResult(
    val level: TriageLevel,
    val title: String,
    val urgencyScore: Int, // 1 to 10
    val summary: String,
    val recommendedActions: List<String>,
    val suggestedSpecialty: String,
    val redFlagWarnings: List<String>,
    val isAiGenerated: Boolean = false,
    val disclaimer: String = "Prototype Clinical Triage Tool. Not a substitute for formal professional medical diagnosis or hospital emergency service."
)

data class SymptomTag(
    val id: String,
    val name: String,
    val category: String
)

interface TriageProvider {
    suspend fun evaluate(
        symptoms: List<String>,
        freeTextDescription: String,
        useDeepThinking: Boolean = false,
        useSearchGrounding: Boolean = false
    ): TriageResult
}
