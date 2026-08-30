# ForgetMeNot

A Compose Multiplatform day view for yearly recurring events. See
`design/` for the design canvas sources.

## Working agreements

- **Git belongs to the developer.** Claude uses read-only git only — `log`,
  `status`, `diff`, `show`. Staging, committing, pushing, branching, remotes and
  config are run by the developer. Enforced by deny rules in
  `.claude/settings.json`; Claude writes commit messages but never commits.
- **Never install anything.** No package managers, no global tooling, and no new
  project dependencies — adding a line to `gradle/libs.versions.toml` or a
  `build.gradle.kts` counts as installing. Say what is needed and why; the
  developer applies it.
- **Agree the work before writing it.** Anything carrying a design decision gets
  settled in conversation first. Asking is preferred to guessing.
- **Push back.** If there is a better approach, say so rather than building the
  thing as asked without comment.

## Commands

`make` on its own lists the targets. `make run` launches the desktop app,
`make test` runs the tests. Gradle is the source of truth — the Makefile is only
aliases, so build behaviour belongs in `build.gradle.kts`, not there.
