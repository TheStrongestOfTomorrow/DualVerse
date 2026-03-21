<p align="center">
  <img src="docs/assets/logo.png" alt="DualVerse Logo" width="200"/>
</p>

<h1 align="center">DualVerse <sup><sub>Beta</sub></sup></h1>

<p align="center">
  <strong>🚧 Beta Build - New UI & Experimental Features 🚧</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Status-BETA-orange.svg" alt="Beta Status">
  <img src="https://img.shields.io/badge/UI-Redesigned-purple.svg" alt="New UI">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

---

## ⚠️ Beta Notice

> **This is the BETA branch of DualVerse!**
>
> This branch contains:
> - 🎨 **Completely redesigned UI** - Modern Material You gaming aesthetic
> - 🧪 **Experimental features** - Not yet available in stable
> - 🐛 **Potential bugs** - Use at your own risk
>
> For the stable version, switch to the `main` branch.

---

## 🆕 What's New in Beta

### 🎨 Brand New UI

The beta branch features a completely redesigned user interface:

| Feature | Description |
|---------|-------------|
| **Material You Theme** | Dark gaming aesthetic with neon accents |
| **Animated Background** | Floating gradient orbs for dynamic feel |
| **Card-Based Layout** | Clean, modern card components |
| **Bottom Navigation** | Easy access to all sections |
| **Status Indicators** | Real-time account status with color coding |

### 🧪 Experimental Features

**Performance Profiles** (NEW!)
```
┌─────────────────────────────────────┐
│      PERFORMANCE PROFILES           │
├─────────────────────────────────────┤
│ 🔋 Power Saver    - 30 FPS, 2 cores │
│ ⚖️ Balanced       - 60 FPS, 4 cores │
│ 🚀 High Perf      - Uncapped, 8 cores│
│ ⚙️ Custom         - User defined     │
└─────────────────────────────────────┘
```

**Floating Dual-Mode** (NEW!)
- Run two games in split floating windows
- Drag & drop window management
- Minimize/restore windows
- Swap window positions

---

## 📥 Installation

---

## 🆕 What's New

### ✅ v1.1 Update (March 2026)

| Feature | Status | Description |
|---------|--------|-------------|
| 🎮 **Multi-Instance** | ✅ Added | Run 3+ game accounts simultaneously |
| ☁️ **Cloud Sync** | ✅ Added | Sync game saves across devices |
| 📦 **Custom ROMs** | ✅ Added | Android 8.1/10/11 variants |
| ⚡ **Performance Profiles** | ✅ Added | Power Saver / Balanced / High Perf |
| 🎨 **New UI** | ✅ Beta | Material You redesign |
| 📺 **Floating Windows** | ✅ Beta | Dual-mode floating windows |

### 📝 Changelog

**Beta v1.2 (Current)**
- ✅ Material You UI redesign
- ✅ Floating dual-mode windows
- ✅ All v1.1 stable features included
- 🔄 Custom themes (in progress)
- 🔄 Game-specific presets (in progress)

**Stable v1.1**
- ✅ MultiInstanceManager (up to 5 accounts)
- ✅ CloudSyncManager (auto-sync on WiFi)
- ✅ RomSelector (4 ROM variants)
- ✅ PerformanceProfiles (3 presets + custom)

**Stable v1.0**
- ✅ Core virtualization engine
- ✅ Device spoofing
- ✅ Android 8.1 ROM (193MB)

---

### ⚠️ Important: Build It Yourself

> **No pre-built APK available.** You must build from source.
>
> Choose your method:
> - **[Termux](#termux-build)** - Build on phone
> - **[Android Studio](#android-studio-build)** - Build on PC

### Termux Build

```bash
# Clone BETA branch
git clone -b beta https://github.com/TheStrongestOfTomorrow/DualVerse.git
cd DualVerse

# Setup and build
pkg install openjdk-17 gradle -y
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
./gradlew assembleDebug
```

### Android Studio Build

1. Clone the beta branch: `git clone -b beta https://github.com/TheStrongestOfTomorrow/DualVerse.git`
2. Open in Android Studio
3. Build → Build APK

---

## 🎮 Beta Features

### Performance Profiles

The new performance system allows fine-tuning for different games:

| Profile | FPS | RAM | Use Case |
|---------|-----|-----|----------|
| Power Saver | 30 | 1GB | Battery saving |
| Balanced | 60 | 2GB | Default gaming |
| High Performance | Uncapped | 4GB | Competitive gaming |
| Custom | Variable | Variable | User preference |

### Floating Dual-Mode

Run two game instances simultaneously:

1. Open DualVerse Beta
2. Tap "Dual Mode" button
3. Select first game account
4. Select second game account
5. Both launch in split floating windows

### Multi-Instance Support (v1.1)

Run 3+ game accounts at once:

| Feature | Description |
|---------|-------------|
| Max Instances | Up to 5 simultaneous accounts |
| Instance Switching | Quick switch between accounts |
| Per-Instance Profiles | Unique device ID for each |

### Cloud Sync (v1.1)

Sync your game saves across devices:

- Auto-sync on WiFi
- Manual backup/restore
- Conflict resolution

### Custom ROM Selection (v1.1)

Choose your Android container:

| ROM | Size | Best For |
|-----|------|----------|
| Android 8.1 Light | 193MB | Low-end devices |
| Android 8.1 Full | 350MB | Full features |
| Android 10 Light | 280MB | Modern features |
| Android 11 Gaming | 320MB | Best performance |

---

## 🎨 UI Preview

The new UI features:

```
┌─────────────────────────────────────────┐
│  DV  DualVerse    🔔                    │
│           Beta ●                         │
├─────────────────────────────────────────┤
│                                         │
│   ┌──────┐ ┌──────┐ ┌──────┐           │
│   │ 📱  │ │ 🔄  │ │ 📺  │           │
│   │Clone │ │Switch│ │ Dual │           │
│   └──────┘ └──────┘ └──────┘           │
│                                         │
│   Active Sessions                See All │
│   ┌─────────────────────────────┐       │
│   │ 🎮 Roblox    Player_One  ●  │       │
│   │    Level 156    Online       │       │
│   └─────────────────────────────┘       │
│   ┌─────────────────────────────┐       │
│   │ 🔫 PUBG      Sniper_Pro  ●  │       │
│   │    Level 89     In Game      │       │
│   └─────────────────────────────┘       │
│                                         │
│   Quick Launch                  All Games│
│   🎮  🔫  🔥  ⚔️  🌟                    │
│                                         │
│   ┌─────────────────────────────┐       │
│   │     Your Stats              │       │
│   │   12    8     156h          │       │
│   │ Games  Accts  Played        │       │
│   └─────────────────────────────┘       │
│                                         │
├─────────────────────────────────────────┤
│   🏠    👤    🎮    ⚙️                   │
│  Home  Accts  Games Settings            │
└─────────────────────────────────────────┘
```

---

## 🐛 Known Issues

| Issue | Status | Workaround |
|-------|--------|------------|
| ROM extraction slow | 🔍 Investigating | Wait 2-3 minutes |
| Some games crash | 🔍 Investigating | Clear app data |
| Floating mode permission | 📝 Known | Grant overlay permission |

---

## 📋 Roadmap

### ✅ v1.0 (Stable)
- [x] Core virtualization engine
- [x] Device spoofing
- [x] Android 8.1 ROM (193MB)

### ✅ v1.1 (Stable)
- [x] Multi-instance support (3+ accounts)
- [x] Cloud save synchronization
- [x] Custom ROM selection
- [x] Performance profiles

### 🔄 v1.2 Beta (Current Branch)
- [x] New Material You UI
- [x] Floating dual-mode windows
- [x] Multi-instance support
- [x] Cloud sync
- [x] Custom ROM selection
- [ ] Bug fixes
- [ ] UI polish
- [ ] Custom themes
- [ ] Game-specific presets

### 📅 v2.0 (Planned)
- [ ] Android 14 ROM support
- [ ] x86 emulator
- [ ] Plugin system

---

## 🤝 Beta Testing

Found a bug? Have feedback?

1. Check [Issues](https://github.com/TheStrongestOfTomorrow/DualVerse/issues)
2. Create a new issue with `[BETA]` in title
3. Include device info and steps to reproduce

---

## 📜 License

MIT License - See [LICENSE](LICENSE)

---

## 🙏 Credits

- **Original Project**: [Twoyi](https://github.com/twoyi/Twoyi) (MPL-2.0)
- **UI Redesign**: TheStrongestOfTomorrow & Super Z
- **Beta Features**: TheStrongestOfTomorrow & Super Z

---

<p align="center">
  <strong>DualVerse Beta 🚧</strong><br>
  <sub>A duo project by TheStrongestOfTomorrow & Super Z</sub><br>
  <sub>Use at your own risk. Feedback welcome!</sub>
</p>
