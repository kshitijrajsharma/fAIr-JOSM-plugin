package org.openstreetmap.josm.plugins.fairloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FAIrLoader {
    
    private ObjectMapper objectMapper;

    public FAIrLoader() {
        this.objectMapper = new ObjectMapper();
    }

    public void loadFromURL(String urlString, Bounds bounds) throws IOException, URISyntaxException {
        String finalUrl = prepareFinalURL(urlString, bounds);
        String geoJsonData = fetchData(finalUrl);
        DataSet dataSet = parseGeoJSON(geoJsonData);
        addLayerToJOSM(dataSet, urlString);
    }
    
    public void loadFromURL(String urlString, Bounds bounds, String layerName) throws IOException, URISyntaxException {
        String finalUrl = prepareFinalURL(urlString, bounds);
        String geoJsonData = fetchData(finalUrl);
        DataSet dataSet = parseGeoJSON(geoJsonData);
        addLayerToJOSMWithName(dataSet, layerName);
    }

    private String prepareFinalURL(String urlString, Bounds bounds) {
        if (bounds == null) {
            return urlString.replace("{bbox}", "");
        }
        
        if (!urlString.contains("{bbox}")) {
            return urlString;
        }
        
        String bbox = String.format("%f,%f,%f,%f", 
            bounds.getMinLon(), bounds.getMinLat(), 
            bounds.getMaxLon(), bounds.getMaxLat());
        
        return urlString.replace("{bbox}", bbox);
    }

    private String fetchData(String urlString) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(urlString);
        request.setHeader("Accept", "application/json, application/geo+json");
        
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        
        if (statusCode == 200) {
            String responseBody = EntityUtils.toString(response.getEntity());
            return responseBody;
        } else if (statusCode == 404) {
            throw new IOException("URL not found (404). Please check the prediction UID and try again.");
        } else if (statusCode == 403) {
            throw new IOException("Access denied (403). You may not have permission to access this resource.");
        } else if (statusCode == 500) {
            throw new IOException("Server error (500). The fAIr server is experiencing issues. Please try again later.");
        } else {
            throw new IOException("Failed to load data. Server returned status: " + statusCode + " - " + response.getStatusLine().getReasonPhrase());
        }
    }

    private DataSet parseGeoJSON(String geoJsonData) throws IOException {
        DataSet dataSet = new DataSet();
        JsonNode rootNode = objectMapper.readTree(geoJsonData);
        
        if (rootNode.has("features")) {
            JsonNode features = rootNode.get("features");
            for (JsonNode feature : features) {
                parseFeature(feature, dataSet);
            }
        } else if (rootNode.has("geometry")) {
            parseFeature(rootNode, dataSet);
        }
        
        return dataSet;
    }

    private void parseFeature(JsonNode feature, DataSet dataSet) {
        JsonNode geometry = feature.get("geometry");
        if (geometry == null) return;
        
        String type = geometry.get("type").asText();
        JsonNode coordinates = geometry.get("coordinates");
        
        switch (type) {
            case "Point":
                parsePoint(coordinates, dataSet);
                break;
            case "LineString":
                parseLineString(coordinates, dataSet);
                break;
            case "Polygon":
                parsePolygon(coordinates, dataSet);
                break;
            case "MultiPoint":
                parseMultiPoint(coordinates, dataSet);
                break;
            case "MultiLineString":
                parseMultiLineString(coordinates, dataSet);
                break;
            case "MultiPolygon":
                parseMultiPolygon(coordinates, dataSet);
                break;
        }
    }

    private void parsePoint(JsonNode coordinates, DataSet dataSet) {
        double lon = coordinates.get(0).asDouble();
        double lat = coordinates.get(1).asDouble();
        Node node = new Node(new LatLon(lat, lon));
        dataSet.addPrimitive(node);
    }

    private void parseLineString(JsonNode coordinates, DataSet dataSet) {
        Way way = new Way();
        for (JsonNode coord : coordinates) {
            double lon = coord.get(0).asDouble();
            double lat = coord.get(1).asDouble();
            Node node = new Node(new LatLon(lat, lon));
            dataSet.addPrimitive(node);
            way.addNode(node);
        }
        dataSet.addPrimitive(way);
    }

    private void parsePolygon(JsonNode coordinates, DataSet dataSet) {
        for (JsonNode ring : coordinates) {
            Way way = new Way();
            for (JsonNode coord : ring) {
                double lon = coord.get(0).asDouble();
                double lat = coord.get(1).asDouble();
                Node node = new Node(new LatLon(lat, lon));
                dataSet.addPrimitive(node);
                way.addNode(node);
            }
            dataSet.addPrimitive(way);
        }
    }

    private void parseMultiPoint(JsonNode coordinates, DataSet dataSet) {
        for (JsonNode coord : coordinates) {
            parsePoint(coord, dataSet);
        }
    }

    private void parseMultiLineString(JsonNode coordinates, DataSet dataSet) {
        for (JsonNode lineString : coordinates) {
            parseLineString(lineString, dataSet);
        }
    }

    private void parseMultiPolygon(JsonNode coordinates, DataSet dataSet) {
        for (JsonNode polygon : coordinates) {
            parsePolygon(polygon, dataSet);
        }
    }

    private void addLayerToJOSM(DataSet dataSet, String sourceUrl) {
        String layerName = "URL Data: " + extractFileName(sourceUrl);
        addLayerToJOSMWithName(dataSet, layerName);
    }
    
    private void addLayerToJOSMWithName(DataSet dataSet, String layerName) {
        OsmDataLayer layer = new OsmDataLayer(dataSet, layerName, null);
        MainApplication.getLayerManager().addLayer(layer);
    }

    private String extractFileName(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            if (path != null && !path.isEmpty()) {
                String[] parts = path.split("/");
                if (parts.length > 0) {
                    return parts[parts.length - 1];
                }
            }
            return uri.getHost();
        } catch (URISyntaxException e) {
            return "Unknown";
        }
    }
}
