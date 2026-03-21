<p align="center">
  <img src="docs/assets/logo.png" alt="DualVerse Logo" width="200"/>
</p>

<h1 align="center">DualVerse</h1>

<p align="center">
  <strong>Revolutionary Multi-Account Gaming Through Lightweight Android Virtualization</strong>
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#how-it-works">How It Works</a> •
  <a href="#installation">Installation</a> •
  <a href="#-termux-support-no-pc-needed">Termux</a> •
  <a href="#architecture">Architecture</a> •
  <a href="#license">License</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/API-24%2B-brightgreen.svg" alt="API Level">
  <img src="https://img.shields.io/badge/Size-~200MB%20ROM-blue.svg" alt="ROM Size">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

---

## 🎮 Overview

**DualVerse** is a groundbreaking Android application that enables users to game with **two accounts simultaneously** without the limitations of traditional multiple-account solutions. Unlike older apps that suffered from patches, detection, or required root access (which apps like Roblox actively block), DualVerse uses an innovative **containerized virtualization approach**.

### The Problem

Traditional multi-account solutions face several critical issues:

- **Root Detection**: Apps like Roblox, PUBG Mobile, and others actively block rooted devices
- **Patches & Updates**: Game developers regularly patch workarounds
- **Account Bans**: Detection leads to permanent account suspension
- **Performance Issues**: Traditional emulators are resource-heavy
- **Complexity**: Existing solutions require technical knowledge

### The DualVerse Solution

DualVerse creates a **completely isolated virtual Android environment** inside your device:

1. **No Root Required** - Works on any unrooted device
2. **Undetectable** - The virtualized environment appears as a completely separate device
3. **Lightweight** - Only ~200MB Android 8.1 container ROM
4. **High Performance** - Native-speed execution through optimized virtualization
5. **Simple UI** - One-tap account switching and management

---

## ✨ Features

### 🔄 Dual Account Management
- **Simultaneous Operation**: Run two game instances side-by-side
- **Quick Switch**: Instantly switch between accounts
- **Account Cloning**: Clone any installed app with one tap
- **Independent Data**: Each account has completely separate data and cache

### 🛡️ Advanced Security
- **Device Spoofing**: Unique device identifiers for each virtual instance
- **MAC Address Randomization**: Network-level privacy protection
- **Anti-Detection Bypass**: Undetectable by anti-cheat systems
- **Sandbox Isolation**: Complete separation between accounts

### ⚡ Performance Optimized
- **200MB Custom ROM**: Minimal storage footprint
- **Memory Optimization**: Smart RAM management for dual instances
- **GPU Acceleration**: Hardware graphics rendering support
- **Battery Efficient**: Optimized power consumption algorithms

### 🎯 Supported Games
| Game | Status | Notes |
|------|--------|-------|
| Roblox | ✅ Full Support | Bypasses root detection completely |
| PUBG Mobile | ✅ Full Support | Anti-cheat bypass enabled |
| Free Fire | ✅ Full Support | Works on all server regions |
| Mobile Legends | ✅ Full Support | No account linking issues |
| Genshin Impact | ✅ Full Support | HoYoverse account compatible |
| COD Mobile | ✅ Full Support | Activision account support |
| Clash of Clans | ✅ Full Support | Supercell ID compatible |
| Generic Apps | ✅ Full Support | Works with any Android app |

---

## 🔧 How It Works

```
┌─────────────────────────────────────────────────────────────┐
│                    DUALVERSE ARCHITECTURE                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────────┐      ┌─────────────────────┐      │
│  │   HOST ANDROID      │      │  VIRTUAL ANDROID    │      │
│  │   (Your Device)     │      │  (200MB Container)  │      │
│  ├─────────────────────┤      ├─────────────────────┤      │
│  │ • Primary Account   │      │ • Secondary Account  │      │
│  │ • Original Apps     │      │ • Cloned Apps        │      │
│  │ • Real Device ID    │      │ • Virtual Device ID  │      │
│  └─────────────────────┘      └─────────────────────┘      │
│            │                            │                    │
│            │    ┌──────────────┐        │                    │
│            └────│  DUALVERSE   │────────┘                    │
│                 │   ENGINE     │                             │
│                 ├──────────────┤                             │
│                 │ • Virtualization Manager                   │
│                 │ • Device ID Spoofer                        │
│                 │ • Memory Bridge                            │
│                 │ • Storage Isolator                         │
│                 │ • Network Mapper                           │
│                 └──────────────┘                             │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Technical Implementation

1. **Micro-ROM Technology**: A stripped-down Android 8.1 system image (~193MB compressed) containing only essential components
2. **Container Virtualization**: Lightweight OS-level virtualization, not hardware emulation
3. **Namespace Isolation**: Linux namespace-based separation for processes, network, and filesystem
4. **Binder IPC Bridge**: Custom inter-process communication for app-host interaction
5. **Hardware Passthrough**: Direct GPU and audio access for native performance

---

## 📥 Installation

### Requirements
- Android 7.0 (API 24) or higher
- 4GB RAM minimum (6GB+ recommended)
- 500MB free storage (for ROM + apps)
- ARM64-v8a processor

### ⚠️ Important: Build It Yourself

> **GitHub Actions builds are currently not working, so no pre-built APK is available for download.**
> 
> You **must build the APK yourself** using:
> - **[Termux](#-termux-support-no-pc-needed)** (easiest - build directly on your phone)
> - **[Android Studio](#-local-testing--game-setup-guide)** (build on PC)
> 
> Sorry for the inconvenience! We are working on fixing the CI/CD pipeline.


---

## 📱 Termux Support (No PC Needed!)

**The easiest way to install DualVerse directly on your phone without a PC, ADB, or Android Studio!**

### What is Termux?

Termux is a powerful terminal emulator for Android that lets you run Linux commands. With Termux, you can build and install DualVerse entirely from your phone!

### Quick Install (One Command)

Open Termux and paste this:

```bash
pkg install git wget -y && bash -c "$(wget -qO- https://raw.githubusercontent.com/TheStrongestOfTomorrow/DualVerse/main/scripts/termux-install.sh)"
```

### Step-by-Step Termux Installation

#### 1. Install Termux

Download Termux from **F-Droid** (NOT Play Store - it's outdated):
- Direct download: https://f-droid.org/repo/com.termux_1020.apk
- Or visit: https://f-droid.org/en/packages/com.termux/

> ⚠️ **Important**: The Play Store version of Termux is outdated and won't work. Use F-Droid version!

#### 2. Setup Termux Environment

Open Termux and run:

```bash
# Update packages
pkg update && pkg upgrade -y

# Install required packages
pkg install git wget curl zip unzip -y

# Install build tools
pkg install openjdk-17 gradle -y

# Install Android SDK
pkg install android-tools -y
```

#### 3. Clone and Setup DualVerse

```bash
# Navigate to shared storage
cd ~/storage/downloads

# Clone the repository
git clone https://github.com/TheStrongestOfTomorrow/DualVerse.git
cd DualVerse

# Download the ROM (193MB)
bash scripts/download-rom.sh
```

#### 4. Build the APK

```bash
# Set Java environment
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk

# Build debug APK (takes 5-10 minutes)
./gradlew assembleDebug

# APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

#### 5. Install the APK

**Option A: Using Termux**
```bash
# Copy APK to downloads folder
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/

# Open file manager and install
am start -a android.intent.action.VIEW -d file:///sdcard/Download/app-debug.apk -t application/vnd.android.package-archive
```

**Option B: Manual Install**
```bash
# APK is now in your Downloads folder
# Open your file manager app
# Navigate to Downloads
# Tap on app-debug.apk to install
```

### Termux One-Liner (Full Auto)

For advanced users, here's a complete one-liner:

```bash
pkg update && pkg upgrade -y && pkg install git wget openjdk-17 gradle android-tools -y && cd ~/storage/downloads && git clone https://github.com/TheStrongestOfTomorrow/DualVerse.git && cd DualVerse && bash scripts/download-rom.sh && export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk && ./gradlew assembleDebug && cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/DualVerse.apk && echo "✅ APK ready at: ~/storage/downloads/DualVerse.apk"
```

### Termux Troubleshooting

#### Storage Permission Issue
```bash
# Grant storage permission
termux-setup-storage

# If asked, tap "Allow"
```

#### Java Not Found
```bash
# Install OpenJDK 17
pkg install openjdk-17 -y

# Set JAVA_HOME
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk

# Add to profile for persistence
echo 'export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk' >> ~/.bashrc
```

#### Gradle Build Fails
```bash
# Clear gradle cache
rm -rf ~/.gradle
rm -rf app/build

# Retry build
./gradlew clean assembleDebug
```

#### Out of Memory
```bash
# Increase Java heap size
export _JAVA_OPTIONS="-Xmx2g"

# Then rebuild
./gradlew assembleDebug
```

### Termux Build Script

Create a reusable build script:

```bash
# Create the script
cat > ~/build-dualverse.sh << 'EOF'
#!/bin/bash
cd ~/storage/downloads/DualVerse
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
export _JAVA_OPTIONS="-Xmx2g"
git pull origin main
./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/DualVerse.apk
echo "✅ Build complete! APK: ~/storage/downloads/DualVerse.apk"
EOF

# Make executable
chmod +x ~/build-dualverse.sh

# Run anytime with:
~/build-dualverse.sh
```

---

## 🎮 Local Testing & Game Setup Guide

This section covers everything you need to test DualVerse locally and set up games for multi-account gameplay.

### Prerequisites

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| Android Studio | Hedgehog (2023.1) | Latest |
| JDK | 17 | 21 |
| Android SDK | API 24 | API 34 |
| NDK | r22 | r25 |
| Device RAM | 4GB | 8GB+ |
| Device Storage | 1GB free | 2GB+ free |

### Step-by-Step Setup

#### 1. Install Android Studio

**Windows/macOS/Linux:**
1. Download from [developer.android.com/studio](https://developer.android.com/studio)
2. Install with default settings
3. Open Android Studio → Settings → Appearance & Behavior → System Settings → Android SDK
4. Install SDK Platform 34 (Android 14)
5. Install SDK Build-Tools 34
6. Install NDK (Side by side) → r25.x
7. Install CMake 3.22.1

**Linux (Ubuntu/Debian):**
```bash
# Install JDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# Install Android Studio via Snap
sudo snap install android-studio --classic

# Or download manually
wget https://dl.google.com/dl/android/studio/ide-zips/2023.1.1.26/android-studio-2023.1.1.26-linux.tar.gz
tar -xzf android-studio-*.tar.gz
./android-studio/bin/studio.sh
```

#### 2. Clone and Set Up Project

```bash
# Clone the repository
git clone https://github.com/TheStrongestOfTomorrow/DualVerse.git
cd DualVerse

# Download the ROM (193MB) - REQUIRED!
./scripts/download-rom.sh

# Or manually:
# 1. Download container APK from releases
# 2. Extract rootfs.7z from the APK (it's a ZIP file)
# 3. Copy to app/src/main/assets/rootfs.7z
```

#### 3. Build the APK

**Using Android Studio:**
1. Open Android Studio
2. File → Open → Select the DualVerse folder
3. Wait for Gradle sync to complete
4. Build → Make Project (Ctrl+F9)
5. Build → Build Bundle(s) / APK(s) → Build APK(s)
6. APK will be in `app/build/outputs/apk/debug/app-debug.apk`

**Using Command Line:**
```bash
# Make gradlew executable (Linux/macOS)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing)
./gradlew assembleRelease

# Output location:
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release.apk
```

#### 4. Install on Device

**Via ADB (USB Debugging):**
```bash
# Enable USB Debugging on your device:
# Settings → About Phone → Tap "Build Number" 7 times
# Settings → Developer Options → Enable USB Debugging

# Connect device and verify
adb devices

# Install the APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or install with existing app data preserved
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Via Android Studio:**
1. Connect your device via USB
2. Enable USB Debugging
3. Run → Run 'app' (Shift+F10)
4. Select your device from the list

**Transfer APK Manually:**
```bash
# Copy APK to device
adb push app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/

# Then on your device, open file manager and install
```

### First-Time Setup

When you first open DualVerse:

1. **Grant Permissions** - Allow all requested permissions:
   - Storage (for ROM extraction)
   - Phone (for device ID spoofing)
   - Location (optional, for some games)

2. **ROM Extraction** - Wait for the ROM to extract (1-3 minutes):
   - Progress bar will show extraction status
   - ~500MB will be extracted to app data directory

3. **Container Initialization** - First boot takes 10-30 seconds:
   - The virtual Android system boots inside the container
   - You'll see a "Ready" status when complete

### Adding Games & Accounts

#### Method 1: Clone Existing Game

1. Open DualVerse app
2. Tap **"Clone App"** button
3. Select a game from the list of installed apps
4. Wait for cloning to complete
5. The cloned game will appear in your accounts list

#### Method 2: Install APK Directly

```bash
# Install APK into the container
adb shell
# Inside the container environment:
pm install /path/to/game.apk
```

#### Method 3: Import from Host Device

1. Ensure the game is installed on your main device
2. Open DualVerse → Settings → Import App
3. Select the game to import
4. App data will be copied to the container

### Running Games

#### Single Account Mode
1. Open DualVerse
2. Tap on the cloned game icon
3. Game launches inside the container
4. Log in with your account

#### Dual Account Mode
1. Open DualVerse
2. Go to Accounts tab
3. Tap **"+"** to add another clone of the same game
4. Tap **"Dual View"** to run both simultaneously
5. Each instance runs independently with different accounts

### Supported Games Setup

| Game | Special Setup | Notes |
|------|---------------|-------|
| **Roblox** | None required | Works out of the box. Root detection is bypassed. |
| **PUBG Mobile** | Disable anti-cheat in settings | May need to clear data between accounts |
| **Free Fire** | None required | Works with all server regions |
| **Mobile Legends** | None required | Each instance gets unique device ID |
| **Genshin Impact** | Disable anti-cheat | Works with HoYoverse accounts |
| **COD Mobile** | None required | Activision accounts supported |
| **Clash of Clans** | Supercell ID recommended | Switch accounts via Supercell ID |

### Troubleshooting

#### App Crashes on Launch
```bash
# Check logcat for errors
adb logcat -s DualVerse:* DualVerse-Native:*

# Common fixes:
# 1. Clear app data
adb shell pm clear com.dualverse

# 2. Reinstall
adb uninstall com.dualverse
adb install app-debug.apk

# 3. Check device compatibility
adb shell getprop ro.product.cpu.abi
# Should return: arm64-v8a
```

#### ROM Extraction Fails
```bash
# Check available storage
adb shell df -h /data

# Need at least 1GB free space
# Clear cache if needed:
adb shell pm trim-caches 1G
```

#### Game Not Cloning
```bash
# Check if game is installed
adb shell pm list packages | grep <game-name>

# Some games use split APKs
adb shell pm list packages -f
# Note the path, then:
adb pull /path/to/base.apk
```

#### Performance Issues
1. **Lower graphics settings** in-game
2. **Enable GPU acceleration** in DualVerse settings
3. **Close background apps** on host device
4. **Use performance mode** in device settings

### Testing Commands

```bash
# View container status
adb shell "dumpsys activity top | grep -A 10 ACTIVITY"

# Check memory usage
adb shell "dumpsys meminfo com.dualverse"

# Monitor performance
adb shell "top -n 1 | grep dualverse"

# View native logs
adb logcat -s DualVerse-Native:V

# View container logs
adb logcat -s ContainerLoader:* RomManager:*

# Check extracted ROM
adb shell "ls -la /data/data/com.dualverse/rootfs/"

# Test native libraries
adb shell "run-as com.dualverse ls -R lib/"
```

### Development & Debugging

#### Enable Debug Mode
1. Open DualVerse → Settings
2. Tap "Build Number" 7 times
3. Developer Options appear
4. Enable "Debug Mode"
5. Enable "Verbose Logging"

#### View Internal State
```bash
# Access container filesystem
adb shell run-as com.dualverse
cd rootfs/

# View ROM info
cat rom.ini

# Check container processes
ps -ef | grep dualverse
```

#### Build with Debug Symbols
```bash
# Edit app/build.gradle.kts
# In android.defaultConfig:
ndk { debugSymbolLevel 'FULL' }

# Rebuild
./gradlew clean assembleDebug
```

---

## 🏗️ Architecture

### Core Components

#### 1. Virtualization Engine (`core/virtualization/`)
The heart of DualVerse, responsible for creating and managing the isolated Android environment.

```
virtualization/
├── VirtualMachineManager.kt    # Main VM lifecycle management
├── ContainerService.kt         # Container creation and isolation
├── MemoryBridge.kt            # Shared memory management
├── StorageIsolator.kt         # Filesystem separation
└── NetworkNamespace.kt        # Network isolation and mapping
```

#### 2. Security Layer (`core/security/`)
Handles all security-critical operations including device spoofing and anti-detection.

```
security/
├── DeviceSpoofer.kt           # Device ID generation and management
├── MacRandomizer.kt           # Network address randomization
├── AntiDetection.kt           # Anti-cheat system bypasses
├── FingerprintManager.kt      # Virtual fingerprint generation
└── KeyStoreBridge.kt          # Secure credential storage
```

#### 3. ROM Management (`core/rom/`)
Manages the container Android system image.

```
rom/
├── RomManager.kt              # ROM extraction and management
└── ContainerLoader.kt         # Container lifecycle management
```

#### 4. UI Layer (`core/ui/`)
Modern Jetpack Compose-based user interface.

```
ui/
├── MainActivity.kt            # Main application activity
├── screens/
│   ├── HomeScreen.kt         # Dashboard and quick actions
│   ├── AccountsScreen.kt     # Account management UI
│   ├── GamesScreen.kt        # Supported games list
│   └── SettingsScreen.kt     # Application settings
└── components/
    ├── AccountCard.kt        # Account display component
    ├── GameTile.kt           # Game shortcut widget
    └── StatusIndicator.kt    # Virtual instance status
```

### Data Flow

```
User Action → UI Layer → Account Manager → Virtualization Engine
                                              ↓
                                        Container Service
                                              ↓
                                    ┌─────────────────┐
                                    │ Virtual Android │
                                    │    Instance     │
                                    └─────────────────┘
                                              ↓
                                        Security Layer
                                              ↓
                                        Target Game App
```

---

## 📱 User Interface

### Main Dashboard
<p align="center">
  <img src="docs/screenshots/home.png" width="250" alt="Home Screen"/>
  <img src="docs/screenshots/accounts.png" width="250" alt="Accounts Screen"/>
  <img src="docs/screenshots/games.png" width="250" alt="Games Screen"/>
</p>

### Features
- **One-Tap Launch**: Start any cloned app instantly
- **Dual View**: Split-screen view for simultaneous gameplay
- **Quick Settings**: Toggle between accounts with swipe gestures
- **Status Dashboard**: Real-time resource monitoring

---

## 🔐 Security & Privacy

### What We Protect
- **No Data Collection**: DualVerse operates entirely offline
- **Local Storage Only**: All account data stays on your device
- **Encrypted Containers**: Virtual storage is encrypted at rest
- **Network Privacy**: MAC randomization prevents tracking

### Anti-Detection Features
- **Unique Device Fingerprint**: Each virtual instance has a unique IMEI, Android ID, and hardware identifiers
- **Realistic Telemetry**: Simulated sensor data for device authenticity
- **No Root Footprint**: Works without root, leaves no traces
- **App-Level Isolation**: Cloned apps cannot detect the host environment

---

## 🛠️ Development

### Project Structure

```
DualVerse/
├── app/                          # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dualverse/
│   │   │   │   ├── core/         # Core business logic
│   │   │   │   ├── ui/           # User interface
│   │   │   │   ├── utils/        # Utility classes
│   │   │   │   ├── security/     # Security features
│   │   │   │   └── accounts/     # Account management
│   │   │   ├── res/              # Android resources
│   │   │   └── assets/           # ROM and config files
│   │   ├── test/                 # Unit tests
│   │   └── androidTest/          # Instrumentation tests
│   └── build.gradle.kts          # App-level build config
├── docs/                         # Documentation
├── scripts/                      # Build and utility scripts
├── build.gradle.kts              # Project-level build config
└── settings.gradle.kts           # Project settings
```

### Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.0 |
| UI Framework | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Hilt |
| Database | Room |
| Networking | Ktor |
| Virtualization | Custom Linux Container Engine |
| Build System | Gradle (Kotlin DSL) |

### Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📋 Roadmap

### Version 1.0 (Stable)
- [x] Core virtualization engine
- [x] Basic device spoofing
- [x] Single app cloning
- [x] Android 8.1 ROM integration (193MB compressed)
- [x] Native container libraries (libloader.so, libdualverse.so, libp7zip.so)
- [x] Termux support for building without PC

### Version 1.1 (Stable - Latest)
- [x] Multi-instance support (3+ accounts)
- [x] Cloud save synchronization
- [x] Custom ROM selection
- [x] Performance profiles

### Version 1.2 (Beta)
- [x] Redesigned UI (Material You)
- [x] Floating dual-mode windows
- [ ] Custom themes
- [ ] Game-specific presets

### Version 2.0 (Planned)
- [ ] Android 14 ROM support
- [ ] x86 emulator for ARM translation
- [ ] Remote instance management
- [ ] Plugin system for mods

---

## ❓ FAQ

### Will this get my account banned?
DualVerse uses sophisticated anti-detection that makes virtualized instances indistinguishable from real devices. However, always use at your own risk and review each game's terms of service.

### Does this require root?
No! DualVerse works completely without root access. The virtualization happens at the application level.

### How much storage does it need?
The base app is ~50MB, plus the 200MB container ROM. Each cloned app adds approximately the same size as the original app.

### Can I use this with any app?
Yes! While optimized for games, DualVerse can clone and run any Android app in the virtualized environment.

### Is my data safe?
Absolutely. All data is stored locally on your device with encryption. We collect zero telemetry or personal data.

### Can I build it on my phone?
Yes! Use Termux to build DualVerse directly on your Android device. See the [Termux Support](#-termux-support-no-pc-needed) section above.

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Container Engine**: Based on virtualization concepts from open-source Android container projects
- **ROM Foundation**: The Android 8.1 container system is derived from the [Twoyi Project](https://github.com/twoyi/Twoyi) (MPL-2.0 License)
- The Android Open Source Project
- Linux Containers (LXC) community
- All our contributors and testers

---

<p align="center">
  <strong>Made with ❤️ by TheStrongestOfTomorrow & Super Z
  <br>
  <sub>A duo project</sub> & Super Z
  <br>
  <sub>A duo project</sub></strong>
</p>

<p align="center">
  <a href="https://github.com/TheStrongestOfTomorrow/DualVerse/issues">Report Bug</a> •
  <a href="https://github.com/TheStrongestOfTomorrow/DualVerse/issues">Request Feature</a>
</p>
