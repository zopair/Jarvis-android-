package com.zubair.jarvis.core.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolAuthorizationPolicyTest {
    @Test
    fun readOnlyToolIsAllowedWhenCapabilityIsGranted() {
        assertTrue(
            ToolAuthorizationPolicy.isAllowed(
                risk = ToolRisk.READ_ONLY,
                capabilityGranted = true,
                userConfirmed = false
            )
        )
    }

    @Test
    fun anyToolIsDeniedWhenCapabilityIsMissing() {
        assertFalse(
            ToolAuthorizationPolicy.isAllowed(
                risk = ToolRisk.READ_ONLY,
                capabilityGranted = false,
                userConfirmed = true
            )
        )
    }

    @Test
    fun externalSideEffectRequiresExplicitConfirmation() {
        assertFalse(
            ToolAuthorizationPolicy.isAllowed(
                risk = ToolRisk.EXTERNAL_SIDE_EFFECT,
                capabilityGranted = true,
                userConfirmed = false
            )
        )
        assertTrue(
            ToolAuthorizationPolicy.isAllowed(
                risk = ToolRisk.EXTERNAL_SIDE_EFFECT,
                capabilityGranted = true,
                userConfirmed = true
            )
        )
    }

    @Test
    fun destructiveActionRequiresExplicitConfirmation() {
        assertFalse(
            ToolAuthorizationPolicy.isAllowed(
                risk = ToolRisk.DESTRUCTIVE,
                capabilityGranted = true,
                userConfirmed = false
            )
        )
        assertTrue(
            ToolAuthorizationPolicy.isAllowed(
                risk = ToolRisk.DESTRUCTIVE,
                capabilityGranted = true,
                userConfirmed = true
            )
        )
    }
}
