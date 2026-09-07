# LevelUp

Cute anime-inspired daily work and focus tracking Android app built with Jetpack Compose. Designed to increase daily productive work hours through gamified session tracking, tactile anime physics interactions, and visual analytics.

---

## ⚡ Features & Flows

- **Work Tracking**: Check in with intent/task tags, track real-time focus sessions, check out with summary notes.
- **Analytics & Goals**: Daily breakdown, weekly trend charts, streak counter, and goal progression.
- **Anime Feedback Engine**: Canvas-rendered, impact-delayed micro-interactions (Katana slash, Slime squash, Neko ear-twitch, Sakura quest stamp, Mahou sparkle burst, Comic panic shake).
- **Customization**: Chibi/anime avatar selection (preset or custom), dynamic color themes, and an in-app VFX/debug inspector in Settings.

---

## 🛠️ Technical Stack

- **Language & UI**: Kotlin 2.0+, Jetpack Compose (Anime inspiration + Material 3 Expressive)
- **Architecture**: Single-activity Compose architecture, repository pattern, unidirectional data flow (UDF)
- **Graphics & Animation**: Custom Compose `drawWithContent` / `Canvas` physics, `Animatable`, spring physics, infinite particle transitions
- **Android Target**: Min SDK 26 (Android 8.0), Compile & Target SDK 36
- **Tooling**: Gradle Kotlin DSL, AndroidX lifecycle & activity Compose extensions

---

## 📁 Project Structure

```text
app/src/main/java/com/manu156/levelup/
├── MainActivity.kt               # App container, navigation state, onboarding
├── data/
│   ├── model/FocusModels.kt      # Session, goal, insight, and streak models
│   └── repository/               # Local session persistence & mock generators
└── ui/
    ├── components/
    │   ├── AnimePhysics.kt       # Particle systems & anime feedback modifiers
    │   └── AnimeWidgets.kt       # Pill buttons, floating nav bar, cards, avatars
    ├── screens/                  # Home, CheckIn, ActiveSession, Stats, Goals, Profile, Settings
    └── theme/                    # Material 3 Expressive palettes, typography, theme manager
```

---

## 🚀 Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test
```

---

_README.md Last updated: September 7, 2026 19:04_
