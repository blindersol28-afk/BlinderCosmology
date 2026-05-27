# Blinder Cosmology

Android app — natal-chart astrology + numerology with a Peaky Blinders sepia/noir aesthetic.

## What's in it

- **Astronomy engine** in pure Kotlin: Julian Day, Sun/Moon, all 8 planets (Mercury → Pluto) via iteratively-solved Kepler equation, Ascendant + Midheaven from local sidereal time, Placidus house cusps, mean lunar node, aspect detection with standard orbs.
- **Numerology**: Life Path, Expression, Soul Urge, Personality, Personal Year + Personal Day.
- **Interpretation engine**: rule-based reading combining sign × house × aspect into seven sections — personality, life mission, relationships, money, career, health, numerology.
- **Daily horoscope**: transits-to-natal calculation against today's sky, plus the moon-sign mood and a personal-day number.
- **Two user modes**:
  - **Main user** — birth details saved once (Room DB), opens daily horoscope.
  - **Guest reading** — one-off chart, nothing stored.
- **Chart wheel**: a Compose Canvas drawing with zodiac slices (color-coded by element), degree ticks, planet glyphs with anti-overlap clustering, house cusps, ASC/DSC/MC/IC axis labels, and inner aspect lines.

## Build

### Easiest: Android Studio
1. Open Android Studio (Hedgehog 2023.1+).
2. **File → Open** and pick the `BlinderCosmology/` folder.
3. Studio will download the Gradle wrapper, SDK platform-34 if needed, and sync.
4. Plug in an Android phone (USB debugging on) or start an emulator.
5. Hit **Run** ▶.

### Command line
You need a JDK 17 and Android SDK with platform-34 + build-tools 34. Then from `BlinderCosmology/`:

```bash
# First time only — generate the Gradle wrapper jar
gradle wrapper --gradle-version 8.9

# Then
./gradlew assembleDebug         # APK at app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug          # install on a connected device
```

If you don't have `gradle` on your PATH, install [Gradle 8.9+](https://gradle.org/install/) or use Android Studio (which manages the wrapper for you).

## Architecture

```
app/src/main/java/com/blindercosmology/
├── MainActivity.kt           # ComponentActivity entry
├── BlinderApp.kt             # Application — instantiates Room + repo
├── astro/                    # Pure-Kotlin astronomy engine (no Android deps)
│   ├── Math.kt               # trig helpers, angle normalization
│   ├── JulianDay.kt          # JD, GMST, LST, obliquity
│   ├── Sun.kt                # Sun longitude (Meeus ch. 25)
│   ├── Moon.kt               # Moon longitude (truncated ELP, top terms)
│   ├── Planets.kt            # All 8 planets, Kepler solver, Earth-relative
│   ├── Houses.kt             # ASC, MC, Placidus cusps (iterative)
│   ├── Aspects.kt            # Aspect type defs + detection
│   ├── Zodiac.kt             # Sign + Body enums, glyphs, elements
│   ├── Chart.kt              # Top-level NatalChart builder
│   └── Transits.kt           # Daily transit positions + transits-to-natal
├── numerology/Numerology.kt  # Life path, expression, soul urge, etc.
├── interpret/
│   ├── Library.kt            # All sign×body, body-house, aspect text
│   ├── Reading.kt            # Stitches placements into a 7-section reading
│   └── DailyHoroscope.kt     # Builds today's reading
├── data/                     # Room: ProfileEntity, DAO, DB, Repository
└── ui/
    ├── theme/                # Peaky Blinders palette (sepia/smoke/brass/blood)
    ├── nav/                  # Navigation graph
    ├── screens/              # Home, Input, Chart (4 tabs), Daily
    └── components/           # ChartWheel canvas, PaintingHeader
```

## Accuracy notes

The astronomy engine targets **0.1°–0.5° geocentric ecliptic accuracy** for planets (well inside any astrological orb). Pluto is the loosest at the edges of the 1800–2050 validity window of the Standish element set. The Moon uses the top ~19 ELP-2000 periodic terms — adequate for sign + house placement.

Placidus houses are computed iteratively. Above latitude ~66° N/S the system is geometrically undefined and falls back to equal house (warning: this happens silently).

## AI deepening (Gemini)

The interpretation engine is two-layered:

1. **Rule-based engine** (always on, offline-capable) — produces the structured reading from sign × house × aspect.
2. **Gemini layer** (optional, online) — takes each rule-based section + the raw chart placements + tight aspects, and writes a deeper explanation grounded in the actual chart, with two or three pieces of actionable advice per section.

The AI is *grounded* in the rule-based reading and the actual placements — it explains the why and adds nuance, rather than inventing fresh astrology. If the AI call fails or the key isn't configured, the rule-based reading still works on its own.

### Setup

1. Copy `local.properties.example` → `local.properties` (in the project root).
2. Replace `YOUR_GEMINI_API_KEY_HERE` with your real Google AI Studio key.
3. Make sure the **Generative Language API** is enabled for your Google Cloud project.
4. **Restrict the key**: Cloud Console → APIs & Services → Credentials → your key → Application restrictions → Android apps → add SHA-1 fingerprint of your signing key + package name `com.blindercosmology`. Without restrictions, a leaked key is a blank check.

`local.properties` is in `.gitignore` — never commit it.

### Files

- `ai/GeminiClient.kt` — minimal HttpURLConnection client, no extra deps.
- `ai/ReadingExpander.kt` — section-aware prompt builder; serializes the chart and asks Gemini to expand each section.
- `ui/screens/ChartScreen.kt` — Full Reading tab fires off all 7 section expansions in parallel and renders them below each rule-based section with loading / loaded / error states.

### Costs / rate

Each open of the Full Reading tab makes 7 parallel calls (one per section). On `gemini-1.5-flash` the free tier gives you generous daily quota, but if you're worried, change `auto-trigger` to manual ("Deepen" button per section) in `ReadingTab.LaunchedEffect`. The model is configurable in `GeminiClient`'s constructor — swap to `gemini-1.5-pro` for higher quality at higher cost, or `gemini-2.0-flash-exp` for the newest fast tier.

## License

MIT — do as you please.
