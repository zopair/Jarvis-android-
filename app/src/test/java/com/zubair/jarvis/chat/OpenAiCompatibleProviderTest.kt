package com.zubair.jarvis.chat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class OpenAiCompatibleProviderTest {
    private val provider = OpenAiCompatibleProvider()

    @Test
    fun requestUsesConfiguredModelAndPreservesConversationOrder() {
        val body = provider.buildRequestBody(
            "example-model",
            listOf(
                ChatMessage("user", "Hello"),
                ChatMessage("assistant", "Hi")
            )
        )

        assertEquals("example-model", body.get("model").asString)
        val messages = body.getAsJsonArray("messages")
        assertEquals(2, messages.size())
        assertEquals("user", messages[0].asJsonObject.get("role").asString)
        assertEquals("Hello", messages[0].asJsonObject.get("content").asString)
        assertEquals("assistant", messages[1].asJsonObject.get("role").asString)
    }

    @Test
    fun parsesAssistantResponse() {
        val response = """{"choices":[{"message":{"role":"assistant","content":"  Hello from Jarvis  "}}]}"""
        assertEquals("Hello from Jarvis", provider.parseAssistantText(response))
    }

    @Test
    fun rejectsEmptyAssistantResponse() {
        val response = """{"choices":[{"message":{"role":"assistant","content":" "}}]}"""
        assertThrows(java.io.IOException::class.java) {
            provider.parseAssistantText(response)
        }
    }
}
