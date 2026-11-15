package com.zubair.jarvis.engine

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Downloads one pinned, Apache-2.0 Qwen2.5 0.5B GGUF quantization.
 * The SHA-256 is pinned to the exact Hugging Face revision used by this URL.
 */
class RecommendedModelDownloader(context: Context) {
    private val appContext = context.applicationContext
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.MINUTES)
        .callTimeout(20, TimeUnit.MINUTES)
        .build()

    fun download(onProgress: (Int) -> Unit): File {
        val modelDir = File(appContext.filesDir, "models").apply { mkdirs() }
        val destination = File(modelDir, MODEL_FILENAME)
        val temporary = File(modelDir, "$MODEL_FILENAME.part")
        if (destination.isFile && sha256(destination) == EXPECTED_SHA256) {
            onProgress(100)
            return destination
        }
        require(modelDir.usableSpace >= MIN_FREE_BYTES) {
            "At least 550 MB of free internal storage is required to download the local model."
        }

        val request = Request.Builder().url(MODEL_URL).get().build()
        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "Model download failed: HTTP ${response.code}" }
            val body = response.body ?: error("The model server returned an empty response.")
            val total = body.contentLength()
            body.byteStream().use { input ->
                FileOutputStream(temporary).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var downloaded = 0L
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        downloaded += count
                        if (total > 0L) onProgress(((downloaded * 100) / total).toInt().coerceIn(0, 99))
                    }
                    output.fd.sync()
                }
            }
        }

        check(sha256(temporary) == EXPECTED_SHA256) {
            temporary.delete()
            "Downloaded model failed SHA-256 verification. Please retry."
        }
        if (destination.exists()) destination.delete()
        check(temporary.renameTo(destination)) { "Could not finalize the downloaded model file." }
        onProgress(100)
        return destination
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val MODEL_FILENAME = "Qwen2.5-0.5B-Instruct-Q4_K_M.gguf"
        const val MODEL_URL = "https://huggingface.co/bartowski/Qwen2.5-0.5B-Instruct-GGUF/resolve/a5f758267d6599f03006d1e909676f2e2671fb0f/Qwen2.5-0.5B-Instruct-Q4_K_M.gguf"
        const val EXPECTED_SHA256 = "b6b3cd19f6e76f178028a34f937a8a08ba8521a37e035638eec3a5dc5a33a564"
        const val MIN_FREE_BYTES = 550L * 1024L * 1024L
    }
}
