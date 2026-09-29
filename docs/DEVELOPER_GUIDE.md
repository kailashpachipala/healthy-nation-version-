# Developer Guide - Healthy Nation

This guide instructs new engineers on building, running, and extending the Healthy Nation codebase.

## Prerequisites

- Android Studio Ladybug / Meerkat or later
- JDK 17 or JDK 21
- Android SDK Platform 36 (minSdk 24, targetSdk 36)
- Gradle 8.11+ / Android Gradle Plugin 9.1.1

## Setup & Running the Application

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd healthynation
   ```

2. **Configure API Keys:**
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   Add your Gemini API key in `.env`:
   ```env
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
   *Note*: The Secrets Gradle Plugin automatically reads `.env` and exposes `BuildConfig.GEMINI_API_KEY`. If no key is provided, the application automatically falls back to the deterministic local rule-based clinical engine without crashing.

3. **Build and Install:**
   ```bash
   gradle assembleDebug
   ```

4. **Run JVM Tests:**
   ```bash
   gradle :app:testDebugUnitTest
   ```

## Adding a New Healthcare Feature

1. **Entity Definition**: Add `@Entity` in `com.example.data.local.Entities.kt`.
2. **DAO Contract**: Declare reactive queries returning `Flow<List<T>>` in `com.example.data.local.Daos.kt`.
3. **Database Registration**: Add entity class to `@Database(entities = [...])` in `HealthyNationDatabase.kt`.
4. **Repository Method**: Expose clean data operations in `HealthyNationRepository.kt`.
5. **ViewModel State**: Connect through `viewModelScope` and `StateFlow` in `HealthyNationViewModel.kt`.
6. **UI Composable**: Build Jetpack Compose screen in `com.example.ui.screens.<feature>/` utilizing reusable tokens from `com.example.components.HNComponents`.
