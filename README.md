# Jarvis Android

<p align="center">
  <strong>A private, local-first AI workspace for Android.</strong><br/>
  Native Kotlin · Jetpack Compose · Provider-neutral architecture
</p>

<p align="center">
  <a href="https://github.com/zopair/Jarvis-android-/actions/workflows/android.yml"><img alt="Android CI" src="https://github.com/zopair/Jarvis-android-/actions/workflows/android.yml/badge.svg?branch=main"></a>
  <img alt="Platform" src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Minimum SDK" src="https://img.shields.io/badge/minimum%20SDK-26-64748b">
  <img alt="Gradle" src="https://img.shields.io/badge/Gradle-9.5.0-02303A?logo=gradle">
  <img alt="License" src="https://img.shields.io/badge/license-pending-lightgrey">
</p>

Jarvis brings the composable ideas behind [OpenJarvis](https://github.com/open-jarvis/OpenJarvis) to a native Android application. The goal is a capable personal AI endpoint with on-device intelligence where practical, optional cloud inference, permission-aware tools, and user-controlled memory.

> **Project status:** foundation stage. The Android shell, product identity, reproducible Gradle Wrapper, CI pipeline, and initial security policy are in place. A working inference runtime, persistent chat, and agent execution are not yet implemented. The repository does not claim otherwise.

## Product principles

| Principle | Engineering commitment |
|---|---|
| Local-first | Prefer on-device inference and local persistence; cloud is optional |
| Provider-neutral | Keep model integrations behind stable contracts |
| User-controlled | Model output never authorizes an action by itself |
| Secure by default | Unknown tools are denied; consequential actions require confirmation |
| Android-native | Respect lifecycle, scoped storage, runtime permissions, and background limits |
| Resource-aware | Consider RAM, storage, ABI, battery, thermal state, and connectivity |

## Architecture at a glance

```text
Jetpack Compose UI
       │
Presentation / ViewModels
       │
Application Orchestrator
  ┌────┼────────┬──────────┐
Agents Memory  Tool Hub  Model Router
  │      │       │           │
Domain contracts / policy / traces
  │              │
Inference ports  Android capability adapters
  │              │
Local or cloud   Android platform APIs
providers
```

The five product primitives are **Intelligence**, **Engine**, **Agents**, **Tools**, and **Memory & Learning**. See [the architecture](docs/architecture/ANDROID_ARCHITECTURE.md) and [the delivery roadmap](docs/ROADMAP.md).

## Current implementation

- [x] Native Android application shell and branded Compose foundation UI
- [x] Adaptive launcher icon and app theme
- [x] Deny-by-default tool authorization policy and unit tests
- [x] Official Gradle 9.5.0 Wrapper with distribution checksum verification
- [x] Android CI: wrapper validation, unit tests, debug APK assembly, and artifact checksum
- [x] Debug APK uploaded as a 14-day GitHub Actions artifact
- [ ] Domain contracts and application orchestration
- [ ] Persistent local conversations
- [ ] First verified on-device inference adapter
- [ ] Tool registry, user approvals, and execution traces

A green build validates the current foundation; it is not evidence that unfinished product capabilities are already available.

## Build

**Requirements:** JDK 17 and Android SDK with API 37 platform and Build Tools 37.0.0.

The repository includes the official Gradle 9.5.0 Wrapper. The downloaded Gradle distribution is verified against its published SHA-256 checksum.

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

On Windows:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

For a ready-built APK, open the latest successful [Android CI run](https://github.com/zopair/Jarvis-android-/actions/workflows/android.yml) and download the `jarvis-android-debug-apk` artifact. The artifact includes a SHA-256 checksum file.

## Security model

- Model and retrieved content are untrusted input.
- Unknown tools and missing capabilities are denied.
- External side effects and destructive actions require explicit user confirmation.
- No unrestricted shell execution, hidden accessibility automation, or background recording.
- Secrets must never be committed to source control.
- Memory and traces must remain inspectable and user-controllable.

See [SECURITY.md](SECURITY.md) for vulnerability reporting guidance.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. Keep changes focused, add tests for behavior, and do not mark a roadmap item complete until its acceptance criteria are verified.

## Upstream inspiration

- [OpenJarvis repository](https://github.com/open-jarvis/OpenJarvis)
- [OpenJarvis architecture overview](https://github.com/open-jarvis/OpenJarvis/blob/main/docs/architecture/overview.md)
- [OpenJarvis documentation](https://open-jarvis.github.io/OpenJarvis/)

Jarvis Android is an independent Android-native implementation, not a direct port of the upstream Python/server runtime. OpenJarvis licensing does not automatically determine this repository's license.
