package com.zubair.jarvis.chat

/**
 * Selects a real inference path without treating an absent optional cloud provider as an error.
 */
internal object ChatRouteResolver {
    enum class Route { LOCAL, CLOUD, SETUP_REQUIRED }

    fun resolve(localModelLoaded: Boolean, config: ProviderConfig, apiKey: String): Route {
        if (localModelLoaded) return Route.LOCAL
        val endpoint = runCatching { java.net.URI(config.endpoint.trim()) }.getOrNull()
        val cloudReady = endpoint != null &&
            endpoint.scheme.equals("https", ignoreCase = true) &&
            !endpoint.host.isNullOrBlank() &&
            config.model.isNotBlank() &&
            apiKey.isNotBlank()
        return if (cloudReady) Route.CLOUD else Route.SETUP_REQUIRED
    }
}
