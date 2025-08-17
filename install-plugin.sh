#!/bin/bash

# Test script for JOSM fAIrLoader Plugin
# This script installs the plugin locally for testing

set -e

echo "Building JOSM fAIrLoader Plugin..."
./gradlew build

echo "Creating JOSM plugins directory..."
mkdir -p ~/Library/JOSM/plugins

echo "Installing plugin..."
cp build/libs/fAIrLoader.jar ~/Library/JOSM/plugins/

echo "Plugin installed successfully!"
echo ""
echo "To test:"
echo "1. Start JOSM"
echo "2. Go to Tools -> Load from URL"
echo "3. Enter a GeoJSON URL like:"
echo "   https://raw.githubusercontent.com/datasets/geo-countries/master/data/countries.geojson"
