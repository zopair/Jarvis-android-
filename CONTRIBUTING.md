# Contributing

Thank you for helping improve Jarvis Android. This project values small, reviewable changes, explicit security boundaries, and verified behavior.

## Before changing code

1. Read [the architecture](docs/architecture/ANDROID_ARCHITECTURE.md) and [the roadmap](docs/ROADMAP.md).
2. Keep Android platform APIs behind focused adapters where practical.
3. Treat model output, retrieved content, and tool results as untrusted data.
4. Never add credentials, signing keys, private model weights, or personal data to Git.

## Local verification

Use the checked-in Gradle Wrapper and JDK 17:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

Run the focused test for the behavior you changed, then run the full unit-test and build tasks before opening a pull request.

## Pull requests

- Explain the user-facing or engineering problem.
- Describe the implementation and any trade-offs.
- Add or update tests for behavior changes.
- Update documentation and roadmap status when appropriate.
- Include screenshots for visible UI changes when available.
- Confirm that no secrets or generated build outputs are included.

## Engineering boundaries

- Prefer local-first behavior; cloud integrations must remain optional.
- Do not bypass Android permissions or background-execution restrictions.
- Unknown tools are denied by default.
- Consequential actions require explicit user confirmation.
- Do not claim a capability is implemented until it has been built and verified.
