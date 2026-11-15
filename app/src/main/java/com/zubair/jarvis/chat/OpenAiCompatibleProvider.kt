package com.zubair.jarvis.chat

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class OpenAiCompatibleProvider(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    suspend fun complete(
        config: ProviderConfig,
        apiKey: String,
        messages: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        validateConfig(config, apiKey)
        val body = buildRequestBody(config.model, messages)
            .toString()
            .toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(config.endpoint)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val responseText = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val detail = parseError(responseText)
                throw IOException("Provider returned HTTP ${response.code}: $detail")
            }
            parseAssistantText(responseText)
        }
    }

    internal fun buildRequestBody(model: String, messages: List<ChatMessage>): JsonObject {
        val root = JsonObject()
        root.addProperty("model", model)
        val messageArray = JsonArray()
        messages.forEach { message ->
            val item = JsonObject()
            item.addProperty("role", message.role)
            item.addProperty("content", message.content)
            messageArray.add(item)
        }
        root.add("messages", messageArray)
        return root
    }

    internal fun parseAssistantText(responseText: String): String {
        val root = JsonParser.parseString(responseText).asJsonObject
        val content = root.getAsJsonArray("choices")
            ?.firstOrNull()?.asJsonObject
            ?.getAsJsonObject("message")
            ?.get("content")
            ?.asString
            ?.trim()
            .orEmpty()
        if (content.isBlank()) throw IOException("The model returned an empty response.")
        return content
    }

    private fun parseError(responseText: String): String = runCatching {
        JsonParser.parseString(responseText).asJsonObject
            .getAsJsonObject("error")?.get("message")?.asString
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: "Request could not be completed."

    private fun validateConfig(config: ProviderConfig, apiKey: String) {
        val uri = runCatching { java.net.URI(config.endpoint) }.getOrNull()
        require(uri != null && uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank()) {
            "Provider endpoint must be a valid HTTPS URL."
        }
        require(config.model.isNotBlank()) { "Enter a model name in Settings." }
        require(apiKey.isNotBlank()) { "Add your provider API key in Settings." }
        require(messagesAreValid = true)
    }

    private fun require(messagesAreValid: Boolean) {
        require(messagesAreValid) { "At least one message is required." }
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
