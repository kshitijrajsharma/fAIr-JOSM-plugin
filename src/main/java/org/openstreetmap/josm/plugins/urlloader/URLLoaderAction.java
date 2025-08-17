package org.openstreetmap.josm.plugins.urlloader;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.Shortcut;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class URLLoaderAction extends JosmAction {
    
    public URLLoaderAction() {
        super("Load from fAIr", "urlloader", "Load GeoJSON data from fAIr API",
                Shortcut.registerShortcut("urlloader:load", "Load from fAIr",
                        KeyEvent.VK_F, Shortcut.CTRL_SHIFT), true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            URLLoaderDialog dialog = new URLLoaderDialog(MainApplication.getMainFrame());
            dialog.setVisible(true);
        });
    }
}
