# Jarvis Android Architecture

## Product direction

Jarvis Android is a native Android personal AI assistant inspired by the composable, local-first principles of OpenJarvis. It is an independent implementation for Android, not a port of the Python desktop/server stack and not a copy of upstream source.

**Primary objective:** make the phone a capable personal AI endpoint with on-device inference as the preferred path, optional cloud providers, explicit user-approved Android actions, durable memory, and observable agent execution.

## Architecture rules

1. Local-first: core chat, conversation history, task planning, and local memory must work without a network connection.
2. Provider-neutral: model access is behind interfaces; no UI or agent code depends on a specific vendor SDK.
3. Capability-gated: tools declare capabilities and required Android permissions. Every consequential action requires an explicit policy decision and, where appropriate, user confirmation.
4. Offline-safe: network loss, provider errors, model unavailability, and permission denial are normal states, not fatal app failures.
5. Modular: each primitive can be replaced or extended without rewriting the app.
6. Observable: record structured, privacy-conscious traces for inference, tool execution, latency, and failures.
7. Resource-aware: model selection considers device RAM, storage, thermal state, battery, latency, and connectivity.
8. Secure by default: secrets stay in Android Keystore-backed storage; untrusted model output never executes directly as shell, code, or an Android action.

## Layered design

```
Android UI (Jetpack Compose)
        |
Presentation / ViewModels
        |
Application Orchestrator
  |       |        |       |
Agents  Memory  Tool Hub  Model Router
  |       |        |       |
Inference Providers   Android Capability Adapters
  |                       |
Local runtimes       Android OS APIs
  |
Local models / optional cloud APIs

Cross-cutting: policy, permissions, audit traces, settings, telemetry
```

## Core modules

| Module | Responsibility | Initial boundary |
|---|---|---|
| :app | Application entry point, Compose navigation, dependency composition | Android app only |
| :core:domain | Conversation, message, task, tool, model, trace and memory contracts | Pure Kotlin; no Android dependencies |
| :core:orchestration | Agent loop, task planning, model routing and cancellation | Depends on domain contracts |
| :core:inference | Provider interfaces and adapters | Local and cloud providers are independent |
| :core:memory | Conversation persistence, user-approved facts and retrieval | Room/SQLite first; embeddings later |
| :core:tools | Tool registry, capability declarations, policy checks and execution | Deny by default |
| :platform:android | Android-specific adapters: notifications, intents, contacts, calendar, files, accessibility only when explicitly enabled | Permission-gated |
| :feature:chat | Chat UI, streaming state, attachments and voice entry points | Compose |
| :feature:settings | Provider, privacy, permissions and runtime settings | Compose |

Modules may be introduced incrementally. Do not create empty modules just to imitate a diagram; extract a module when its contract and tests justify it.

## Five OpenJarvis-inspired primitives

### 1. Intelligence / Model Catalog
- Model metadata: provider, context size, modalities, quantization, storage footprint and capability flags.
- Device suitability checks before model download or activation.
- Explicit user-selected defaults with transparent fallback behavior.

### 2. Engine / Inference Runtime
- Common streaming inference contract.
- Local runtime adapter(s) selected after Android device profiling and compatibility tests.
- Optional cloud adapters behind the same contract.
- Cancellation, timeouts, structured errors and token/latency metrics.

### 3. Agents / Orchestration
- Simple chat agent first.
- Tool-using orchestrator next, with bounded steps and explicit execution states.
- Specialist agents (research, reminders, summaries) only after core tool policy is reliable.
- No autonomous background action without a user-configured policy and Android-compliant scheduling.

### 4. Tools / Android Capabilities
- Typed tool definitions with input/output schemas, capability IDs and risk levels.
- Registry and policy engine; deny unknown tools.
- Separate read-only, reversible and consequential actions.
- Confirmation UI for sending messages, creating events, deleting or modifying data, purchases, and external communication.
- Use official Android APIs and intents. AccessibilityService is not a general-purpose bypass and must only support a clear user-facing accessibility feature.

### 5. Memory / Learning
- Room-backed conversations and user-controlled memory.
- Searchable local notes and retrieval.
- Trace capture with sensitive-content minimization and retention controls.
- Learning initially means measurable routing/prompt improvements from opt-in local traces; no silent model training or hidden collection.

## Android execution constraints

- Background work: WorkManager for deferrable work; foreground services only for user-visible, policy-compliant ongoing tasks.
- Voice: Android SpeechRecognizer / on-device speech services when available, with a clear fallback; TextToSpeech for spoken responses.
- Notifications: runtime permission and user-configurable channels.
- Files: Storage Access Framework and scoped storage; never assume unrestricted filesystem access.
- Contacts/calendar: request narrowly scoped permissions only when the user invokes the relevant feature.
- Network: TLS, bounded requests, cancellation and explicit provider configuration.
- Secrets: Android Keystore-backed encryption; never commit API keys or put them in BuildConfig constants.

## Data and trust boundaries

- User content, retrieved documents, webpages, tool output and model output are untrusted data.
- Model output is a proposal, not authorization.
- Tool execution passes through schema validation, capability checks, risk policy, user confirmation when required, and an audit event.
- Logs must redact secrets and avoid storing raw sensitive content by default.
- Memory is inspectable, editable, exportable and deletable by the user.

## Delivery sequence

1. Establish a reproducible Android build, package identity, Compose shell, CI and unit tests.
2. Implement domain contracts and deterministic tests.
3. Implement local conversation storage and streaming chat UI.
4. Add provider-neutral inference and one verified local inference path.
5. Add optional cloud providers and model catalog.
6. Add the tool registry and safe, read-only Android tools.
7. Add user-confirmed write actions and permission education.
8. Add voice, attachments, retrieval, trace viewer and performance tuning.
9. Add specialist agents and opt-in learning after safety and reliability gates pass.

## Non-goals

- No root requirement.
- No hidden surveillance, credential extraction, or unrestricted control of other apps.
- No claim that every model can run locally on every Android device.
- No automatic sending, deletion, purchasing, or messaging without an explicit user-authorized flow.
- No direct dependency on OpenJarvis Python internals; reuse ideas and compatible open standards, not an unsuitable desktop runtime.
