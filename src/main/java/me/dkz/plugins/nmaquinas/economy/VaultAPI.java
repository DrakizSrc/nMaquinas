package me.dkz.plugins.nmaquinas.economy;

import me.dkz.plugins.nmaquinas.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI {

    public static Economy economy;

    public static boolean setupEconomy() {
        if (Main.getMain.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Main.getMain.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

}
