package org.openstreetmap.josm.plugins.fairloader;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.spi.preferences.Config;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FAIrLoaderDialog extends JDialog {
    
    private static final String PREF_PREDICTION_UID = "fairloader.prediction_uid";
    private static final String PREF_SERVER_SELECTION = "fairloader.server_selection";
    private static final String PREF_FORMAT_SELECTION = "fairloader.format_selection";
    
    private JTextField predictionUidField;
    private JComboBox<ServerOption> serverComboBox;
    private JComboBox<FormatOption> formatComboBox;
    private JTextArea urlTextArea;
    private JTextField bboxField;
    private JButton loadButton;
    private JButton cancelButton;

    private static class ServerOption {
        String name;
        String domain;
        
        ServerOption(String name, String domain) {
            this.name = name;
            this.domain = domain;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    private static class FormatOption {
        String name;
        String value;
        
        FormatOption(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }

    public FAIrLoaderDialog(Frame parent) {
        super(parent, "Load from fAIr", true);
        initComponents();
        setupLayout();
        setupListeners();
        loadPreferences();
        updateURL();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        predictionUidField = new JTextField(20);
        
        ServerOption[] servers = {
            new ServerOption("Production", "api-prod.fair.hotosm.org"),
            new ServerOption("Development", "fair-dev.hotosm.org")
        };
        serverComboBox = new JComboBox<>(servers);
        serverComboBox.setSelectedIndex(0);
        
        FormatOption[] formats = {
            new FormatOption("GeoJSON", "geojson"),
            new FormatOption("OSM XML", "osmxml")
        };
        formatComboBox = new JComboBox<>(formats);
        formatComboBox.setSelectedIndex(0);
        
        urlTextArea = new JTextArea(3, 50);
        urlTextArea.setLineWrap(true);
        urlTextArea.setWrapStyleWord(true);
        urlTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        bboxField = new JTextField(30);
        bboxField.setEditable(false);
        bboxField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        bboxField.setBackground(getBackground());
        
        loadButton = new JButton("Load");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 5, 5);
        mainPanel.add(new JLabel("Prediction UID:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 5, 10);
        mainPanel.add(predictionUidField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 10, 5, 5);
        mainPanel.add(new JLabel("Server:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 10);
        mainPanel.add(serverComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 10, 5, 5);
        mainPanel.add(new JLabel("Format:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 10);
        mainPanel.add(formatComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 10, 5, 5);
        mainPanel.add(new JLabel("Current Bbox:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 10);
        mainPanel.add(bboxField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(15, 10, 5, 5);
        mainPanel.add(new JLabel("Generated URL:"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 10, 10, 10);
        JScrollPane scrollPane = new JScrollPane(urlTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loadButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        predictionUidField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateURL(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateURL(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateURL(); }
        });
        
        serverComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateURL();
                }
            }
        });
        
        formatComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateURL();
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePreferences();
                dispose();
            }
        });

        predictionUidField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
    }
    
    private void updateURL() {
        String predictionUid = predictionUidField.getText().trim();
        ServerOption selectedServer = (ServerOption) serverComboBox.getSelectedItem();
        FormatOption selectedFormat = (FormatOption) formatComboBox.getSelectedItem();
        
        updateBboxField();
        
        if (predictionUid.isEmpty()) {
            urlTextArea.setText("");
            loadButton.setEnabled(false);
            return;
        }
        
        String url = String.format("https://%s/api/v1/workspace/stream/%s/labels.fgb?bbox={bbox}",
                selectedServer.domain,
                predictionUid);
                // selectedFormat.value);
        
        urlTextArea.setText(url);
        loadButton.setEnabled(true);
    }
    
    private void updateBboxField() {
        try {
            Bounds bounds = getCurrentViewBounds();
            if (bounds != null) {
                String bbox = String.format("%f,%f,%f,%f", 
                    bounds.getMinLon(), bounds.getMinLat(), 
                    bounds.getMaxLon(), bounds.getMaxLat());
                bboxField.setText(bbox);
            } else {
                bboxField.setText("No map bounds available");
            }
        } catch (Exception e) {
            bboxField.setText("Map not ready");
        }
    }

    private void loadData() {
        String predictionUid = predictionUidField.getText().trim();
        if (predictionUid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Prediction UID", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String url = urlTextArea.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "URL is empty", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Save preferences before loading
        savePreferences();
        
        dispose();

        SwingUtilities.invokeLater(() -> {
            try {
                Bounds bounds = getCurrentViewBounds();
                FAIrLoader loader = new FAIrLoader();
                loader.loadFromURL(url, bounds, "fAIr_" + predictionUid);
                
                JOptionPane.showMessageDialog(MainApplication.getMainFrame(), 
                    "Successfully loaded fAIr prediction data for UID: " + predictionUid + "\n" +
                    "Layer 'fAIr_" + predictionUid + "' has been added to JOSM.", 
                    "Data Loaded Successfully", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                String errorMessage = ex.getMessage();
                if (errorMessage.contains("UnknownHostException") || errorMessage.contains("ConnectException")) {
                    errorMessage = "❌ Cannot connect to fAIr server. Please check your internet connection.";
                } else if (errorMessage.contains("timeout")) {
                    errorMessage = "❌ Request timed out. The fAIr server may be busy. Please try again.";
                } else if (!errorMessage.startsWith("❌")) {
                    errorMessage = "❌ Error loading data: " + errorMessage;
                }
                
                JOptionPane.showMessageDialog(MainApplication.getMainFrame(), 
                    errorMessage, "Loading Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private Bounds getCurrentViewBounds() {
        try {
            if (MainApplication.getMap() != null && MainApplication.getMap().mapView != null) {
                return MainApplication.getMap().mapView.getRealBounds();
            }
        } catch (Exception e) {
            // Map not ready yet
        }
        return null;
    }
    
    /**
     * Load preferences and restore previous values
     */
    private void loadPreferences() {
        // Load prediction UID
        String savedPredictionUid = Config.getPref().get(PREF_PREDICTION_UID, "");
        predictionUidField.setText(savedPredictionUid);
        
        // Load server selection
        int savedServerIndex = Config.getPref().getInt(PREF_SERVER_SELECTION, 0);
        if (savedServerIndex >= 0 && savedServerIndex < serverComboBox.getItemCount()) {
            serverComboBox.setSelectedIndex(savedServerIndex);
        }
        
        // Load format selection
        int savedFormatIndex = Config.getPref().getInt(PREF_FORMAT_SELECTION, 0);
        if (savedFormatIndex >= 0 && savedFormatIndex < formatComboBox.getItemCount()) {
            formatComboBox.setSelectedIndex(savedFormatIndex);
        }
    }
    
    /**
     * Save current preferences
     */
    private void savePreferences() {
        // Save prediction UID
        Config.getPref().put(PREF_PREDICTION_UID, predictionUidField.getText().trim());
        
        // Save server selection
        Config.getPref().putInt(PREF_SERVER_SELECTION, serverComboBox.getSelectedIndex());
        
        // Save format selection
        Config.getPref().putInt(PREF_FORMAT_SELECTION, formatComboBox.getSelectedIndex());
    }
}
