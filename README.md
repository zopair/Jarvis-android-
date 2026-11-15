# Jarvis Android

<p align="center">
  <strong>A private, local-first AI workspace for Android.</strong><br/>
  Native Kotlin · Jetpack Compose · Provider-neutral architecture
</p>

<p align="center">
  <a href="https://github.com/zopair/Jarvis-android-/releases/latest"><img alt="Download for Android" src="https://img.shields.io/badge/Android-Download%20APK-3DDC84?logo=android&logoColor=white"></a>
  <a href="https://github.com/zopair/Jarvis-android-/actions/workflows/android.yml"><img alt="Android CI" src="https://github.com/zopair/Jarvis-android-/actions/workflows/android.yml/badge.svg?branch=main"></a>
  <img alt="Platform" src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin">
  <img alt="Minimum SDK" src="https://img.shields.io/badge/minimum%20SDK-26-64748b">
  <img alt="Gradle" src="https://img.shields.io/badge/Gradle-9.5.0-02303A?logo=gradle">
</p>

<p align="center">
  <a href="https://github.com/zopair">GitHub Organization / Maintainer Profile</a>
  · <a href="https://github.com/zopair/Jarvis-android-/issues">Issues</a>
  · <a href="https://github.com/zopair/Jarvis-android-/discussions">Discussions</a>
</p>

## Project

Jarvis brings the composable ideas behind [OpenJarvis](https://github.com/open-jarvis/OpenJarvis) to a native Android application. The goal is a capable personal AI endpoint with on-device intelligence where practical, optional cloud inference, permission-aware tools, and user-controlled memory.

**Project founder and creator:** Aly Hussein Zopair

> **Implementation status:** functional chat MVP in validation. The app includes an OpenAI-compatible HTTPS chat client, encrypted API-key storage backed by Android Keystore, and app-private conversation persistence. It requires the user to configure a compatible provider endpoint, model ID, and API key. On-device inference, voice, autonomous agents, and background actions are not implemented yet.

## Android app

The app interface includes a direct Android release entry point:

- **[Download the latest Android APK](https://github.com/zopair/Jarvis-android-/releases/latest)** — published stable builds will appear here.
- [Browse all releases](https://github.com/zopair/Jarvis-android-/releases)
- [Latest CI build artifacts](https://github.com/zopair/Jarvis-android-/actions/workflows/android.yml) — temporary build artifacts are available from individual workflow runs.

A release link may show no downloadable APK until the first GitHub Release is published. CI artifacts are temporary and are not a substitute for a versioned release.

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
- [x] Android release and source-code entry points in the app interface
- [x] Adaptive launcher icon and app theme
- [x] Deny-by-default tool authorization policy and unit tests
- [x] Official Gradle 9.5.0 Wrapper with distribution checksum verification
- [x] Android CI: wrapper validation, unit tests, debug APK assembly, and artifact checksum
- [x] Debug APK uploaded as a 14-day GitHub Actions artifact
- [ ] First versioned public Android release
- [ ] Domain contracts and application orchestration
- [x] Provider-backed chat UI and OpenAI-compatible HTTPS adapter
- [x] App-private conversation history persistence
- [x] Android Keystore AES-GCM encrypted provider API key storage
- [ ] First verified on-device inference adapter
- [ ] Tool registry, user approvals, and execution traces

A green build validates the current foundation; it is not evidence that unfinished product capabilities are already available.

## Build

**Requirements:** JDK 17 and Android SDK with API 37 platform and Build Tools 37.0.0.

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

On Windows:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

### Configure the chat provider

1. Open Jarvis and enter **Settings**.
2. Set an OpenAI-compatible HTTPS Chat Completions endpoint (the default is `https://api.openai.com/v1/chat/completions`).
3. Enter the model ID accepted by your provider.
4. Enter your own provider API key and save.
5. Send a message. The key is encrypted with Android Keystore and is not embedded in the APK.

The configured provider must implement the OpenAI-compatible `/chat/completions` request and response shape. The app does not ship with a bundled API key or free hosted inference service. Conversation history is stored in app-private internal storage.

## Security and responsible disclosure

- Model and retrieved content are untrusted input.
- Unknown tools and missing capabilities are denied.
- External side effects and destructive actions require explicit user confirmation.
- No unrestricted shell execution, hidden accessibility automation, or background recording.
- Secrets must never be committed to source control.
- Memory and traces must remain inspectable and user-controllable.

See [SECURITY.md](SECURITY.md) for vulnerability reporting guidance.

## Governance and contribution

- [Contribution guide](CONTRIBUTING.md)
- [Security policy](SECURITY.md)
- [Issue tracker](https://github.com/zopair/Jarvis-android-/issues)
- [Pull requests](https://github.com/zopair/Jarvis-android-/pulls)

Changes should be focused, tested, documented, and reviewed. Do not mark roadmap items complete until their acceptance criteria are verified.

## Upstream inspiration

- [OpenJarvis repository](https://github.com/open-jarvis/OpenJarvis)
- [OpenJarvis architecture overview](https://github.com/open-jarvis/OpenJarvis/blob/main/docs/architecture/overview.md)
- [OpenJarvis documentation](https://open-jarvis.github.io/OpenJarvis/)

Jarvis Android is an independent Android-native implementation, not a direct port of the upstream Python/server runtime. OpenJarvis licensing does not automatically determine this repository's license.

---

<p align="center">
  **Created and maintained by Aly Hussein Zopair**
  <br/>
  <a href="https://github.com/zopair">GitHub Profile</a>
</p>
