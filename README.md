# Jarvis Android

**A local-first personal AI assistant for Android.**

Jarvis Android brings the composable ideas behind [OpenJarvis](https://github.com/open-jarvis/OpenJarvis) to native Android: on-device intelligence where practical, optional cloud inference, bounded agents, permission-aware Android tools, user-controlled memory, and measurable execution traces.

> This repository is the Android-native implementation. It is not a direct port of the OpenJarvis Python/server runtime.

## Product goals

- **Local-first:** prioritize on-device inference and local persistence; cloud providers are optional.
- **Provider-neutral:** keep model and inference integrations behind stable interfaces.
- **Agentic, but controlled:** agents plan and propose actions; a policy layer authorizes tools.
- **Android-native:** use platform APIs for voice, notifications, files and app integration.
- **Private by design:** user-controlled memory, explicit permissions, secure secret storage and inspectable traces.
- **Resource-aware:** account for RAM, storage, battery, thermal state, ABI and network availability.

## Architecture

The design follows five OpenJarvis-inspired primitives:

| Primitive | Android responsibility |
|---|---|
| Intelligence | Model catalog, capabilities and device-aware selection |
| Engine | Local and cloud inference adapters with streaming/cancellation |
| Agents | Simple chat and bounded tool-using orchestration |
| Tools | Typed, permission-gated Android capabilities and policy checks |
| Memory & Learning | Local conversations, user-controlled memory, traces and evaluation |

See [Android Architecture](docs/architecture/ANDROID_ARCHITECTURE.md) for module boundaries, trust model and Android execution constraints.

## Roadmap

See [the phased roadmap](docs/ROADMAP.md). The project prioritizes a reproducible Android build, domain contracts and safe local chat before adding advanced agents or autonomous device actions.

## Security principles

- Model output is never treated as authorization.
- Unknown tools are denied by default.
- Consequential actions require explicit user approval.
- No unrestricted shell execution or hidden accessibility automation.
- No API keys or credentials committed to source control.
- Memory and traces must be inspectable and user-controllable.

## Development status

The repository is establishing its architecture and delivery baseline. Build setup and feature implementation will be added in staged, testable increments. No APK or working on-device inference runtime is claimed until it has been built and verified.

## Reference project

- [OpenJarvis source](https://github.com/open-jarvis/OpenJarvis)
- [OpenJarvis architecture overview](https://github.com/open-jarvis/OpenJarvis/blob/main/docs/architecture/overview.md)
- [OpenJarvis documentation](https://open-jarvis.github.io/OpenJarvis/)

## License

The Android implementation's license will be declared when the source baseline is established. OpenJarvis is separately licensed under Apache-2.0; this repository does not imply ownership of or a change to upstream licensing.
