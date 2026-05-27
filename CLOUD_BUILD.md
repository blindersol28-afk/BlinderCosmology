# Cloud Build via GitHub Actions

Build the APK on GitHub's servers — no Android Studio, no JDK, no SDK on your machine. You just need a GitHub account.

## One-time setup (~10 min)

### 1. Create a GitHub repo

- Go to https://github.com/new
- Name it anything (e.g., `blinder-cosmology`)
- Set it to **Private** if you don't want the code public
- Do **not** check "Initialize with README" — your local project already has files
- Click **Create repository**

### 2. Push this folder

GitHub will show you commands. From the `BlinderCosmology/` folder (not the parent), open a terminal and run:

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

`local.properties` (containing your Gemini API key) **will** be pushed — this project is configured to keep the key in source for build simplicity.

> If you've never used git on this machine, GitHub will prompt for credentials. Generate a personal access token at https://github.com/settings/tokens (classic, scope: `repo`) and use it as the password.

## Every build cycle (~3–5 min)

1. **Push** any change (or trigger manually): repo → **Actions** tab → "Build Debug APK" → **Run workflow** → **Run**
2. Wait for the green ✓ (usually 3–5 minutes — first run is slower because the Android SDK is downloaded)
3. Click into the completed run → scroll to **Artifacts** → download `blinder-cosmology-debug-apk` (a zip containing `app-debug.apk`)

## Side-loading the APK on your phone

1. Transfer `app-debug.apk` to your phone — email it to yourself, drop it in Drive/WhatsApp/Telegram, or USB it across.
2. On the phone: tap the APK in your file manager.
3. Android will say "For your security, your phone isn't allowed to install unknown apps from this source." Tap **Settings** → toggle **Allow from this source** on for whichever app you used (Files / Gmail / Drive / etc.). Back out.
4. Tap the APK again → **Install** → **Open**.

You'll get a Play Protect warning ("App not scanned") — that's normal for any app sideloaded outside the Play Store. Tap **Install anyway**.

## Troubleshooting

- **Build fails with "SDK location not found"** — make sure the `android-actions/setup-android@v3` step ran successfully. Check the workflow log.
- **Build fails on Kotlin compile** — paste the error here and I'll fix it.
- **APK installs but crashes on launch** — open `Settings → Apps → Blinder Cosmology → Storage → Clear cache` and try again. If still crashing, grab `adb logcat` output (requires USB debugging) or video the crash and I'll diagnose.
- **AI deepening not appearing** — confirm `local.properties` was pushed (it should NOT be in `.gitignore` anymore) and contains a non-empty `geminiApiKey=...` line.
