package com.example.services.gemini

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        model: String = "gemini-3.5-flash",
        enableHighThinking: Boolean = false,
        useSearchGrounding: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("GEMINI_API_KEY is not configured in .env"))
        }

        try {
            val root = JSONObject()

            // System Instruction
            if (!systemInstruction.isNullOrBlank()) {
                val sysPart = JSONObject().put("text", systemInstruction)
                val sysContent = JSONObject().put("parts", JSONArray().put(sysPart))
                root.put("systemInstruction", sysContent)
            }

            // User Contents
            val userPart = JSONObject().put("text", prompt)
            val userContent = JSONObject().put("role", "user").put("parts", JSONArray().put(userPart))
            root.put("contents", JSONArray().put(userContent))

            val targetModel = if (enableHighThinking) "gemini-3.1-pro-preview" else model

            // Generation Config
            val generationConfig = JSONObject()
            if (enableHighThinking) {
                val thinkingConfig = JSONObject().put("thinkingLevel", "HIGH")
                generationConfig.put("thinkingConfig", thinkingConfig)
            } else {
                generationConfig.put("temperature", 0.4)
            }
            root.put("generationConfig", generationConfig)

            // Tools (Search grounding if requested)
            if (useSearchGrounding) {
                val toolObj = JSONObject().put("googleSearch", JSONObject())
                root.put("tools", JSONArray().put(toolObj))
            }

            val requestBody = root.toString().toRequestBody("application/json".toMediaType())
            val url = "$BASE_URL$targetModel:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gemini API Error HTTP ${response.code}: $responseBody"))
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val textBuilder = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("text")) {
                            textBuilder.append(part.getString("text"))
                        }
                    }
                    return@withContext Result.success(textBuilder.toString())
                }
            }

            Result.failure(Exception("No candidate content received from Gemini."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
