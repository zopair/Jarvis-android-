package com.zubair.jarvis.chat

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class ChatStorage(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun loadConfig(): ProviderConfig = ProviderConfig(
        endpoint = preferences.getString(KEY_ENDPOINT, "").orEmpty(),
        model = preferences.getString(KEY_MODEL, "").orEmpty()
    )

    fun saveConfig(config: ProviderConfig) {
        preferences.edit()
            .putString(KEY_ENDPOINT, config.endpoint.trim())
            .putString(KEY_MODEL, config.model.trim())
            .apply()
    }

    fun saveApiKey(apiKey: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(apiKey.toByteArray(Charsets.UTF_8))
        preferences.edit()
            .putString(KEY_API_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString(KEY_API_CIPHER, Base64.encodeToString(encrypted, Base64.NO_WRAP))
            .apply()
    }

    fun loadApiKey(): String? = runCatching {
        val iv = preferences.getString(KEY_API_IV, null) ?: return null
        val encrypted = preferences.getString(KEY_API_CIPHER, null) ?: return null
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP))
        )
        String(cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)), Charsets.UTF_8)
    }.getOrNull()

    fun clearApiKey() {
        preferences.edit().remove(KEY_API_IV).remove(KEY_API_CIPHER).apply()
    }

    fun saveLocalModelPath(path: String) {
        preferences.edit().putString(KEY_LOCAL_MODEL_PATH, path).apply()
    }

    fun loadLocalModelPath(): String? = preferences.getString(KEY_LOCAL_MODEL_PATH, null)

    fun clearLocalModelPath() {
        preferences.edit().remove(KEY_LOCAL_MODEL_PATH).apply()
    }

    fun loadMessages(): List<ChatMessage> = runCatching {
        val json = appContext.openFileInput(CHAT_FILE).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        gson.fromJson<List<ChatMessage>>(json, type).orEmpty()
    }.getOrDefault(emptyList())

    fun saveMessages(messages: List<ChatMessage>) {
        val json = gson.toJson(messages)
        appContext.openFileOutput(CHAT_FILE, Context.MODE_PRIVATE).bufferedWriter().use { it.write(json) }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance("AES", ANDROID_KEY_STORE)
        generator.init(
            android.security.keystore.KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                    android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes("GCM")
                .setEncryptionPaddings("NoPadding")
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        const val KEY_ALIAS = "jarvis_provider_api_key_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val CHAT_FILE = "jarvis_conversations.json"
        const val KEY_ENDPOINT = "provider_endpoint"
        const val KEY_MODEL = "provider_model"
        const val KEY_API_IV = "provider_api_iv"
        const val KEY_API_CIPHER = "provider_api_cipher"
        const val KEY_LOCAL_MODEL_PATH = "local_model_path"
    }
}
