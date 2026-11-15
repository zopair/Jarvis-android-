# Security Policy

Jarvis Android is being developed with a deny-by-default security model. The project is not yet a production-ready AI assistant; the current implementation is a foundation and does not include a connected inference runtime.

## Reporting a vulnerability

Please do not publish exploitable details in a public issue.

If GitHub private vulnerability reporting is enabled for this repository, use the repository's **Security → Advisories** workflow. Otherwise, contact the repository maintainer through a private channel before disclosing details.

Include the affected commit or version, impact, reproduction steps, and any practical mitigation. Do not include real credentials, private user data, or live API keys in the report.

## Security expectations

- Model output is untrusted and is never authorization.
- Unknown tools and missing capabilities must be denied.
- External side effects and destructive actions require explicit confirmation.
- Secrets belong in secure local configuration or platform-backed storage, never in source control.
- Logs and traces must minimize sensitive content.
