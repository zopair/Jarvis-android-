package com.zubair.jarvis.chat

import org.junit.Assert.assertEquals
import org.junit.Test

class ChatRouteResolverTest {
    @Test
    fun emptyOptionalCloudSettingsRequireSetupInsteadOfCallingProvider() {
        assertEquals(
            ChatRouteResolver.Route.SETUP_REQUIRED,
            ChatRouteResolver.resolve(false, ProviderConfig(), "")
        )
    }

    @Test
    fun loadedLocalModelAlwaysUsesLocalInferenceWithoutCloudCredentials() {
        assertEquals(
            ChatRouteResolver.Route.LOCAL,
            ChatRouteResolver.resolve(true, ProviderConfig(), "")
        )
    }

    @Test
    fun completeHttpsProviderConfigurationEnablesCloudInference() {
        assertEquals(
            ChatRouteResolver.Route.CLOUD,
            ChatRouteResolver.resolve(
                false,
                ProviderConfig("https://api.example.com/v1/chat/completions", "example-model"),
                "test-key"
            )
        )
    }

    @Test
    fun invalidOrIncompleteCloudConfigurationNeverEnablesCloudInference() {
        assertEquals(
            ChatRouteResolver.Route.SETUP_REQUIRED,
            ChatRouteResolver.resolve(
                false,
                ProviderConfig("http://api.example.com/v1/chat/completions", "example-model"),
                "test-key"
            )
        )
    }
}
