package me.dkz.plugins.nmaquinas.menu;

import me.dkz.plugins.nmaquinas.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MenuManager {

    private Main plugin;
    private File file;
    private YamlConfiguration yml;

    public MenuManager(Main plugin){
        this.plugin = plugin;
    }


    public YamlConfiguration getYml(String menu) {
        file = new File(plugin.getDataFolder()+"/menus", menu+".yml");
        yml = YamlConfiguration.loadConfiguration(file);
        return yml;
    }
}
