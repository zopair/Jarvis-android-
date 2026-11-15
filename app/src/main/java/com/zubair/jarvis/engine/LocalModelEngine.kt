package com.zubair.jarvis.engine

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.hazratbilal.aikit.chat.AiKitChat
import com.hazratbilal.aikit.chat.chat
import com.hazratbilal.aikit.core.AiKitEngine

/**
 * Adapter around the MIT-licensed Android AI Kit / llama.cpp runtime.
 * Model files are user-selected GGUF files and remain in app-private storage.
 */
class LocalModelEngine(context: Context) {
    private val engine = AiKitEngine.create(context.applicationContext)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var chat: AiKitChat? = null

    fun isLoaded(): Boolean = engine.isModelLoaded()

    fun currentModelPath(): String? = engine.currentModelPath()

    fun loadModel(path: String, onState: (State) -> Unit) {
        engine.loadModel(path, object : AiKitEngine.ModelStateListener {
            override fun onModelLoading() = dispatch { onState(State.Loading) }
            override fun onModelLoaded() {
                chat = engine.chat()
                dispatch { onState(State.Ready) }
            }
            override fun onModelError(error: Throwable) =
                dispatch { onState(State.Failed(error.message ?: "Local model could not be loaded.")) }
            override fun onModelUnloaded() = dispatch { onState(State.Unloaded) }
        })
    }

    fun send(prompt: String, systemPrompt: String?, onState: (Generation) -> Unit) {
        val activeChat = chat
        if (activeChat == null || !engine.isModelLoaded()) {
            onState(Generation.Failed("Load a GGUF model before sending a message."))
            return
        }
        activeChat.sendMessage(
            prompt = prompt,
            maxTokens = 384,
            systemPrompt = systemPrompt,
            listener = object : AiKitChat.ChatListener {
                override fun onGenerationStarted() = dispatch { onState(Generation.Started) }
                override fun onToken(token: String) = dispatch { onState(Generation.Token(token)) }
                override fun onGenerationComplete(fullResponse: String) =
                    dispatch { onState(Generation.Completed(fullResponse)) }
                override fun onGenerationError(error: Throwable) =
                    dispatch { onState(Generation.Failed(error.message ?: "Local inference failed.")) }
                override fun onCancelled() = dispatch { onState(Generation.Cancelled) }
            }
        )
    }

    fun cancel() {
        chat?.cancelGeneration()
    }

    fun unload() {
        chat?.cancelGeneration()
        chat = null
        engine.unloadModel()
    }

    private fun dispatch(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) action() else mainHandler.post(action)
    }

    sealed interface State {
        data object Loading : State
        data object Ready : State
        data object Unloaded : State
        data class Failed(val message: String) : State
    }

    sealed interface Generation {
        data object Started : Generation
        data class Token(val value: String) : Generation
        data class Completed(val text: String) : Generation
        data class Failed(val message: String) : Generation
        data object Cancelled : Generation
    }
}
