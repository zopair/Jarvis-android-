# Upstream alignment and Android adaptation

Jarvis Android is based on the product architecture and design principles of [OpenJarvis](https://github.com/open-jarvis/OpenJarvis), while remaining a native Android implementation.

## Upstream license and provenance

- Upstream repository: https://github.com/open-jarvis/OpenJarvis
- Upstream license: Apache License 2.0 (verified from the upstream repository metadata and LICENSE file).
- This project adapts architectural concepts and documented interfaces; it does not vendor the Python/server runtime or claim that those runtime components execute on Android.
- Any future copied source files must retain their upstream copyright and license notices and be recorded in `THIRD_PARTY_NOTICES/`.

## Android mapping

| OpenJarvis primitive | Android implementation boundary |
|---|---|
| Intelligence / model catalog | Device-aware model metadata, local model compatibility and provider-neutral routing |
| Engine | Local GGUF runtime adapter plus optional OpenAI-compatible HTTPS adapter |
| Agents | Bounded orchestration layer; no autonomous execution until policy and approval gates are implemented |
| Tools | Android capability adapters behind deny-by-default authorization and explicit confirmation |
| Memory & Learning | App-private conversation history first; user-controlled memory and inspectable traces are later milestones |

## Reuse policy

Prefer Apache-2.0, MIT, BSD-2-Clause and BSD-3-Clause components when their Android runtime, transitive dependencies, maintenance state and security posture are suitable. Verify each dependency's exact license and version before integration. Do not assume a repository being public on GitHub makes its code, model weights, datasets or artwork license-free.

Free hosted inference is not a guaranteed production dependency: quotas, API keys, availability and terms can change. The default product path is on-device inference; cloud providers remain optional and user-configured.

## Original visual identity

The animated chrome-core artwork in `app/src/main/res/raw/jarvis_core.gif` was generated specifically for Jarvis Android. It was not edited from the user-supplied reference GIF and does not reuse its frames. The reference image's license was not established, so it is not redistributed.
