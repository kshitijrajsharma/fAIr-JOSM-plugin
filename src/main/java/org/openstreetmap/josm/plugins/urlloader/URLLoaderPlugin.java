package org.openstreetmap.josm.plugins.urlloader;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class URLLoaderPlugin extends Plugin {
    
    public URLLoaderPlugin(PluginInformation info) {
        super(info);
        MainMenu mainMenu = MainApplication.getMenu();
        mainMenu.toolsMenu.add(new URLLoaderAction());
    }
}
