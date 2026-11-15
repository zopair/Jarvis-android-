package com.zubair.jarvis

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubair.jarvis.chat.ChatMessage
import com.zubair.jarvis.chat.ChatStorage
import com.zubair.jarvis.chat.OpenAiCompatibleProvider
import com.zubair.jarvis.chat.ProviderConfig
import com.zubair.jarvis.engine.LocalModelEngine
import com.zubair.jarvis.engine.RecommendedModelDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val Midnight = Color(0xFF070B14)
private val Panel = Color(0xFF121D30)
private val Ice = Color(0xFF83D9F7)
private val Muted = Color(0xFF91A1B8)
private val UserBubble = Color(0xFF18364B)
private const val DIRECT_APK_URL = "https://github.com/zopair/Jarvis-android-/releases/download/v0.2.0-test/Jarvis-Android.apk"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = ChatStorage(applicationContext)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Ice,
                    background = Midnight,
                    surface = Panel,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                JarvisChatApp(storage)
            }
        }
    }
}

@Composable
private fun JarvisChatApp(storage: ChatStorage) {
    val context = LocalContext.current
    val provider = remember { OpenAiCompatibleProvider() }
    val localEngine = remember { LocalModelEngine(context.applicationContext) }
    val modelDownloader = remember { RecommendedModelDownloader(context.applicationContext) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val messages = remember {
        mutableStateListOf<ChatMessage>().apply { addAll(storage.loadMessages()) }
    }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var config by remember { mutableStateOf(storage.loadConfig()) }
    var apiKey by remember { mutableStateOf(storage.loadApiKey().orEmpty()) }
    var draft by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var localStatus by remember { mutableStateOf("No local model loaded") }
    var downloadProgress by remember { mutableStateOf<Int?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val modelPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                busy = true
                error = null
                try {
                    val modelFile = withContext(Dispatchers.IO) {
                        val name = "jarvis-local-model.gguf"
                        val destination = File(context.filesDir, name)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            destination.outputStream().use { output -> input.copyTo(output) }
                        } ?: throw java.io.IOException("Android could not open the selected model file.")
                        require(destination.length() > 4L) { "The selected model file is empty." }
                        val magic = destination.inputStream().use { input -> ByteArray(4).also { input.read(it) } }
                        require(String(magic, Charsets.US_ASCII) == "GGUF") { "This file is not a valid GGUF model." }
                        destination
                    }
                    storage.saveLocalModelPath(modelFile.absolutePath)
                    localStatus = "Loading local model…"
                    localEngine.loadModel(modelFile.absolutePath) { state ->
                        when (state) {
                            LocalModelEngine.State.Loading -> localStatus = "Loading local model…"
                            LocalModelEngine.State.Ready -> localStatus = "LOCAL MODEL READY"
                            LocalModelEngine.State.Unloaded -> localStatus = "No local model loaded"
                            is LocalModelEngine.State.Failed -> {
                                localStatus = "Local model unavailable"
                                error = state.message
                            }
                        }
                        busy = state == LocalModelEngine.State.Loading
                    }
                } catch (failure: Exception) {
                    error = failure.message ?: "Could not import the GGUF model."
                    localStatus = "No local model loaded"
                    busy = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val savedPath = storage.loadLocalModelPath()
        if (!savedPath.isNullOrBlank() && File(savedPath).isFile) {
            busy = true
            localStatus = "Loading saved local model…"
            localEngine.loadModel(savedPath) { state ->
                when (state) {
                    LocalModelEngine.State.Loading -> localStatus = "Loading saved local model…"
                    LocalModelEngine.State.Ready -> localStatus = "LOCAL MODEL READY"
                    LocalModelEngine.State.Unloaded -> localStatus = "No local model loaded"
                    is LocalModelEngine.State.Failed -> {
                        localStatus = "Local model unavailable"
                        error = state.message
                    }
                }
                busy = state == LocalModelEngine.State.Loading
            }
        }
    }

    LaunchedEffect(messages.size, busy) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Midnight).padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("JARVIS", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Text("ANDROID INTELLIGENCE", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { uriHandler.openUri(DIRECT_APK_URL) }) { Text("APK", color = Ice, fontSize = 10.sp) }
                TextButton(enabled = !busy, onClick = {
                    scope.launch {
                        busy = true
                        error = null
                        downloadProgress = 0
                        localStatus = "Downloading verified Qwen model…"
                        try {
                            val modelFile = withContext(Dispatchers.IO) {
                                modelDownloader.download { progress ->
                                    Handler(Looper.getMainLooper()).post { downloadProgress = progress }
                                }
                            }
                            storage.saveLocalModelPath(modelFile.absolutePath)
                            localStatus = "Loading local model…"
                            localEngine.loadModel(modelFile.absolutePath) { state ->
                                when (state) {
                                    LocalModelEngine.State.Loading -> localStatus = "Loading local model…"
                                    LocalModelEngine.State.Ready -> localStatus = "LOCAL MODEL READY"
                                    LocalModelEngine.State.Unloaded -> localStatus = "No local model loaded"
                                    is LocalModelEngine.State.Failed -> { localStatus = "Local model unavailable"; error = state.message }
                                }
                                busy = state == LocalModelEngine.State.Loading
                                if (state != LocalModelEngine.State.Loading) downloadProgress = null
                            }
                        } catch (failure: Exception) {
                            error = failure.message ?: "Model download failed."
                            localStatus = "No local model loaded"
                            busy = false
                            downloadProgress = null
                        }
                    }
                }) { Text("GET MODEL", color = Ice, fontSize = 10.sp) }
                TextButton(enabled = !busy, onClick = { modelPicker.launch(arrayOf("*/*")) }) { Text("IMPORT", color = Ice, fontSize = 10.sp) }
                TextButton(enabled = !busy, onClick = {
                    messages.clear()
                    error = null
                    scope.launch(Dispatchers.IO) { storage.saveMessages(emptyList()) }
                }) { Text("CLEAR", color = Muted, fontSize = 10.sp) }
                TextButton(onClick = { showSettings = true }) { Text("SETTINGS", color = Ice, fontSize = 10.sp) }
            }
        }

        Text(
            if (downloadProgress != null) "DOWNLOADING MODEL: " + downloadProgress + "%" else localStatus,
            color = if (localEngine.isLoaded()) Color(0xFF75D9B0) else Muted,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 5.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f),
            color = Panel,
            shape = RoundedCornerShape(18.dp)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(22.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("J", color = Ice, fontSize = 48.sp, fontWeight = FontWeight.Light)
                    Spacer(Modifier.height(12.dp))
                    Text("Ready when you are.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Choose LOCAL to load a GGUF model for offline chat, or configure a cloud provider in SETTINGS.",
                        color = Muted, fontSize = 13.sp, lineHeight = 20.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { message -> MessageBubble(message) }
                    if (busy) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.padding(8.dp), color = Ice, strokeWidth = 2.dp)
                                Text("Jarvis is thinking…", color = Muted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        error?.let { Text(it, color = Color(0xFFFFA6A6), fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp)) }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message Jarvis…", color = Muted) },
                enabled = !busy,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp)
            )
            Button(
                onClick = {
                    val text = draft.trim()
                    if (text.isNotEmpty() && !busy) {
                        draft = ""
                        error = null
                        messages.add(ChatMessage("user", text))
                        busy = true
                        if (localEngine.isLoaded()) {
                            messages.add(ChatMessage("assistant", ""))
                            val assistantIndex = messages.lastIndex
                            localEngine.send(text, null) { result ->
                                when (result) {
                                    LocalModelEngine.Generation.Started -> Unit
                                    is LocalModelEngine.Generation.Token -> {
                                        val previous = messages[assistantIndex]
                                        messages[assistantIndex] = previous.copy(content = previous.content + result.value)
                                    }
                                    is LocalModelEngine.Generation.Completed -> {
                                        messages[assistantIndex] = messages[assistantIndex].copy(content = result.text)
                                        busy = false
                                        scope.launch(Dispatchers.IO) { storage.saveMessages(messages.toList()) }
                                    }
                                    is LocalModelEngine.Generation.Failed -> {
                                        error = result.message
                                        busy = false
                                    }
                                    LocalModelEngine.Generation.Cancelled -> busy = false
                                }
                            }
                        } else {
                            scope.launch {
                                try {
                                    val requestHistory = messages.toList()
                                    withContext(Dispatchers.IO) { storage.saveMessages(requestHistory) }
                                    val answer = provider.complete(config, apiKey, requestHistory)
                                    messages.add(ChatMessage("assistant", answer))
                                    val completedHistory = messages.toList()
                                    withContext(Dispatchers.IO) { storage.saveMessages(completedHistory) }
                                } catch (failure: Exception) {
                                    error = failure.message ?: "The request failed. Check provider settings and connectivity."
                                } finally {
                                    busy = false
                                }
                            }
                        }
                    }
                },
                enabled = !busy && draft.isNotBlank(),
                modifier = Modifier.padding(bottom = 4.dp)
            ) { Text("SEND") }
        }
        Text(
            if (localEngine.isLoaded()) "On-device inference is active. Messages stay on this phone."
            else "Cloud provider requests use HTTPS. API keys are encrypted with Android Keystore.",
            color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 7.dp)
        )
    }

    if (showSettings) {
        ProviderSettingsDialog(
            initialConfig = config,
            initialApiKey = apiKey,
            onDismiss = { showSettings = false },
            onSave = { newConfig, newKey ->
                config = newConfig
                storage.saveConfig(newConfig)
                if (newKey.isNotBlank()) {
                    storage.saveApiKey(newKey)
                    apiKey = newKey
                }
                showSettings = false
                error = null
            },
            onClearKey = {
                storage.clearApiKey()
                apiKey = ""
            }
        )
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            color = if (isUser) UserBubble else Color(0xFF1A263B),
            shape = RoundedCornerShape(15.dp)
        ) {
            Column(Modifier.padding(horizontal = 13.dp, vertical = 10.dp)) {
                Text(if (isUser) "YOU" else "JARVIS", color = if (isUser) Ice else Color(0xFF75D9B0), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Text(message.content, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun ProviderSettingsDialog(
    initialConfig: ProviderConfig,
    initialApiKey: String,
    onDismiss: () -> Unit,
    onSave: (ProviderConfig, String) -> Unit,
    onClearKey: () -> Unit
) {
    var endpoint by remember { mutableStateOf(initialConfig.endpoint.ifBlank { "https://api.openai.com/v1/chat/completions" }) }
    var model by remember { mutableStateOf(initialConfig.model) }
    var apiKey by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect a model provider") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("For cloud chat, add an OpenAI-compatible HTTPS endpoint, model ID and provider API key. Local GGUF models work without a key.", color = Muted, fontSize = 12.sp)
                OutlinedTextField(value = endpoint, onValueChange = { endpoint = it }, label = { Text("HTTPS endpoint") }, singleLine = true)
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model ID") }, singleLine = true)
                OutlinedTextField(
                    value = apiKey, onValueChange = { apiKey = it },
                    label = { Text(if (initialApiKey.isBlank()) "API key" else "New API key (blank keeps saved key)") },
                    singleLine = true, visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                if (initialApiKey.isNotBlank()) TextButton(onClick = onClearKey) { Text("Remove saved API key", color = Color(0xFFFFA6A6)) }
                validationError?.let { Text(it, color = Color(0xFFFFA6A6), fontSize = 11.sp) }
            }
        },
        confirmButton = {
            Button(onClick = {
                val uri = runCatching { java.net.URI(endpoint.trim()) }.getOrNull()
                if (uri == null || !uri.scheme.equals("https", true) || uri.host.isNullOrBlank()) validationError = "Enter a valid HTTPS endpoint."
                else if (model.isBlank()) validationError = "Enter the provider's model ID."
                else if (apiKey.isBlank() && initialApiKey.isBlank()) validationError = "Enter an API key."
                else onSave(ProviderConfig(endpoint.trim(), model.trim()), apiKey.trim())
            }) { Text("SAVE PROVIDER") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } },
        containerColor = Panel
    )
}
