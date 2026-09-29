package com.example.services.triage

import com.example.services.gemini.GeminiClient

class RuleBasedTriageProvider : TriageProvider {
    override suspend fun evaluate(
        symptoms: List<String>,
        freeTextDescription: String,
        useDeepThinking: Boolean,
        useSearchGrounding: Boolean
    ): TriageResult {
        val lowerText = (symptoms.joinToString(" ") + " " + freeTextDescription).lowercase()

        // Emergency Red Flags
        val emergencyKeywords = listOf(
            "chest pain", "shortness of breath", "loss of consciousness", "stroke",
            "paralysis", "coughing blood", "severe head injury", "unresponsive", "anaphylaxis"
        )
        if (emergencyKeywords.any { lowerText.contains(it) }) {
            return TriageResult(
                level = TriageLevel.EMERGENCY_RED,
                title = "Emergency Medical Attention Required",
                urgencyScore = 10,
                summary = "The described symptoms contain critical clinical red flags (e.g., cardiopulmonary distress or neurological compromise). Immediate emergency evaluation is vital.",
                recommendedActions = listOf(
                    "Call emergency services (911 / 112) or proceed to the nearest emergency room immediately.",
                    "Do NOT drive yourself; have an ambulance or someone else transport you.",
                    "Keep patient seated comfortably and loosen restrictive clothing."
                ),
                suggestedSpecialty = "Emergency Medicine / Cardiology",
                redFlagWarnings = listOf("Chest tightness radiating to arm or jaw", "Sudden breathlessness", "Acute numbness"),
                isAiGenerated = false
            )
        }

        // Urgent Same-Day
        val urgentKeywords = listOf("high fever", "severe migraine", "stomach pain", "persistent vomiting", "asthma", "wheezing", "deep cut")
        if (urgentKeywords.any { lowerText.contains(it) } || symptoms.size >= 4) {
            return TriageResult(
                level = TriageLevel.URGENT_ORANGE,
                title = "Urgent Same-Day Medical Evaluation",
                urgencyScore = 7,
                summary = "Symptoms suggest acute discomfort or an infectious/inflammatory response that warrants same-day medical review to prevent escalation.",
                recommendedActions = listOf(
                    "Book a same-day teleconsultation or visit an urgent care center within 6-12 hours.",
                    "Stay well hydrated with oral rehydration solutions.",
                    "Monitor core temperature and pulse rate every 2 hours."
                ),
                suggestedSpecialty = "General Physician / Internal Medicine",
                redFlagWarnings = listOf("Fever exceeding 103°F (39.4°C)", "Inability to keep liquids down for 12 hours"),
                isAiGenerated = false
            )
        }

        // Routine Consult
        val routineKeywords = listOf("skin rash", "joint ache", "fatigue", "back pain", "mild allergy", "cough", "sore throat")
        if (routineKeywords.any { lowerText.contains(it) }) {
            return TriageResult(
                level = TriageLevel.ROUTINE_YELLOW,
                title = "Routine Consultation Recommended",
                urgencyScore = 4,
                summary = "Non-emergent subacute symptoms detected. A standard outpatient consultation will help diagnose root cause and provide targeted therapy.",
                recommendedActions = listOf(
                    "Schedule a consultation with a specialist or primary care physician this week.",
                    "Log symptom progression, triggers, and any over-the-counter medications taken.",
                    "Rest and avoid strenuous physical exertion."
                ),
                suggestedSpecialty = "Primary Care Physician",
                redFlagWarnings = listOf("Escalation of localized pain", "Onset of high fever or disorientation"),
                isAiGenerated = false
            )
        }

        // Default Self-Care
        return TriageResult(
            level = TriageLevel.SELF_CARE_GREEN,
            title = "Mild Symptoms - Home Care & Observation",
            urgencyScore = 2,
            summary = "Symptoms appear mild and characteristic of benign, self-limiting discomfort. Focus on rest, supportive nutrition, and active symptom logging.",
            recommendedActions = listOf(
                "Prioritize 8 hours of restorative sleep and warm fluid intake.",
                "Track vitals (heart rate and temperature) in the Healthy Nation Vitals tracker.",
                "If symptoms persist beyond 48 hours or worsen, consult a doctor."
            ),
            suggestedSpecialty = "Wellness & Lifestyle Guidance",
            redFlagWarnings = listOf("Worsening pain or fever spike"),
            isAiGenerated = false
        )
    }
}

class GeminiTriageProvider(private val fallback: RuleBasedTriageProvider = RuleBasedTriageProvider()) : TriageProvider {
    override suspend fun evaluate(
        symptoms: List<String>,
        freeTextDescription: String,
        useDeepThinking: Boolean,
        useSearchGrounding: Boolean
    ): TriageResult {
        val prompt = """
            You are a clinical decision support assistant in the Healthy Nation healthcare mobile app.
            Analyze the following patient reported symptoms:
            Selected Symptoms: ${symptoms.joinToString(", ")}
            Description: $freeTextDescription

            Assess the urgency and provide a structured clinical assessment.
            Classify into one of four triage levels:
            - EMERGENCY_RED (Urgent immediate ER needed)
            - URGENT_ORANGE (Needs same-day doctor review)
            - ROUTINE_YELLOW (Can wait for regular outpatient appointment)
            - SELF_CARE_GREEN (Mild, self-limiting home care)

            Respond in plain English with these explicit section headers:
            LEVEL: [EMERGENCY_RED / URGENT_ORANGE / ROUTINE_YELLOW / SELF_CARE_GREEN]
            TITLE: [Concise clinical assessment title]
            URGENCY: [Number from 1 to 10]
            SPECIALTY: [Recommended doctor specialty]
            SUMMARY: [2-3 sentences explaining the clinical rationale]
            RECOMMENDATIONS:
            - [Step 1]
            - [Step 2]
            - [Step 3]
            RED_FLAGS:
            - [Red flag 1]
            - [Red flag 2]
        """.trimIndent()

        val systemInstruction = "You are Healthy Nation's triage intelligence. Adhere to conservative clinical triage safety guidelines. Always advise immediate emergency medical care if red flags like chest pain, severe dyspnea, or acute neurological deficits are present."

        val model = if (useDeepThinking) "gemini-3.1-pro-preview" else "gemini-3.5-flash"

        val geminiResult = GeminiClient.generateContent(
            prompt = prompt,
            systemInstruction = systemInstruction,
            model = model,
            enableHighThinking = useDeepThinking,
            useSearchGrounding = useSearchGrounding
        )

        return geminiResult.fold(
            onSuccess = { responseText ->
                parseGeminiTriageResponse(responseText)
            },
            onFailure = {
                // Graceful fallback to rule-based clinical engine
                fallback.evaluate(symptoms, freeTextDescription, useDeepThinking, useSearchGrounding)
            }
        )
    }

    private fun parseGeminiTriageResponse(raw: String): TriageResult {
        var level = TriageLevel.ROUTINE_YELLOW
        var title = "Clinical Health Evaluation"
        var urgency = 4
        var specialty = "General Physician"
        var summary = ""
        val recommendations = mutableListOf<String>()
        val redFlags = mutableListOf<String>()

        val lines = raw.lines()
        var currentSection = ""

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("LEVEL:", ignoreCase = true) -> {
                    val code = trimmed.substringAfter(":").trim().uppercase()
                    level = when {
                        code.contains("EMERGENCY") || code.contains("RED") -> TriageLevel.EMERGENCY_RED
                        code.contains("URGENT") || code.contains("ORANGE") -> TriageLevel.URGENT_ORANGE
                        code.contains("SELF") || code.contains("GREEN") -> TriageLevel.SELF_CARE_GREEN
                        else -> TriageLevel.ROUTINE_YELLOW
                    }
                }
                trimmed.startsWith("TITLE:", ignoreCase = true) -> {
                    title = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("URGENCY:", ignoreCase = true) -> {
                    urgency = trimmed.substringAfter(":").trim().filter { it.isDigit() }.toIntOrNull() ?: 5
                }
                trimmed.startsWith("SPECIALTY:", ignoreCase = true) -> {
                    specialty = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("SUMMARY:", ignoreCase = true) -> {
                    summary = trimmed.substringAfter(":").trim()
                    currentSection = "SUMMARY"
                }
                trimmed.startsWith("RECOMMENDATIONS:", ignoreCase = true) -> {
                    currentSection = "RECOMMENDATIONS"
                }
                trimmed.startsWith("RED_FLAGS:", ignoreCase = true) -> {
                    currentSection = "RED_FLAGS"
                }
                trimmed.startsWith("-") || trimmed.startsWith("*") -> {
                    val item = trimmed.removePrefix("-").removePrefix("*").trim()
                    if (item.isNotEmpty()) {
                        if (currentSection == "RECOMMENDATIONS") recommendations.add(item)
                        else if (currentSection == "RED_FLAGS") redFlags.add(item)
                    }
                }
                else -> {
                    if (currentSection == "SUMMARY" && trimmed.isNotEmpty() && summary.isEmpty()) {
                        summary = trimmed
                    }
                }
            }
        }

        if (summary.isEmpty()) summary = raw.take(250)
        if (recommendations.isEmpty()) {
            recommendations.add("Consult a licensed healthcare professional for precise diagnosis.")
            recommendations.add("Keep a real-time log of vitals in Healthy Nation.")
        }
        if (redFlags.isEmpty()) {
            redFlags.add("Sudden worsening of symptoms or onset of sharp chest pain.")
        }

        return TriageResult(
            level = level,
            title = title,
            urgencyScore = urgency.coerceIn(1, 10),
            summary = summary,
            recommendedActions = recommendations,
            suggestedSpecialty = specialty,
            redFlagWarnings = redFlags,
            isAiGenerated = true
        )
    }
}

class TriageService(
    private val primaryProvider: TriageProvider = GeminiTriageProvider(),
    private val fallbackProvider: TriageProvider = RuleBasedTriageProvider()
) {
    suspend fun evaluateSymptoms(
        symptoms: List<String>,
        freeText: String,
        deepThinking: Boolean = false,
        searchGrounding: Boolean = false
    ): TriageResult {
        return try {
            primaryProvider.evaluate(symptoms, freeText, deepThinking, searchGrounding)
        } catch (e: Exception) {
            fallbackProvider.evaluate(symptoms, freeText, deepThinking, searchGrounding)
        }
    }
}
