# NoteTake — AGENTS.md

## Project Description

**NoteTake** is a small, responsive note-taking app built with Jetpack Compose for `org.cidcode.notetake`.

- **Note list** — live list backed by Room, swipe to reveal delete, confirm in a dialog.
- **Note editor** — create or edit a note, explicit save, draft survives process death.
- **Responsive** — single-pane navigation on compact/medium widths, list-detail two-pane on expanded (>= 840dp) widths. The open note is preserved when the window crosses the breakpoint.

**Tech Stack**
| Concern | Choice |
|---|---|
| UI | Jetpack Compose, Material 3, edge-to-edge |
| Architecture | Feature-layered modules + Clean Architecture layers, MVI presentation |
| DI | Hilt (`@HiltAndroidApp`, `@HiltViewModel`, Hilt modules) |
| Database | Room + KSP, single source of truth exposed as `Flow` |
| Navigation | Compose Navigation with type-safe `@Serializable` routes |
| Async | Coroutines + Flow |
| Build | Gradle 8.7, AGP 8.5, Kotlin 2.0.21, version catalog, convention plugins |
| Tests | JUnit 5, AssertK, Turbine, `UnconfinedTestDispatcher`, fakes |

`minSdk` 26, `compileSdk` / `targetSdk` 34.

---

## Module Structure & Dependency Rules

```
:app                          Application, MainActivity, adaptive NotesApp host
:build-logic                  Gradle convention plugins (android-library, compose, hilt, room, kotlin-module)
:core:domain                  Error, Result, EmptyResult, DataError, Result extensions
:core:presentation            UiText, ObserveAsEvents, DataError.toUiText()
:core:design-system           NoteTakeTheme, typography, colors, AppIcons
:feature:notes:domain         Note, NoteLocalDataSource contract, NoteValidator, NoteValidationError
:feature:notes:data           NoteEntity, NoteDao, NoteDatabase, RoomNoteDataSource, mappers, Hilt modules
:feature:notes:presentation   Routes, nav graph, list/editor MVI, components, UI mappers
```

**Dependency Rules** (enforced by convention plugins):
- `presentation → domain`
- `data → domain`
- `domain → core:domain`
- Features **never** depend on each other
- `:app` wires everything together

---

## Presentation Pattern (MVI)

Every screen is split into:
- `State` — a single data class.
- `Action` — a sealed interface of user intents.
- `Event` — only where a ViewModel-only side effect exists (`NoteEditorEvent.NoteSaved`). The list screen has no events: navigation happens directly from composable callbacks.
- `ViewModel` — exposes `StateFlow<State>`, handles actions, maps errors with `Result`.
- `<Screen>Root` — owns the ViewModel, collects state, observes events.
- `<Screen>Screen` — pure state + `onAction` + navigation callbacks, with previews for each state.

**Form state** in the editor is mirrored into `SavedStateHandle` so a draft survives process death. The editor ViewModel is scoped to the host activity (rather than a navigation entry), so an unsaved draft also survives compact ↔ expanded transitions. Timestamps come from an injected `java.time.Clock`, so save behavior is deterministic in tests.

---

## Data Layer

`RoomNoteDataSource` is the only data source; a repository would be misleading because there is no second source. Expected failures are returned as `Result.Error(DataError.Local...)`, never thrown, with `SQLiteFullException` mapped to `DISK_FULL`. Room's `Flow` is the single source of truth, so edits and deletions update the list automatically.

---

## Responsive Behavior

`MainActivity` calculates a `WindowSizeClass`:
- **Compact / Medium** — a `NavHost` with `NoteListRoute` and `NoteEditorRoute`.
- **Expanded** — list pane (`38%`), vertical divider, editor/placeholder pane (`62%`). System back closes the detail pane.

The state of the open note is hoisted in `rememberSaveable`; a `LaunchedEffect` syncs the compact back stack and the two-pane selection when the window crosses the breakpoint.

---

## Build & Test Commands

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Run all unit tests
./gradlew test
```

**Unit test coverage** (77 test executions across debug/release variants):
- `NoteValidatorTest` — blank/whitespace titles, exact and over-limit code points, emoji and ZWJ sequences, whitespace normalization.
- `NoteListViewModelTest` — mapping, empty state, failures, previous notes surviving a failure, retry resubscription, delete request/confirm/dismiss/failure, unknown note ids.
- `NoteEditorViewModelTest` — new/existing/missing notes, load failure, validation, title trimming, clock stamping, `NoteSaved`, failed save, double-save guard, draft preservation, process-death restoration, draft leak between consecutive new notes, `SavedStateHandle` writes.
- `NoteUiTest` — zone-aware date formatting, preview collapsing/trimming/truncation without splitting surrogate pairs.

---

## Code Style & Conventions

- **Kotlin** — Official style (`kotlin.code.style=official` in gradle.properties).
- **JVM target** — 17.
- **Encoding** — UTF-8.
- **Version catalog** — All dependency versions in `gradle/libs.versions.toml`.
- **Convention plugins** — In `:build-logic`; apply via `alias(libs.plugins.*)` in module `build.gradle.kts`.
- **Compose previews** — Every `Screen` composable has previews for each state.
- **String resources** — All user-facing strings in `strings.xml` (or `UiText` mappings for errors).
- **Icons** — Defined in `AppIcons.kt` as `Icon` composables wrapping `Icons.Default.*` with `contentDescription = null` (decorative) or string resource.
- **Colors/Typography** — Defined in `core:design-system` (`Color.kt`, `Type.kt`, `Theme.kt`); use `MaterialTheme.colorScheme` / `MaterialTheme.typography` in components.

---

## Boundaries & Limits (What NOT to Do)

| Boundary | Rule |
|---|---|
| **Cross-feature deps** | Never make `:feature:notes:*` depend on another feature module. |
| **Layer violations** | `domain` must not depend on `data` or `presentation`. `presentation` must not depend on `data`. |
| **DI scope** | ViewModels are `@HiltViewModel` scoped to Activity or NavBackStackEntry per screen; do not scope to Application unless explicitly needed. |
| **Error handling** | Never throw expected failures. Return `Result<T, DataError>`. Map to `UiText` only in `Root` composables. |
| **Navigation** | Use type-safe `@Serializable` route objects. No stringly-typed routes. |
| **Room** | Single database (`NoteDatabase`). DAO exposes `Flow<List<NoteEntity>>` as source of truth. No raw queries unless necessary. |
| **Process death** | Editor draft state lives in `SavedStateHandle`. Do not persist drafts to Room until explicit save. |
| **Lint SDK issue** | AGP 8.5 lint crashes on `platforms/android-37.0`. Lint runs against `.sdk-temp/` (gitignored) containing only platform 34. Do not commit `.sdk-temp/`. |
| **Min SDK** | 26. Do not use APIs requiring higher SDK without `@RequiresApi` guards. |
| **Test scope** | Unit tests are in-process, no I/O (no disk, network, DB, real clock). Use fakes, `UnconfinedTestDispatcher`, `Turbine`. |
| **Dependencies** | Do not add new dependencies without updating the version catalog and convention plugins if needed. |

---

## Key Files to Know

| File | Purpose |
|---|---|
| `app/src/main/kotlin/.../NotesApp.kt` | Adaptive host (two-pane / single-pane logic) |
| `app/src/main/kotlin/.../MainActivity.kt` | Entry point, edge-to-edge, window size class |
| `feature/notes/presentation/navigation/*.kt` | Type-safe routes, nav graph |
| `feature/notes/presentation/list/NoteListViewModel.kt` | List screen MVI |
| `feature/notes/presentation/editor/NoteEditorViewModel.kt` | Editor screen MVI |
| `feature/notes/data/RoomNoteDataSource.kt` | Single data source implementation |
| `core/domain/Result.kt` | `Result<T, E>` sealed class + extensions |
| `core/presentation/UiText.kt` | Error-to-string mapping |
| `core/design-system/theme/Theme.kt` | `NoteTakeTheme` composable |

---

## Common Tasks

### Add a new screen in Notes feature
1. Create route object in `feature:notes:presentation:navigation`.
2. Add to `notesGraph` in `NotesNavGraph.kt`.
3. Create `State`, `Action`, (optional) `Event`, `ViewModel`, `Root`, `Screen` in `presentation`.
4. Add previews for `Screen`.
5. Wire `Root` in `NotesApp.kt` (single-pane) or `TwoPaneNotes` (two-pane).

### Add a new feature module
1. Create `:feature:<name>:domain`, `:data`, `:presentation` modules.
2. Apply convention plugins (`android-library`, `compose`, `hilt`, `room` as needed).
3. Follow dependency rules: `presentation → domain`, `data → domain`, `domain → core:domain`.
4. Add Hilt modules in `data/di` and `presentation/di`.
5. Wire in `:app` `build.gradle.kts` and `NotesApp.kt`.

### Modify database schema
1. Update `NoteEntity` in `:feature:notes:data`.
2. Bump `NoteDatabase` version.
3. Add migration or `fallbackToDestructiveMigration` (dev only).
4. Update mappers in `NoteMappers.kt`.

---

## CI / Local Verification

```bash
# Full verification (build + test + lint against curated SDK)
./gradlew :app:assembleDebug test lint
```

> **Note**: Lint uses `.sdk-temp/` (gitignored) to avoid AGP 8.5 crash on stray platform 37.0. If lint fails, check `.sdk-temp/` exists with platform 34 only.

---

## Risk Areas

- **Window size transitions**: `NotesApp.kt` `LaunchedEffect` logic is subtle; test both directions (compact→expanded, expanded→compact) with open editor.
- **Process death**: Editor draft restoration via `SavedStateHandle` is tested but fragile; verify `SavedStateHandle` keys match exactly.
- **Hilt + Navigation compose**: ViewModel scopes must match navigation graph structure; scoping editor to Activity is intentional for two-pane draft survival.
- **Room Flow as source of truth**: Deletes/updates are automatic; do not add manual list refresh logic.
- **KSP + Hilt**: Regenerate code after schema changes (`./gradlew :feature:notes:data:kspKotlin`).