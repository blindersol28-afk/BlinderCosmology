package com.blindercosmology.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Minimal Gemini client. Uses HttpURLConnection + org.json — no extra dependencies.
 * Designed for short text generation calls; not for streaming.
 */
class GeminiClient(
    private val apiKey: String,
    private val model: String = "gemini-1.5-flash",
) {
    sealed class Result {
        data class Ok(val text: String) : Result()
        data class Err(val message: String) : Result()
    }

    val isConfigured: Boolean
        get() = apiKey.isNotBlank()

    suspend fun generate(
        prompt: String,
        systemInstruction: String? = null,
        temperature: Double = 0.7,
        maxOutputTokens: Int = 1200,
    ): Result = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext Result.Err("Gemini API key not configured.")
        val urlStr = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        val body = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", maxOutputTokens)
                put("topP", 0.95)
            })
            if (!systemInstruction.isNullOrBlank()) {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })
            }
        }.toString()

        var conn: HttpURLConnection? = null
        try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 60_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            if (code !in 200..299) return@withContext Result.Err("HTTP $code: ${truncated(text)}")
            parseFirstCandidate(text)?.let { Result.Ok(it) }
                ?: Result.Err("No candidate text in response.")
        } catch (t: Throwable) {
            Result.Err(t.message ?: t::class.java.simpleName)
        } finally {
            conn?.disconnect()
        }
    }

    private fun parseFirstCandidate(json: String): String? {
        return try {
            val root = JSONObject(json)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val first = candidates.getJSONObject(0)
            val content = first.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            buildString {
                for (i in 0 until parts.length()) {
                    val t = parts.getJSONObject(i).optString("text", "")
                    if (t.isNotEmpty()) {
                        if (isNotEmpty()) append("\n")
                        append(t)
                    }
                }
            }.takeIf { it.isNotBlank() }
        } catch (_: Throwable) { null }
    }

    private fun truncated(s: String, n: Int = 300): String =
        if (s.length <= n) s else s.take(n) + "…"
}
