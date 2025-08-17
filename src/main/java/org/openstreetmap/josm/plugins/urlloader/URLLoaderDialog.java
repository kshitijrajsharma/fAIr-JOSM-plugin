package org.openstreetmap.josm.plugins.urlloader;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class URLLoaderDialog extends JDialog {
    
    private JTextField urlField;
    private JButton loadButton;
    private JButton cancelButton;

    public URLLoaderDialog(Frame parent) {
        super(parent, "Load Data from URL", true);
        initComponents();
        setupLayout();
        setupListeners();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        urlField = new JTextField(50);
        loadButton = new JButton("Load");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 5);
        mainPanel.add(new JLabel("URL:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 5, 10);
        mainPanel.add(urlField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loadButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
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

        urlField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
    }

    private void loadData() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a URL", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();

        SwingUtilities.invokeLater(() -> {
            try {
                Bounds bounds = getCurrentViewBounds();
                URLLoader loader = new URLLoader();
                loader.loadFromURL(url, bounds);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainApplication.getMainFrame(), 
                    "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
