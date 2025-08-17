package org.openstreetmap.josm.plugins.fairloader;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FAIrLoaderDialog extends JDialog {
    
    private JTextField predictionUidField;
    private JComboBox<ServerOption> serverComboBox;
    private JComboBox<FormatOption> formatComboBox;
    private JTextArea urlTextArea;
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
        gbc.insets = new Insets(15, 10, 5, 5);
        mainPanel.add(new JLabel("Generated URL:"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
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
        
        if (predictionUid.isEmpty()) {
            urlTextArea.setText("");
            loadButton.setEnabled(false);
            return;
        }
        
        String url = String.format("https://%s/api/v1/workspace/stream/%s/labels.fgb?bbox={bbox}&format=%s",
                selectedServer.domain,
                predictionUid,
                selectedFormat.value);
        
        urlTextArea.setText(url);
        loadButton.setEnabled(true);
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
        MapView mapView = MainApplication.getMap().mapView;
        if (mapView != null) {
            return mapView.getRealBounds();
        }
        return null;
    }
}
