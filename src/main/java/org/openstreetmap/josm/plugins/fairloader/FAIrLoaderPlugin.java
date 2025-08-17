package org.openstreetmap.josm.plugins.fairloader;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class FAIrLoaderPlugin extends Plugin {
    
    public FAIrLoaderPlugin(PluginInformation info) {
        super(info);
        MainMenu mainMenu = MainApplication.getMenu();
        mainMenu.toolsMenu.add(new FAIrLoaderAction());
    }
}
