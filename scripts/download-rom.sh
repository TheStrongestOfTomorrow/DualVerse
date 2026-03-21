#!/bin/bash

# DualVerse ROM Download Script
# Downloads the Android 8.1 container ROM

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
ASSETS_DIR="$PROJECT_ROOT/app/src/main/assets"

echo "DualVerse ROM Downloader"
echo "========================"
echo ""

# Create assets directory if it doesn't exist
mkdir -p "$ASSETS_DIR"

# Check if ROM already exists
if [ -f "$ASSETS_DIR/rootfs.7z" ]; then
    echo "ROM already exists at $ASSETS_DIR/rootfs.7z"
    echo "Delete it and run this script again to re-download."
    exit 0
fi

echo "Downloading container ROM (193MB)..."
echo "Note: The ROM is derived from an open-source Android container project."
echo ""

# Container ROM URL (derived from open-source project)
ROM_URL="https://github.com/twoyi/twoyi/releases/download/0.5.4/twoyi_0.5.4-03211927-release.apk"
TEMP_APK="/tmp/dualverse-rom.apk"

# Download
curl -L --progress-bar -o "$TEMP_APK" "$ROM_URL"

echo ""
echo "Extracting ROM from package..."

# Extract rootfs.7z from APK (APK is a ZIP file)
unzip -j "$TEMP_APK" "assets/rootfs.7z" -d "$ASSETS_DIR/"
unzip -j "$TEMP_APK" "assets/rom.ini" -d "$ASSETS_DIR/"

# Cleanup
rm -f "$TEMP_APK"

echo ""
echo "✓ ROM downloaded successfully!"
echo "  Location: $ASSETS_DIR/rootfs.7z"
echo "  Size: $(du -h "$ASSETS_DIR/rootfs.7z" | cut -f1)"
echo ""
echo "You can now build the DualVerse app."
