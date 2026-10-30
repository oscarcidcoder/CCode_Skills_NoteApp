# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project Overview

**Notes App** — a 2-screen note-taking Android application built with Jetpack
Compose and Kotlin, following clean architecture with feature-layered
modularization. Dependency injection uses Koin, and persistence is offline-first
with Room.

### Screens

1. **Note List** — shows all notes in a `LazyColumn`; FAB to create, delete via
   trailing icon, tap to edit.
2. **Note Editor** — create/edit a note (title + content), save and delete,
   process-death restoration via `SavedStateHandle`.

## Tech Stack

- Kotlin **1.9.23**, Android Gradle Plugin **8.5.0**, JDK **17**
- `compileSdk` / `targetSdk` **34**, `minSdk` **24**
- Jetpack Compose (BOM **2024.06.00**, Material 3), Compose compiler **1.5.12**
- Navigation Compose **2.7.7** with type-safe `@Serializable` routes
- Room **2.6.1** (via KSP), kotlinx-serialization, kotlinx-datetime
- Koin **3.5.6**
- Testing: JUnit **5**, AssertK, Turbine, kotlinx-coroutines-test

## Module Structure

```
:app                          Application, MainActivity, nav wiring, Koin bootstrap
:build-logic                  Gradle convention plugins (com.example.buildlogic.*)
:core:domain                  Result, Error, DataError, UiText (pure Kotlin/JVM)
:core:presentation            ObserveAsEvents, UiText/DataError extensions
:core:design-system           App* components, AppIcons, theme (AppTheme, BrandColors)
:feature:notes:domain         Note, NoteError, NoteRepository interface
:feature:notes:data           Room DB, NoteDao, RoomNoteDataSource, repository impl
:feature:notes:presentation   ViewModels, State/Action/Event, screens, nav routes
```

All modules use the base package `com.example.notesapp`, with sub-packages
mirroring the Gradle path (e.g. `:core:design-system` →
`com.example.notesapp.core.designsystem`).

### Convention Plugins (`build-logic/convention`)

Modules should apply these rather than raw Android/Kotlin plugins:

| Plugin | Purpose |
| --- | --- |
| `com.example.buildlogic.android-application` | Android app module |
| `com.example.buildlogic.android-library` | Android library module |
| `com.example.buildlogic.compose` | Android library + Compose |
| `com.example.buildlogic.domain-module` | Pure Kotlin/JVM module (no Android) |
| `com.example.buildlogic.koin` | Adds Koin dependencies |
| `com.example.buildlogic.room` | Adds Room + KSP |

Versions and libraries are centralized in `gradle/libs.versions.toml` — never
hardcode version numbers in module `build.gradle.kts` files.

## Build & Test

Requires an Android SDK and JDK 17.

```bash
# Generate the Gradle wrapper (once, with Gradle installed)
gradle wrapper --gradle-version 8.5

# Assemble the debug APK
./gradlew :app:assembleDebug

# Run unit tests
./gradlew :feature:notes:presentation:test
./gradlew :feature:notes:data:test
```

## Dependency Rules

```
presentation → domain, core:*
data         → domain, core:*
domain       → core:domain only
:app         → everything
```

Enforce these strictly. Never introduce a reverse dependency (e.g. `domain`
must not depend on `data` or `presentation`).

## Architecture & Coding Conventions

### MVI (presentation layer)

Each screen follows a strict MVI shape:

- **State** — immutable `data class` (default values), exposed as a `StateFlow`
  (`_state` / `state`).
- **Action** — `sealed interface` of user intents (`NoteListAction`,
  `NoteEditorAction`); `data object` for events, `data class` for ones with
  payloads.
- **Event** — `sealed interface` for one-time side effects only, delivered via a
  `Channel` and consumed with `ObserveAsEvents`. Only define events when a
  one-time side effect actually exists (navigation, snackbar).
- **Root/Screen split** — `XxxRoot` holds ViewModel wiring (via
  `koinViewModel()`) and collects state; `XxxScreen` is a stateless composable
  that takes `state` + callbacks. Screens are what get `@Preview`s.

ViewModel naming: `NoteListViewModel`, `NoteEditorViewModel`, etc. Place each
screen in its own package under `...presentation/<screen>/`.

### Result & Error Handling

- Use `Result<T, E : Error>` / `EmptyResult<E>` from `:core:domain`; never throw
  exceptions for expected failures.
- `Error` is a marker interface. `DataError` (Network/Local) lives in
  `:core:domain`; feature-specific errors (e.g. `NoteError`) live in the
  feature's `domain` module.
- Map errors to UI strings via `toUiText()` extensions (`:core:presentation`
  handles `DataError`, the feature presentation module handles its own error
  enum). Use `UiText.getString(resources)` in composables.

### Repository / Data Layer

- Domain defines the `NoteRepository` interface; `data` provides the
  implementation (`OfflineFirstNoteRepository`) backed by a local data source
  (`NoteLocalDataSource` → `RoomNoteDataSource`).
- Room entities are prefixed `...Entity` (e.g. `NoteEntity`); DAO returns
  `Flow` for observable queries and `suspend` for one-shot operations.
- Mappers are top-level functions named `Xxx.toYyy()` in a `...Mappers.kt`
  file, converting between entity and domain models (epoch-millis ↔
  `kotlinx.datetime.Instant`).
- `Flow` sources map to `Result` and `catch` errors, emitting
  `Result.Error(...)` rather than letting the flow throw.

### Dependency Injection (Koin)

- One module per layer, declared as a top-level `val` in a
  `...Module.kt` file (e.g. `notesDataModule`, `notesPresentationModule`).
- Use Koin DSL helpers: `singleOf(...) { bind<Interface>() }`,
  `viewModelOf(::XxxViewModel)`, `viewModel { params -> ... }`.
- Assemble modules in `NotesApplication` via `startKoin { modules(...) }`.
- Inject ViewModels in composables with `koinViewModel()` (and
  `parametersOf(...)` for parameters).

### Navigation

- Type-safe routes: `@Serializable` objects/data classes in `NotesRoutes.kt`
  (`NoteListRoute`, `NoteEditorRoute(noteId)`).
- Feature-level nav graph: a `NavGraphBuilder.xxxGraph(...)` extension in
  `NotesNavGraph.kt`, wired into the app-level `NavHost` in `MainActivity`.

### Compose / Design System

- Reuse components from `:core:design-system` (`AppButton`, `AppTextField`,
  `AppCard`, `AppIconButton`, `AppFilledTonalIconButton`, `AppTopAppBar`,
  `AppEmptyState`). Do not use raw Material 3 widgets where an `App*` equivalent
  exists.
- Icons come from the `AppIcons` object (vector drawables in
  `core/design-system/src/main/res/drawable/`). Use `AppIcons.X`, not
  `painterResource(R.drawable...)` directly.
- Colors come from `MaterialTheme.colorScheme` (defined in `AppTheme` /
  `BrandColors`). Never hardcode hex colors in composables.
- Every interactive/meaningful icon and image needs a `contentDescription`
  string resource (prefixed `cd_` in the design-system strings).
- All user-visible strings must be string resources (`strings.xml` in the owning
  module), never hardcoded literals.
- Previews are required per visual state — cover empty/loading/data/error
  states, and include dark-mode (`uiMode = UI_MODE_NIGHT_YES`), large
  `fontScale` (e.g. `2f`), and wide (`widthDp`) variants.

### Testing

- JUnit 5 + AssertK + Turbine. Prefer hand-written **fakes** (e.g.
  `FakeNoteRepository`) over mocking libraries.
- ViewModel tests use `UnconfinedTestDispatcher` with `Dispatchers.setMain` /
  `resetMain` in `@BeforeEach` / `@AfterEach`, and `runTest`.
- Unit tests are pure in-memory (no disk, network, DB, or real clock).
- Test names use backtick sentences describing behavior. Cover unicode/emoji,
  boundary, and empty-input cases (see `NoteMappersTest`).

## Project Skills

Authoritative, detailed guidance for specific tasks lives in `.agents/skills/`.
Consult the relevant skill before doing that kind of work:

- `android-compose-components` — writing individual Compose components
- `android-data-layer` — data sources, repositories, DTOs/entities, mappers
- `android-di-koin` — Koin module setup and injection
- `android-error-handling` — `Result` wrapper and error types
- `android-module-structure` — module layout and Gradle convention plugins
- `android-navigation` — type-safe navigation and nav graphs
- `android-presentation-mvi` — ViewModels, State/Action/Event, screens
- `android-unit-testing` — test methodology and tooling

## Repository Safety

`.agents/extensions/confirm-delete.ts` blocks destructive `rm` commands from
running without explicit confirmation. Do not bypass this guard.
