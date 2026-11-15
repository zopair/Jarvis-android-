package com.zubair.jarvis.core.security

enum class ToolRisk {
    READ_ONLY,
    REVERSIBLE,
    EXTERNAL_SIDE_EFFECT,
    DESTRUCTIVE
}

object ToolAuthorizationPolicy {
    fun isAllowed(
        risk: ToolRisk,
        capabilityGranted: Boolean,
        userConfirmed: Boolean
    ): Boolean {
        if (!capabilityGranted) return false

        return when (risk) {
            ToolRisk.READ_ONLY,
            ToolRisk.REVERSIBLE -> true

            ToolRisk.EXTERNAL_SIDE_EFFECT,
            ToolRisk.DESTRUCTIVE -> userConfirmed
        }
    }
}
