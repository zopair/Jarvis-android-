# Project profile

## Identity
- Product: Jarvis Android
- Category: Native Android AI assistant foundation
- Creator and project maintainer: Aly Hussein Zopair
- Source repository: https://github.com/zopair/Jarvis-android-
- Android release channel: https://github.com/zopair/Jarvis-android-/releases
- Upstream inspiration: https://github.com/open-jarvis/OpenJarvis

## Product positioning
A privacy-conscious, local-first Android AI workspace designed around modular inference providers, user-controlled memory, permission-aware tools, and resource-aware on-device execution.

## Current maturity
Foundation / pre-runtime. The Android shell, Compose UI, repository build pipeline, and baseline security policy exist. Inference, persistent conversations, and agent execution remain roadmap work.

## Engineering standards
- Kotlin and native Android APIs
- Jetpack Compose UI
- Reproducible Gradle Wrapper builds
- CI-gated unit tests and debug APK assembly
- Deny-by-default capability policy
- Explicit user approval for consequential actions
- Documented architecture and roadmap
- No claims of capability without implementation and verification

## Release policy
Versioned Android APKs belong in GitHub Releases. CI artifacts are temporary engineering outputs and must not be presented as stable public releases.
