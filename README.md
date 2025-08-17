# JOSM URL Loader Plugin

A simple JOSM plugin that allows you to load GeoJSON data from any URL with automatic bounding box support.

## Features

- Load GeoJSON data from any URL
- Automatic bounding box detection from current JOSM view
- Support for URLs with `{bbox}` placeholder
- Loads data as a new layer in JOSM
- Supports all GeoJSON geometry types (Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon)

## Installation

### Manual Installation
1. Download the latest `URLLoader.jar` from the [releases page](../../releases)
2. Copy the JAR file to your JOSM plugins directory:
   - **Windows**: `%APPDATA%\JOSM\plugins\`
   - **macOS**: `~/.josm/plugins/`
   - **Linux**: `~/.josm/plugins/`
3. Restart JOSM
4. The plugin will appear in the **Tools** menu as "Load from URL"

### Quick Installation Commands
```bash
# macOS/Linux
mkdir -p ~/.josm/plugins
curl -L "https://github.com/kshitijrajsharma/lcgeojosmplugin/releases/latest/download/URLLoader.jar" -o ~/.josm/plugins/URLLoader.jar

# Windows (PowerShell)
New-Item -ItemType Directory -Force -Path "$env:APPDATA\JOSM\plugins"
Invoke-WebRequest -Uri "https://github.com/kshitijrajsharma/lcgeojosmplugin/releases/latest/download/URLLoader.jar" -OutFile "$env:APPDATA\JOSM\plugins\URLLoader.jar"
```

## Usage

1. Open JOSM and navigate to your area of interest
2. Go to **Tools** → **Load from URL** (or use Ctrl+Shift+U)
3. Enter your URL in the dialog:
   - **For bbox-enabled URLs**: `https://example.com/api/data?bbox={bbox}`
   - **For direct URLs**: `https://example.com/data.geojson`
4. Click **Load**

### URL Examples

**With bbox support:**
```
https://overpass-api.de/api/interpreter?data=[out:json][bbox:{bbox}];way[highway];out;
https://api.example.com/geojson?bbox={bbox}
```

**Direct GeoJSON URLs:**
```
https://raw.githubusercontent.com/user/repo/main/data.geojson
https://api.github.com/repos/user/repo/contents/data.geojson
```

### Bbox Transformation

When you use `{bbox}` in your URL, the plugin replaces it with your current JOSM view bounds in this format:

**Format**: `minlon,minlat,maxlon,maxlat`

**Example transformation:**
- **Input URL**: `https://api.example.com/data?bbox={bbox}`
- **Your JOSM view**: Berlin, Germany  
- **Transformed URL**: `https://api.example.com/data?bbox=13.088,52.338,13.761,52.675`

**Coordinate explanation:**
- `minlon` (13.088) = westernmost longitude (left edge)
- `minlat` (52.338) = southernmost latitude (bottom edge)  
- `maxlon` (13.761) = easternmost longitude (right edge)
- `maxlat` (52.675) = northernmost latitude (top edge)

The plugin will automatically:
- Replace `{bbox}` with your current view bounds (minlon,minlat,maxlon,maxlat) if present
- Fetch the GeoJSON data from the URL
- Parse the GeoJSON and create appropriate OSM nodes and ways
- Create a new layer with the loaded data

## Supported GeoJSON Features

- Point
- LineString  
- Polygon
- MultiPoint
- MultiLineString
- MultiPolygon
- Feature collections
- Single features

## Building from Source

### Prerequisites
- Java 17+
- Gradle (or use included wrapper)

### Build Steps
```bash
git clone https://github.com/kshitijrajsharma/lcgeojosmplugin.git
cd lcgeojosmplugin
./gradlew build
```

The JAR will be created in `build/libs/URLLoader.jar`

### Development
The plugin includes all necessary dependencies and can be built offline after the first successful build.

## Security Note

 **Warning**: This plugin will fetch data from any URL you provide. Only use trusted sources and be aware that:
- The plugin makes HTTP requests to the URLs you specify
- Data is loaded directly into JOSM without validation
- Malformed GeoJSON may cause errors

## Troubleshooting

**Plugin doesn't appear in Tools menu:**
- Ensure the JAR is in the correct plugins directory
- Restart JOSM completely
- Check JOSM's plugin list in Preferences

**"Error loading data" message:**
- Check that the URL is accessible
- Verify the URL returns valid GeoJSON
- Check JOSM's log for detailed error messages

**Empty layer created:**
- The GeoJSON may not contain any features in your current view
- Try zooming out or using a different URL

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test with JOSM
5. Submit a pull request

## License

This project is released under the GPL v3 license - the same license as JOSM.
