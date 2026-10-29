# Notes App

A 2-screen note-taking Android app built with Jetpack Compose, following clean architecture with feature-layered modularization. Uses Koin for dependency injection.

## Architecture

```
:app                          ← Application, MainActivity, nav wiring
:build-logic                  ← Gradle convention plugins
:core:domain                  ← Result, Error, DataError, UiText (pure Kotlin)
:core:presentation            ← ObserveAsEvents, UiText extensions, DataError.toUiText()
:core:design-system           ← AppButton, AppTextField, AppCard, AppIcons, theme
:feature:notes:domain         ← Note, NoteError, NoteRepository interface
:feature:notes:data           ← Room DB, NoteDao, RoomNoteDataSource, repository impl
:feature:notes:presentation   ← ViewModels, State/Action/Event, screens, nav routes
```

## Dependency rules

- `presentation` → `domain`, `core:*`
- `data` → `domain`, `core:*`
- `domain` → `core:domain` only
- `:app` → everything

## Screens

1. **Note List** — shows all notes (LazyColumn), FAB to create, delete via icon, tap to edit.
2. **Note Editor** — create/edit note with title + content fields, save & delete, process-death restoration via `SavedStateHandle`.

## Build

Requires Android SDK and a JDK 17.

```bash
# Generate the Gradle wrapper (once, with Gradle installed)
gradle wrapper --gradle-version 8.5

# Assemble debug APK
./gradlew :app:assembleDebug

# Run unit tests
./gradlew :feature:notes:presentation:test
./gradlew :feature:notes:data:test
```

## Key patterns

- **MVI**: `State` / `Action` / `Event` (only when one-time side effects exist), `Root`/`Screen` composable split.
- **Result wrapper**: `Result<T, E>` with `DataError` and feature `NoteError`.
- **Koin**: per-layer modules (`notesDataModule`, `notesPresentationModule`) assembled in `NotesApplication`.
- **Type-safe navigation**: `@Serializable` route objects + feature nav graph.
- **Accessibility & responsiveness**: content descriptions, 48dp touch targets, font-scale & width previews, light/dark theme.
