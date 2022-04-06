package me.dkz.plugins.nmaquinas.languages;

import me.dkz.plugins.nmaquinas.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class LanguageManager {

    public Main plugin = Main.getMain;
    private File file;
    private YamlConfiguration yml;

    public LanguageManager(String language){
        File file = new File(Main.getMain.getDataFolder()+"/languages", language+".yml");
        yml = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean isValidLanguageFile(String lang){
        return new File(Main.getMain.getDataFolder()+"/languages", lang+".yml").exists();
    }

    public String getStringMessage(String name){
        return yml.getString(name).replaceAll("&", "§");
    }

    public ArrayList<String> getStringListMessage(String name){
        return (ArrayList<String>) yml.getStringList(name).stream().map(str -> str.replaceAll("&", "§")).collect(Collectors.toList());
    }

    public YamlConfiguration getYml() {
        return yml;
    }
}
