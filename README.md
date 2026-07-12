# spring-boot-svelte-template

Fullstack project template: Java 25 + Spring Boot 4 backend, SvelteKit 2 (Svelte 5) SPA frontend
with Tailwind CSS 4, built as a single Gradle project.

## Prerequisites

- JDK 25 — must be installed locally (e.g. `brew install openjdk@25` or [Temurin 25](https://adoptium.net/temurin/releases/?version=25)).
- Node 24 for the Gradle build is downloaded automatically (node-gradle plugin).
- For frontend development only: your own Node 24+ and pnpm 10+
  (pnpm settings live in `web/pnpm-workspace.yaml`).

## Quick start

```bash
./gradlew bootRun             # backend + built frontend on :8080, `local` profile active
```

Health check: `GET /actuator/health` (actuator also exposes `info` and `metrics`).

Frontend development with HMR (backend must be running):

```bash
cd web
pnpm install
pnpm run dev                  # dev server on :5173, /api proxied to :8080
```

Environment variables:

- `SERVER_PORT` — backend port for `./gradlew bootRun` (default 8080)
- `APP_API_SERVER` — Vite proxy target for `/api` (default `http://localhost:8080`)

## Commands

```bash
./gradlew build               # full build: backend + frontend + tests
./gradlew test                # backend tests
./gradlew check               # build + tests

cd web
pnpm run lint                 # svelte-kit sync + prettier --check + eslint
pnpm run lint:fix             # svelte-kit sync + prettier --write + eslint --fix
pnpm run format                # prettier --write
pnpm run check                 # svelte-check
pnpm run test                  # vitest (node + browser projects) + playwright e2e
```

## Structure

- `src/main/java/dev/template/app/` — backend
  - `Application.java` — entry point
  - `config/WebConfig.java` — SPA fallback: serves `index.html` for unknown, non-API,
    extensionless routes so deep links into the SvelteKit app work on full page load
- `src/test/java/dev/template/app/` — `TestBase` (`@SpringBootTest`, random port, `test` profile),
  `ApplicationTest`, `config/WebConfigTest`
- `web/` — SvelteKit SPA (adapter-static, served by Spring Boot as static resources)
  - `src/lib/components/` — `counter.svelte`, `header.svelte` with matching `*.svelte.test.ts`
  - `src/routes/` — `+page.svelte`, `about/+page.svelte`
  - `src/routes/page.e2e.ts` — Playwright end-to-end test

## Conventions

- Component tests are named `*.svelte.test.ts` (run in a real browser via the vitest browser
  project); everything else `*.test.ts` runs in Node.
- Formatting is Prettier: tabs, single quotes, trailing commas, `printWidth` 120, LF line
  endings (`web/prettier.config.js`).
- ESLint (`web/eslint.config.js`, flat config) enforces the code style beyond formatting, e.g.:
  - imports are grouped and alphabetized (`import-x/order`), with `$lib/*` treated as internal
    and `$app/*` after external packages; unused imports/vars are auto-removed on `--fix`
    (prefix with `_` to keep them).
  - type-only imports use inline `type` syntax (`import { fn, type A }`).
  - `$lib/api` is a barrel — import from `$lib/api`, not its internal files.
  - Svelte blocks must be `<script lang="ts">`, buttons need an explicit `type`, and multi-line
    inline markup handlers should be extracted to named functions.
  - `eqeqeq`, `prefer-template`, `object-shorthand`, no nested ternaries, no `console.log`
    (only `warn`/`error`).

## License

[MIT](LICENSE)
