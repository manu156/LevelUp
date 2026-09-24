# LevelUp

Cute anime-inspired daily work and focus tracking Android app built with Jetpack Compose. Designed to increase daily productive work hours through gamified session tracking, tactile anime physics interactions, and visual analytics.

---

## ⚡ Features & Flows

- **Work Tracking**: Check in with intent/task tags and category selection, track real-time focus sessions with ongoing foreground timer, check out with summary notes.
- **Analytics & Goals**: Daily breakdown with category distribution, weekly/monthly/yearly trend charts, streak counter, and daily/weekly goal progression.
- **GitHub Session Backup & Sync**: Cloud backup and synchronization via Git repository, partitioned JSON storage (`sessions/YYYY/MM/`), and merge conflict resolution.
- **Anime Feedback Engine**: Canvas-rendered, impact-delayed micro-interactions (Katana slash, Slime squash, Neko ear-twitch, Sakura floating overlay, Mahou sparkle burst, Comic panic shake).
- **Customization**: Chibi/anime avatar selection (preset or custom), dynamic color themes, and an in-app VFX/debug inspector in Settings.

---

## 🛠️ Technical Stack

- **Language & UI**: Kotlin 2.0+, Jetpack Compose (Anime inspiration + Material 3 Expressive)
- **Architecture**: Single-activity Compose architecture, repository pattern, unidirectional data flow (UDF)
- **Graphics & Animation**: Custom Compose `drawWithContent` / `Canvas` physics, `Animatable`, spring physics, infinite particle transitions
- **Storage & Sync**: Local JSON file persistence, Eclipse JGit for remote GitHub sync
- **Android Target**: Min SDK 26 (Android 8.0), Compile & Target SDK 36
- **Tooling**: Gradle Kotlin DSL, AndroidX lifecycle & activity Compose extensions

---

## 📁 Project Structure

```text
app/src/main/java/com/manu156/levelup/
├── MainActivity.kt               # App container, navigation modal state, onboarding
├── data/
│   ├── git/                      # Git repository synchronization, serializer & conflict handling
│   │   ├── GitSessionSerializer.kt
│   │   ├── GitSyncConfig.kt
│   │   └── GitSyncManager.kt
│   ├── model/FocusModels.kt      # Session, goal, insight, and streak models
│   └── repository/               # Local session persistence, stats calculations & Git bridge
├── service/
│   └── SessionForegroundService.kt # Background notification & ongoing session timer service
└── ui/
    ├── components/
    │   ├── AnimeCharts.kt        # Donut charts, capsule bar charts & circular gauges
    │   ├── AnimePhysics.kt       # Particle systems & anime feedback modifiers
    │   ├── AnimeVectorIcons.kt   # Vector icons (Cat, House, Target, Flame, Crown)
    │   ├── AnimeWidgets.kt       # Pill buttons, floating nav bar, cards, avatars
    │   └── GitConflictDialog.kt  # Side-by-side Git sync conflict resolution dialog
    ├── screens/                  # Home, CheckIn, ActiveSession, Stats, Goals, Profile, Settings, etc.
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

_README.md Last updated: September 23, 2026_
