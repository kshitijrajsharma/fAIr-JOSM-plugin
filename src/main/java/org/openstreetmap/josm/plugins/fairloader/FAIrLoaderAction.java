package org.openstreetmap.josm.plugins.fairloader;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.Shortcut;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FAIrLoaderAction extends JosmAction {
    
    public FAIrLoaderAction() {
        super("Load from fAIr", "fairloader", "Load GeoJSON data from fAIr API",
                Shortcut.registerShortcut("fairloader:load", "Load from fAIr",
                        KeyEvent.VK_F, Shortcut.CTRL_SHIFT), true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            FAIrLoaderDialog dialog = new FAIrLoaderDialog(MainApplication.getMainFrame());
            dialog.setVisible(true);
        });
    }
}
