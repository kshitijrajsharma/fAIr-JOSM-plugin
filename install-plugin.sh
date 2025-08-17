#!/bin/bash

# Test script for JOSM URL Loader Plugin
# This script installs the plugin locally for testing

set -e

echo "Building JOSM URL Loader Plugin..."
./gradlew build

echo "Creating JOSM plugins directory..."
mkdir -p ~/.josm/plugins

echo "Installing plugin..."
cp build/libs/URLLoader.jar ~/.josm/plugins/

echo "Plugin installed successfully!"
echo ""
echo "To test:"
echo "1. Start JOSM"
echo "2. Go to Tools -> Load from URL"
echo "3. Enter a GeoJSON URL like:"
echo "   https://raw.githubusercontent.com/datasets/geo-countries/master/data/countries.geojson"
echo "4. Or a bbox-enabled URL like:"
echo "   https://overpass-api.de/api/interpreter?data=[out:json][bbox:{bbox}];way[highway];out;"
