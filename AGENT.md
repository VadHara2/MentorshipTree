# AGENT.md — MentorshipTree

> A short, practical “runbook” for any human or AI agent working in this repo.
> Keep it concise, actionable, and up to date.

## 0) TL;DR
- **Project type:** Kotlin Multiplatform (KMP) + Compose Multiplatform + Firebase.
- **Primary module to build in headless/Codex:** `:shared` / `:composeApp` **without** Android target when SDK is absent.
- **Do not** configure Android where there is no SDK. Use the guard `hasAndroidSdk` (see Gradle snippet below).
- **Prod data:** Firestore + Authentication (Email/Google). Sensitive config via `.env`/`local.properties`.

## 1) Goals & Non‑Goals
**Goals**
- Implement a mentorship network: profiles, relations (mentor/mentee), requests, approvals, and messaging hooks.
- Keep reads/writes to Firestore minimal and rule‑safe.
- Shared business logic lives in KMP; platform UIs are thin.

**Non‑Goals**
- Native iOS build in CI that lacks Xcode.
- Heavy Android build steps in environments without Android SDK.

## 2) Architecture
- **Modules:**
  - `:composeApp` (Compose Multiplatform UI, may expose Android target conditionally)
  - `:shared` (if present; KMP domain/data)
- **Pattern:** MVI (State + Intent + Reducer) with coroutines/flows.
- **DI:** Koin/Hilt (depending on module); keep platform DI adapters thin.
- **Remote config:** `RemoteConfigRepository` for feature flags & constants.

## 3) Build Matrix
| Env | Android SDK | Kotlin/Native | Builds |
|---|---|---|---|
| Local Android dev | ✅ | optional | Full app |
| Codex/Headless CI | ❌ | ❌ (ignore iOS) | **KMP only** (no Android tasks) |
| iOS CI | Xcode required | ✅ | iOS artifacts |

**Gradle & MVI guard (must exist in Android/Compose modules):**
This ensures Android targets are only built when SDK is present, and illustrates how MVI pattern is enforced (intents → processor → reducer → publisher/repeater).
```kotlin
val hasAndroidSdk by lazy {
    providers.environmentVariable("ANDROID_HOME").isPresent ||
    providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
    file("${rootDir}/local.properties").exists()
}

kotlin {
    jvm()
    if (hasAndroidSdk) androidTarget() // do not enable if SDK is missing
}

if (hasAndroidSdk) {
    apply(plugin = "com.android.library")
    android { namespace = "<your.package.name>"; compileSdk = 34; defaultConfig { minSdk = 24 } }
}
```

**Gradle performance switches (`gradle.properties`):**
```
org.gradle.configuration-cache=true
org.gradle.caching=true
org.gradle.parallel=true
kotlin.native.ignoreDisabledTargets=true
kotlin.incremental=true
```

## 4) How to Run
### Local (Android)
1. Create `local.properties` with `sdk.dir=/path/to/Android/Sdk`.
2. Run from Android Studio: *Run* ➜ `composeApp`.

### Headless (Codex / no SDK)
```bash
./gradlew :composeApp:build -x test
# or, if modules split:
./gradlew :shared:build -x test
```
Should finish without trying to configure Android/iOS.

## 5) Firebase & Secrets
- **Files:** `google-services.json` (Android) / `GoogleService-Info.plist` (iOS).
- **Local dev:** keep these **out of the repo**. Place under platform modules. For CI that doesn’t ship, skip Google Services plugin.
- **Remote Config keys** used in code:
  - `features.relations.enabled` (bool)
  - `ui.maxItemsPerPage` (int)
  - *(extend as needed)*

## 6) Firestore Model (minimal contract)
```
/users/{uid}
  displayName: string
  photoUrl: string
  role: "mentor"|"mentee"|"both"
  createdAt: seconds

/users/{uid}/relations/{relationId}
  otherUid: string
  type: "mentor"|"mentee"
  status: "pending"|"active"|"blocked"
  updatedAt: seconds

/requests/{requestId}
  fromUid: string
  toUid: string
  note: string
  status: "pending"|"accepted"|"rejected"
  createdAt: seconds
```

### Security Rules principles
- Auth required for all reads.
- User can **write** only their own profile.
- Relation/Request writes are allowed only to involved users via custom checks.
- Prefer `allow read: if request.auth != null;` for profiles, stricter for subcollections.

## 7) Error Handling & Logging
- Wrap Firestore ops; map to domain errors: `PermissionDenied`, `NotFound`, `Network`, `Unknown`.
- Central `Logger` (expect logs but avoid PII). For Crashlytics, enable only on Android with SDK present.

## 8) UI/UX Conventions
- Material 3 Expressive for Compose.
- Snackbars via a single app‑level host, surfaced through helper utility.
- Time formatting: Telegram‑style (today, yesterday, else date).

## 9) Internationalization
- Strings must be in resources (no hardcoded UI strings). Lokalise integration via `LokaliseUtil`.
- All UI strings must be referenced via `Res.string.*` (JetBrains Compose Multiplatform resources). Direct literals in composables are forbidden.
- On system locale change, call `updateTranslations()`.

## 10) Testing
- Unit tests for reducers/use cases in `:shared`.
- Instrumentation tests only in Android module (skip in Codex/CI without SDK).

## 11) Branching & Releases
- **Branching:** trunk‑based (`main`) + short‑lived feature branches.
- **Versioning:** SemVer for shared library; app version via `versionCode`/`versionName`.
- **Release checklist:**
  - Update rules & verify with `firebase emulators:exec` (if used).
  - Bump version, update changelog.

## 12) Common Pitfalls (and fixes)
- **Gradle tries to build Android without SDK** → ensure `hasAndroidSdk` guard; keep Android plugins out of root.
- **iOS targets slow down CI** → `kotlin.native.ignoreDisabledTargets=true`.
- **Hardcoded strings** → move to resources; wire Lokalise.
- **Firestore PERMISSION_DENIED** → verify rules and that `uid` matches path.

## 13) Ready‑to‑Use Commands
```bash
# KMP build only (safe in Codex)
./gradlew build -x test

# Lint/format
./gradlew ktlintCheck detekt

# Run unit tests only
./gradlew :composeApp:testDebugUnitTest
```

## 14) Owner & Contacts
- Tech owner: <name> — <contact>
- Product owner: <name> — <contact>

## 15) To‑Do for This Doc
- [ ] Fill `<your.package.name>` and contacts
- [ ] Keep Firestore model & rules aligned with actual schema
- [ ] Add module diagram once stabilized