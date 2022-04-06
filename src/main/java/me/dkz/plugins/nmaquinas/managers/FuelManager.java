package me.dkz.plugins.nmaquinas.managers;

import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.models.Fuel;
import org.bukkit.inventory.ItemStack;

public class FuelManager {

    public static Main plugin = Main.getMain;

    public static ItemStack getFuel(String name, int amount){
        for (Fuel fuel : plugin.manager._loadedFuel) {
            if(fuel.getId().equals(name)){
                return fuel.toItemStack(amount);
            }
        }
        return null;
    }

}
