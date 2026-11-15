# ADR 0001: Local-first, provider-neutral Android assistant

- **Status:** Accepted
- **Date:** 2026-11-15

## Context

OpenJarvis demonstrates a composable personal-AI architecture with model intelligence, inference engines, agents, tools/memory and trace-driven learning. Android imposes different constraints: mobile RAM/storage, battery and thermal limits, scoped storage, runtime permissions, background execution limits, and app sandboxing.

A direct port of the upstream Python/server stack would add operational weight and would not provide native Android integration.

## Decision

Build Jarvis Android as a native Kotlin application. Keep the domain and orchestration contracts provider-neutral and isolate Android APIs behind adapters. Prefer local inference and local persistence; expose cloud providers as optional integrations. Require policy checks and user confirmation for consequential device actions.

## Consequences

### Positive
- Native lifecycle, permissions, notifications, voice and storage integration.
- Local-first behavior and provider choice remain replaceable.
- Domain logic can be unit-tested without an Android device.
- Security policy is centralized rather than scattered through UI code.

### Trade-offs
- Local model support must be validated per device and ABI.
- Multiple inference adapters increase maintenance cost.
- Android background restrictions limit always-on behavior.
- Feature delivery is staged; the first release will not match every desktop OpenJarvis capability.

## Guardrails

- No secrets in source control.
- No unrestricted shell execution or arbitrary accessibility automation.
- No hidden background recording or data collection.
- No irreversible or externally visible tool action without explicit authorization.
