# ForgetMeNot

A Compose Multiplatform day view for yearly recurring events. See
`design/` for the design canvas sources.

## Working agreements

- **Git belongs to the developer.** Claude uses read-only git only — `log`,
  `status`, `diff`, `show`. Staging, committing, pushing, branching, remotes and
  config are run by the developer. Enforced by deny rules in
  `.claude/settings.json`; Claude writes commit messages but never commits.
- **Never install anything.** No package managers, no global tooling, no fetching
  artifacts. A new project dependency is the developer's decision: Claude
  proposes it with reasons, and edits `gradle/libs.versions.toml` /
  `build.gradle.kts` only when asked to. Resolving it — running the build that
  downloads it — is always the developer's.
- **Agree the work before writing it.** Anything carrying a design decision gets
  settled in conversation first. Asking is preferred to guessing.
- **Push back.** If there is a better approach, say so rather than building the
  thing as asked without comment.

## Commands

`make` on its own lists the targets. `make run` launches the desktop app,
`make test` runs the tests. Gradle is the source of truth — the Makefile is only
aliases, so build behaviour belongs in `build.gradle.kts`, not there.

### Desktop UI scale

Compose Desktop takes its density from the AWT graphics transform, which is the
identity matrix under XWayland — so on a fractionally-scaled Linux desktop the
app renders at 1.0 while every other window is scaled, and looks about 30% too
small. It therefore prefers the scale the JVM detects (`sun.java2d.uiScale`), and
prints a line at startup whenever it is correcting the platform's own value.

Override it when the detected scale is wrong:

    FORGETMENOT_UI_SCALE=1.6 make run

Values outside 0.5–4.0 are ignored so a typo cannot leave the window unreadable.
The scale is read once at startup, so moving the window to a differently-scaled
monitor needs a restart. Desktop only — Android and iOS get density right natively.
