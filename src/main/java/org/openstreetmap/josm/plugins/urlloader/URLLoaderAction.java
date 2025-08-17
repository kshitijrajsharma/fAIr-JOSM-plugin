package org.openstreetmap.josm.plugins.urlloader;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.Shortcut;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class URLLoaderAction extends JosmAction {
    
    public URLLoaderAction() {
        super("Load from URL", "urlloader", "Load GeoJSON data from URL",
                Shortcut.registerShortcut("urlloader:load", "Load from URL",
                        KeyEvent.VK_U, Shortcut.CTRL_SHIFT), true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            URLLoaderDialog dialog = new URLLoaderDialog(MainApplication.getMainFrame());
            dialog.setVisible(true);
        });
    }
}
