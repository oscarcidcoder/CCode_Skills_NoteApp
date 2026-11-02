# NoteTake

A small, responsive note-taking app built with Jetpack Compose for `org.cidcode.notetake`.

- **Note list** — live list backed by Room, swipe a note to reveal delete, then confirm in a dialog.
- **Note editor** — create or edit a note, explicit save, draft survives process death.
- **Responsive** — single-pane navigation on compact/medium widths, list-detail two-pane on expanded
  (>= 840dp) widths. The open note is preserved when the window crosses the breakpoint.

## Tech stack

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

## Modules

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

Dependency rules: `presentation → domain`, `data → domain`, `domain → core:domain`; features never
depend on each other. `:app` wires everything together.

## Presentation pattern

Every screen is split into:

- `State` — a single data class.
- `Action` — a sealed interface of user intents.
- `Event` — only where a ViewModel-only side effect exists (`NoteEditorEvent.NoteSaved`). The list
  screen has no events: navigation happens directly from composable callbacks.
- `ViewModel` — exposes `StateFlow<State>`, handles actions, maps errors with `Result`.
- `<Screen>Root` — owns the ViewModel, collects state, observes events.
- `<Screen>Screen` — pure state + `onAction` + navigation callbacks, with previews for each state.

Form state in the editor is mirrored into `SavedStateHandle` so a draft survives process death.
The editor ViewModel is scoped to the host activity (rather than a navigation entry), so an
unsaved draft also survives compact <-> expanded transitions. Timestamps come from an injected
`java.time.Clock`, so save behavior is deterministic in tests.

## Data layer

`RoomNoteDataSource` is the only data source; a repository would be misleading because there is no
second source. Expected failures are returned as `Result.Error(DataError.Local...)`, never thrown,
with `SQLiteFullException` mapped to `DISK_FULL`. Room's `Flow` is the single source of truth, so
edits and deletions update the list automatically.

## Responsive behavior

`MainActivity` calculates a `WindowSizeClass`:

- **Compact / Medium** — a `NavHost` with `NoteListRoute` and `NoteEditorRoute`.
- **Expanded** — list pane (`38%`), vertical divider, editor/placeholder pane (`62%`).
  System back closes the detail pane.

The state of the open note is hoisted in `rememberSaveable`; a `LaunchedEffect` syncs the compact
back stack and the two-pane selection when the window crosses the breakpoint.

## Build and test

```bash
./gradlew :app:assembleDebug
./gradlew test
```

Unit test coverage (77 test executions across debug/release variants):

- `NoteValidatorTest` — blank/whitespace titles, exact and over-limit code points, emoji and ZWJ
  sequences, whitespace normalization.
- `NoteListViewModelTest` — mapping, empty state, failures, previous notes surviving a failure,
  retry resubscription, delete request/confirm/dismiss/failure, unknown note ids.
- `NoteEditorViewModelTest` — new/existing/missing notes, load failure, validation, title trimming,
  clock stamping, `NoteSaved`, failed save, double-save guard, draft preservation, process-death
  restoration, draft leak between consecutive new notes, `SavedStateHandle` writes.
- `NoteUiTest` — zone-aware date formatting, preview collapsing/trimming/truncation without
  splitting surrogate pairs.

## Lint note

This machine's Android SDK contains a stray `platforms/android-37.0` whose `source.properties`
declares `AndroidVersion.ApiLevel=37.0`; AGP 8.5 lint crashes parsing it (`For input string: "37.0"`).
Lint was run successfully against a curated temporary SDK root (`.sdk-temp/`, gitignored) containing
only platform 34 and build-tools 34. The app itself builds and tests against the normal SDK.
