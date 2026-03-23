#!/bin/bash

# DualVerse ROM Download Script
# Downloads the Android 8.1 container ROM
# Works in Termux and regular Linux

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
ASSETS_DIR="$PROJECT_ROOT/app/src/main/assets"

# Use appropriate temp directory
if [ -n "$TMPDIR" ]; then
    TEMP_DIR="$TMPDIR"
elif [ -d "/tmp" ]; then
    TEMP_DIR="/tmp"
else
    TEMP_DIR="$HOME/.tmp"
    mkdir -p "$TEMP_DIR"
fi

echo "=========================================="
echo "    DualVerse ROM Downloader"
echo "=========================================="
echo ""

# Create assets directory if it doesn't exist
mkdir -p "$ASSETS_DIR"

# Check if ROM already exists
if [ -f "$ASSETS_DIR/rootfs.7z" ]; then
    echo "✓ ROM already exists at:"
    echo "  $ASSETS_DIR/rootfs.7z"
    echo ""
    echo "Delete it and run this script again to re-download."
    exit 0
fi

echo "Downloading container ROM (~193MB)..."
echo "Source: Open-source Android container project"
echo ""

# Container ROM URL
ROM_URL="https://github.com/twoyi/twoyi/releases/download/0.5.4/twoyi_0.5.4-03211927-release.apk"
TEMP_APK="$TEMP_DIR/dualverse-rom.apk"

# Check for download tool
if command -v wget &> /dev/null; then
    echo "Using wget to download..."
    wget --show-progress -O "$TEMP_APK" "$ROM_URL" || {
        echo ""
        echo "✗ Download failed! Trying mirror..."
        # Try alternative approach - download in parts or use different tool
        rm -f "$TEMP_APK"
        exit 1
    }
elif command -v curl &> /dev/null; then
    echo "Using curl to download..."
    curl -L --progress-bar -o "$TEMP_APK" "$ROM_URL" || {
        echo ""
        echo "✗ Download failed!"
        rm -f "$TEMP_APK"
        exit 1
    }
else
    echo "✗ Error: Neither wget nor curl is installed!"
    echo "Please install one of them:"
    echo "  pkg install wget curl  (in Termux)"
    echo "  apt install wget curl  (on Debian/Ubuntu)"
    exit 1
fi

# Check if download succeeded
if [ ! -f "$TEMP_APK" ]; then
    echo "✗ Error: Download file not found!"
    exit 1
fi

FILE_SIZE=$(stat -c %s "$TEMP_APK" 2>/dev/null || stat -f %z "$TEMP_APK" 2>/dev/null || echo "0")
if [ "$FILE_SIZE" -lt 10000000 ]; then
    echo "✗ Error: Downloaded file is too small ($FILE_SIZE bytes)"
    echo "The download may have failed. Please try again."
    rm -f "$TEMP_APK"
    exit 1
fi

echo ""
echo "✓ Download complete! Size: $(du -h "$TEMP_APK" | cut -f1)"
echo ""
echo "Extracting ROM from package..."

# Extract rootfs.7z from APK (APK is a ZIP file)
if ! unzip -j "$TEMP_APK" "assets/rootfs.7z" -d "$ASSETS_DIR/" 2>/dev/null; then
    echo "Note: rootfs.7z not found in APK, trying alternative extraction..."
    # List contents and try to find the ROM file
    unzip -l "$TEMP_APK" | grep -i "rootfs\|\.7z" || true
fi

# Try to extract rom.ini
unzip -j "$TEMP_APK" "assets/rom.ini" -d "$ASSETS_DIR/" 2>/dev/null || true

# Cleanup
rm -f "$TEMP_APK"

# Verify extraction
if [ -f "$ASSETS_DIR/rootfs.7z" ]; then
    echo ""
    echo "=========================================="
    echo "✓ ROM downloaded successfully!"
    echo "=========================================="
    echo "  Location: $ASSETS_DIR/rootfs.7z"
    echo "  Size: $(du -h "$ASSETS_DIR/rootfs.7z" | cut -f1)"
    echo ""
    echo "You can now build the DualVerse app."
else
    echo ""
    echo "=========================================="
    echo "⚠ ROM file not found after extraction"
    echo "=========================================="
    echo ""
    echo "The APK was downloaded but rootfs.7z was not found inside."
    echo "This might be due to a different APK structure."
    echo ""
    echo "Manual steps:"
    echo "1. Download the APK manually from:"
    echo "   $ROM_URL"
    echo "2. Extract it using: unzip -l <apk_file>"
    echo "3. Find the ROM file and extract it to:"
    echo "   $ASSETS_DIR/"
    exit 1
fi
