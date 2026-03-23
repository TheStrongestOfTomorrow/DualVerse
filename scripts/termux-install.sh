#!/bin/bash

# DualVerse Termux Installation Script
# This script builds and installs DualVerse directly on your Android device via Termux
# No PC, ADB, or Android Studio required!

# Don't use set -e so we can handle errors ourselves

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Error handler
error_exit() {
    echo -e "${RED}"
    echo "=========================================="
    echo "✗ Error: $1"
    echo "=========================================="
    echo -e "${NC}"
    exit 1
}

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
    echo "Please tap 'Allow' on the permission dialog!"
    termux-setup-storage
    sleep 3
    if [ ! -d "$HOME/storage/downloads" ]; then
        error_exit "Storage permission denied. Please allow storage access and run again."
    fi
fi

echo -e "${GREEN}✓ Storage permission granted${NC}"

# Update packages
echo -e "${BLUE}Updating packages... (this may take a minute)${NC}"
pkg update -y || echo -e "${YELLOW}Warning: Some packages couldn't be updated${NC}"

# Install required packages
echo -e "${BLUE}Installing dependencies...${NC}"
pkg install -y git wget curl zip unzip openjdk-17 || error_exit "Failed to install dependencies"

# Set Java environment
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
export _JAVA_OPTIONS="-Xmx2g"

echo -e "${GREEN}✓ Dependencies installed${NC}"

# Create working directory
WORK_DIR="$HOME/storage/downloads/DualVerse"

if [ -d "$WORK_DIR" ]; then
    echo -e "${YELLOW}Existing DualVerse directory found. Removing for clean install...${NC}"
    rm -rf "$WORK_DIR"
fi

echo -e "${BLUE}Cloning DualVerse repository...${NC}"
cd "$HOME/storage/downloads"
git clone https://github.com/TheStrongestOfTomorrow/DualVerse.git || error_exit "Failed to clone repository"
cd "$WORK_DIR"

echo -e "${GREEN}✓ Repository ready${NC}"

# Download ROM
echo ""
echo -e "${BLUE}==========================================${NC}"
echo -e "${BLUE}Downloading container ROM (~193MB)...${NC}"
echo -e "${BLUE}==========================================${NC}"

# Create assets directory
mkdir -p app/src/main/assets

ROM_URL="https://github.com/twoyi/twoyi/releases/download/0.5.4/twoyi_0.5.4-03211927-release.apk"
TEMP_APK="$HOME/dualverse-rom.apk"

echo -e "${YELLOW}Downloading... Please wait, this may take a few minutes.${NC}"

if command -v wget &> /dev/null; then
    wget --show-progress -O "$TEMP_APK" "$ROM_URL" || {
        echo -e "${YELLOW}wget failed, trying curl...${NC}"
        curl -L --progress-bar -o "$TEMP_APK" "$ROM_URL" || error_exit "Failed to download ROM"
    }
elif command -v curl &> /dev/null; then
    curl -L --progress-bar -o "$TEMP_APK" "$ROM_URL" || error_exit "Failed to download ROM"
else
    error_exit "Neither wget nor curl available"
fi

# Check file size
FILE_SIZE=$(stat -c %s "$TEMP_APK" 2>/dev/null || echo "0")
if [ "$FILE_SIZE" -lt 10000000 ]; then
    rm -f "$TEMP_APK"
    error_exit "Download failed - file too small ($FILE_SIZE bytes). Check your internet connection."
fi

echo -e "${GREEN}✓ Download complete! Size: $(du -h "$TEMP_APK" | cut -f1)${NC}"

# Extract ROM from APK
echo -e "${BLUE}Extracting ROM from package...${NC}"

if ! unzip -j "$TEMP_APK" "assets/rootfs.7z" -d app/src/main/assets/ 2>/dev/null; then
    echo -e "${YELLOW}Note: rootfs.7z not found at expected location"
    echo -e "${YELLOW}Listing APK contents...${NC}"
    unzip -l "$TEMP_APK" | head -50
    echo ""
    echo -e "${YELLOW}The APK structure may have changed. Continuing without ROM...${NC}"
    echo -e "${YELLOW}You may need to manually add rootfs.7z later.${NC}"
else
    echo -e "${GREEN}✓ ROM extracted successfully${NC}"
fi

# Cleanup
rm -f "$TEMP_APK"

# Build the APK
echo ""
echo -e "${BLUE}==========================================${NC}"
echo -e "${BLUE}Building DualVerse APK...${NC}"
echo -e "${BLUE}==========================================${NC}"
echo -e "${YELLOW}This will take 5-15 minutes. Please be patient!${NC}"
echo ""

# Make gradlew executable
chmod +x gradlew

# Build with limited memory for Termux
./gradlew assembleDebug --no-daemon --max-workers=1 -Dorg.gradle.jvmargs="-Xmx1536m"

BUILD_STATUS=$?

if [ $BUILD_STATUS -ne 0 ]; then
    echo ""
    echo -e "${RED}=========================================="
    echo "Build failed with exit code: $BUILD_STATUS"
    echo "==========================================${NC}"
    echo ""
    echo -e "${YELLOW}Common fixes:${NC}"
    echo "1. Make sure you have at least 4GB free storage"
    echo "2. Close other apps to free up memory"
    echo "3. Run: ./gradlew clean && ./gradlew assembleDebug"
    exit 1
fi

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    # Copy to downloads
    cp app/build/outputs/apk/debug/app-debug.apk "$HOME/storage/downloads/DualVerse.apk"
    
    APK_SIZE=$(du -h "$HOME/storage/downloads/DualVerse.apk" | cut -f1)
    
    echo -e "${GREEN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║              BUILD SUCCESSFUL! 🎉                          ║"
    echo "╠═══════════════════════════════════════════════════════════╣"
    echo "║  APK Size: $APK_SIZE                                        "
    echo "║  Location:                                                 ║"
    echo "║  ~/storage/downloads/DualVerse.apk                         ║"
    echo "║                                                           ║"
    echo "║  To install:                                              ║"
    echo "║  1. Open your file manager app                            ║"
    echo "║  2. Go to Downloads folder                                ║"
    echo "║  3. Tap on DualVerse.apk                                  ║"
    echo "║  4. Enable 'Install unknown apps' if asked                ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    # Try to open the APK
    echo -e "${BLUE}Opening APK for installation...${NC}"
    am start -a android.intent.action.VIEW \
        -d file:///sdcard/Download/DualVerse.apk \
        -t application/vnd.android.package-archive 2>/dev/null || \
    termux-open "$HOME/storage/downloads/DualVerse.apk" 2>/dev/null || \
    echo -e "${YELLOW}Please manually open DualVerse.apk from your Downloads folder${NC}"
else
    error_exit "Build completed but APK not found at expected location"
fi
