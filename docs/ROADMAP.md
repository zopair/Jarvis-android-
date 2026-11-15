# Jarvis Android Roadmap

This roadmap turns the OpenJarvis-inspired architecture into a shippable Android product. A phase is complete only when its acceptance criteria are met on a real Android device or emulator.

## Phase 0 — Repository and build foundation
- [ ] Establish native Android project structure and package identity.
- [ ] Pin Android Gradle Plugin, Gradle, Kotlin and dependency versions.
- [ ] Add debug/release build variants and baseline CI.
- [ ] Add unit-test conventions, lint and dependency verification.
- [ ] Document supported Android versions, build prerequisites and setup.

**Exit gate:** clean checkout can sync, assemble, lint and run unit tests in CI.

## Phase 1 — App shell and domain contracts
- [ ] Compose navigation and responsive chat shell.
- [ ] Domain models for conversation, message, model, tool, task and trace.
- [ ] Repository interfaces and deterministic use-case tests.
- [ ] Settings and permission education screens.

**Exit gate:** app launches reliably; domain behavior is tested independently of Android UI.

## Phase 2 — Local-first chat
- [ ] Room persistence and migration strategy.
- [ ] Conversation list, message history and streaming UI states.
- [ ] Cancellation, retry, offline and provider-error states.
- [ ] Export/delete conversation controls.

**Exit gate:** conversations persist across process restarts and work without a network connection.

## Phase 3 — Inference and model catalog
- [ ] Provider-neutral streaming inference interface.
- [ ] Device capability profile (RAM, storage, ABI, thermal/battery signals).
- [ ] Verify a compatible local runtime and model on representative devices.
- [ ] Optional cloud provider adapters and secure credential storage.
- [ ] Model download lifecycle, integrity checks and storage management.

**Exit gate:** model selection is explainable, local inference is demonstrated on target hardware, and cloud use is opt-in.

## Phase 4 — Agent and safe tool execution
- [ ] Bounded agent loop with cancellation and step limits.
- [ ] Typed tool registry and capability manifests.
- [ ] Policy engine with deny-by-default behavior.
- [ ] Read-only Android tools before write-capable tools.
- [ ] Confirmation flows and audit history for consequential actions.

**Exit gate:** tests prove unknown tools are denied, invalid arguments are rejected, and protected actions cannot run without authorization.

## Phase 5 — Voice and multimodal
- [ ] Speech-to-text with explicit runtime availability checks.
- [ ] Text-to-speech with interruption and playback controls.
- [ ] Image/document attachment pipeline with size/type validation.
- [ ] Clear privacy controls for microphone and media access.

**Exit gate:** voice and attachment flows work with denied permissions and offline/provider failure states.

## Phase 6 — Memory, retrieval and learning
- [ ] User-controlled memory records and local search.
- [ ] Optional embedding index after baseline search is measured.
- [ ] Trace viewer and opt-in telemetry.
- [ ] Evaluation datasets for latency, quality, battery and failure rate.
- [ ] Evidence-based routing improvements; no silent training.

**Exit gate:** users can inspect, edit, export and delete memory and traces.

## Phase 7 — Release hardening
- [ ] Threat model and privacy review.
- [ ] Accessibility and localization review (Arabic/English).
- [ ] Battery, thermal, memory and startup profiling.
- [ ] Offline and low-end device test matrix.
- [ ] Signed release workflow and reproducible release notes.

**Exit gate:** release checklist passes and known limitations are documented.

## Priority order

Reliability and safe boundaries precede feature breadth. Do not add autonomous app control, background agents, or large local models until permission policy, cancellation, resource limits and user-visible execution state are proven.
