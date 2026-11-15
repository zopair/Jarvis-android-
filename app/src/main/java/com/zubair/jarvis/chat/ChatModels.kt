package com.zubair.jarvis.chat

data class ChatMessage(
    val role: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class ProviderConfig(
    val endpoint: String = "",
    val model: String = ""
)

data class ChatRequestMessage(
    val role: String,
    val content: String
)
