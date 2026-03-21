#!/bin/bash

# DualVerse Termux Installation Script
# This script builds and installs DualVerse directly on your Android device via Termux
# No PC, ADB, or Android Studio required!

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print banner
echo -e "${BLUE}"
echo "╔═══════════════════════════════════════════════════════════╗"
echo "║                    DualVerse Builder                       ║"
echo "║           Multi-Account Gaming Container                   ║"
echo "╚═══════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Check if running in Termux
if [ ! -d "/data/data/com.termux" ]; then
    echo -e "${RED}Error: This script must be run in Termux!${NC}"
    echo "Install Termux from: https://f-droid.org/en/packages/com.termux/"
    exit 1
fi

# Check for storage permission
if [ ! -d "$HOME/storage/downloads" ]; then
    echo -e "${YELLOW}Requesting storage permission...${NC}"
    termux-setup-storage
    sleep 2
    if [ ! -d "$HOME/storage/downloads" ]; then
        echo -e "${RED}Storage permission denied. Please allow storage access.${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✓ Storage permission granted${NC}"

# Update packages
echo -e "${BLUE}Updating packages...${NC}"
pkg update -y && pkg upgrade -y

# Install required packages
echo -e "${BLUE}Installing dependencies...${NC}"
pkg install -y git wget curl zip unzip openjdk-17 gradle android-tools

# Set Java environment
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
export _JAVA_OPTIONS="-Xmx2g"

echo -e "${GREEN}✓ Dependencies installed${NC}"

# Create working directory
WORK_DIR="$HOME/storage/downloads/DualVerse"

if [ -d "$WORK_DIR" ]; then
    echo -e "${YELLOW}Existing DualVerse directory found. Updating...${NC}"
    cd "$WORK_DIR"
    git pull origin main || {
        echo -e "${YELLOW}Cannot pull updates, re-cloning...${NC}"
        cd ..
        rm -rf "$WORK_DIR"
        git clone https://github.com/TheStrongestOfTomorrow/DualVerse.git
        cd "$WORK_DIR"
    }
else
    echo -e "${BLUE}Cloning DualVerse repository...${NC}"
    cd "$HOME/storage/downloads"
    git clone https://github.com/TheStrongestOfTomorrow/DualVerse.git
    cd "$WORK_DIR"
fi

echo -e "${GREEN}✓ Repository ready${NC}"

# Download ROM if not present
ROM_FILE="app/src/main/assets/rootfs.7z"
if [ ! -f "$ROM_FILE" ]; then
    echo -e "${BLUE}Downloading container ROM (193MB)...${NC}"
    
    # Create assets directory
    mkdir -p app/src/main/assets
    
    # Download ROM
    ROM_URL="https://github.com/twoyi/twoyi/releases/download/0.5.4/twoyi_0.5.4-03211927-release.apk"
    TEMP_APK="/tmp/dualverse-rom.apk"
    
    wget -q --show-progress -O "$TEMP_APK" "$ROM_URL"
    
    # Extract ROM from APK
    echo -e "${BLUE}Extracting ROM from package...${NC}"
    unzip -j "$TEMP_APK" "assets/rootfs.7z" -d app/src/main/assets/
    unzip -j "$TEMP_APK" "assets/rom.ini" -d app/src/main/assets/
    
    # Cleanup
    rm -f "$TEMP_APK"
    
    echo -e "${GREEN}✓ ROM downloaded (${ROM_FILE})${NC}"
else
    echo -e "${GREEN}✓ ROM already present${NC}"
fi

# Build the APK
echo -e "${BLUE}Building DualVerse APK...${NC}"
echo -e "${YELLOW}This may take 5-10 minutes. Please wait...${NC}"

./gradlew assembleDebug --no-daemon

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    # Copy to downloads
    cp app/build/outputs/apk/debug/app-debug.apk "$HOME/storage/downloads/DualVerse.apk"
    
    echo -e "${GREEN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║              BUILD SUCCESSFUL! 🎉                          ║"
    echo "╠═══════════════════════════════════════════════════════════╣"
    echo "║  APK Location:                                            ║"
    echo "║  ~/storage/downloads/DualVerse.apk                        ║"
    echo "║                                                           ║"
    echo "║  To install:                                              ║"
    echo "║  1. Open your file manager                                ║"
    echo "║  2. Go to Downloads folder                                ║"
    echo "║  3. Tap on DualVerse.apk                                  ║"
    echo "║  4. Enable 'Install from unknown sources' if prompted     ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    # Offer to open file manager
    echo -e "${BLUE}Open file manager now? (y/n)${NC}"
    read -r answer
    if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
        am start -a android.intent.action.VIEW -d file:///sdcard/Download/DualVerse.apk -t application/vnd.android.package-archive 2>/dev/null || \
        xdg-open "$HOME/storage/downloads/DualVerse.apk" 2>/dev/null || \
        echo -e "${YELLOW}Please manually open the APK from your Downloads folder${NC}"
    fi
else
    echo -e "${RED}Build failed! Check the error messages above.${NC}"
    exit 1
fi
