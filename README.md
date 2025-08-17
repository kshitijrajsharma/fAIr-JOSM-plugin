# JOSM fAIrLoader Plugin

A JOSM plugin specifically designed to load fAIr prediction data with using an interface for HOT's fAIr API.

## Installation

### Manual Installation
1. Download the latest `fAIrLoader.jar` from the [releases page](../../releases)
2. Copy the JAR file to your JOSM plugins directory:
   - **Windows**: `%APPDATA%\JOSM\plugins\`
   - **macOS**: `~/Library/JOSM/plugins/`
   - **Linux**: `~/.local/share/JOSM/plugins/` (or `~/.josm/plugins/` for older installations)
3. Restart JOSM
4. The plugin will appear in the **Tools** menu as "Load from URL"

### Quick Installation Commands

**macOS:**
```bash
mkdir -p ~/Library/JOSM/plugins
curl -L "https://github.com/kshitijrajsharma/lcgeojosmplugin/releases/latest/download/fAIrLoader.jar" -o ~/Library/JOSM/plugins/fAIrLoader.jar
```

**Linux:**
```bash
mkdir -p ~/.local/share/JOSM/plugins
curl -L "https://github.com/kshitijrajsharma/lcgeojosmplugin/releases/latest/download/fAIrLoader.jar" -o ~/.local/share/JOSM/plugins/fAIrLoader.jar

```

**Windows (PowerShell):**
```powershell
New-Item -ItemType Directory -Force -Path "$env:APPDATA\JOSM\plugins"
Invoke-WebRequest -Uri "https://github.com/kshitijrajsharma/lcgeojosmplugin/releases/latest/download/fAIrLoader.jar" -OutFile "$env:APPDATA\JOSM\plugins\fAIrLoader.jar"
```

## Usage

1. Open JOSM and navigate to your area of interest
2. Go to **Tools** → **Load from fAIr** (or use Ctrl+Shift+F)
3. In the fAIr dialog:
   - **Enter Prediction UID**: Type your prediction identifier (e.g., `prediction_100`)
   - **Select Server**: Choose Production (default) or Development
   - **Select Format**: Choose GeoJSON (default) or OSM XML
   - **Review URL**: The URL is auto-generated but can be edited
4. Click **Load**

The plugin will:
- Automatically use your current JOSM view bounds
- Create a new layer named `fAIr_[your_prediction_uid]`
- Load the prediction data without affecting existing layers

## fAIr API Integration

### Server Options
- **Production**: `api-prod.fair.hotosm.org` (default)
- **Development**: `fair-dev.hotosm.org`

### Generated URL Format
```
https://[server]/api/v1/workspace/stream/[prediction_uid]/labels.[format]?bbox={bbox}&format=[format]
```

### Example URLs Generated
**Production GeoJSON:**
```
https://api-prod.fair.hotosm.org/api/v1/workspace/stream/prediction_100/labels.fgb?bbox={bbox}&format=geojson
```

**Development OSM XML:**
```
https://fair-dev.hotosm.org/api/v1/workspace/stream/prediction_100/labels.fgb?bbox={bbox}&format=osmxml
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

The JAR will be created in `build/libs/fAIrLoader.jar`

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
