package me.dkz.plugins.nmaquinas.menu;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import me.dkz.plugins.nmaquinas.economy.VaultAPI;
import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.utils.MachineUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class MachineGlobal extends SimpleInventory {

    public static Main plugin = Main.getMain;

    public MachineGlobal() {
        super("nmaquinas.machine.globalgui", "Maquinas - Menu Principal", 9*3);
    }

    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        ItemStack playerInfoItem = new ItemStack(Material.getMaterial(plugin.languageManager.getYml().getString("Menus.Informações.Player.Icone")));
        ItemMeta playerInfoMeta = playerInfoItem.getItemMeta();
        playerInfoMeta.setDisplayName(plugin.languageManager.getYml().getString("Menus.Informações.Player.Nome").replaceAll("&", "§"));
        playerInfoMeta.setLore(plugin.languageManager.getYml().getStringList("Menus.Informações.Player.Descrição").stream().map(str ->
                str.replaceAll("&", "§")
        .replaceAll("@Maquinas", MachineUtils.format(MachineUtils.getMachinesInWorld(viewer.getPlayer())))
        .replaceAll("@Gastos", VaultAPI.economy.format(MachineUtils.getMachinesInWorldValue(viewer.getPlayer())))
        .replaceAll("@Money", VaultAPI.economy.format(VaultAPI.economy.getBalance(viewer.getPlayer())))).collect(Collectors.toList()));
        playerInfoItem.setItemMeta(playerInfoMeta);
        editor.setItem(10, InventoryItem.of(playerInfoItem));

        ItemStack dropIcon = new ItemStack(Material.getMaterial(plugin.languageManager.getYml().getString("Menus.Informações.Drops.Icone")));
        ItemMeta dropIconMeta = dropIcon.getItemMeta();;
        dropIconMeta.setDisplayName(plugin.languageManager.getYml().getString("Menus.Informações.Drops.Nome").replaceAll("&", "§"));
        dropIconMeta.setLore(plugin.languageManager.getYml().getStringList("Menus.Informações.Drops.Descrição").stream().map(str -> str.replaceAll("&", "§")).collect(Collectors.toList()));
        dropIcon.setItemMeta(dropIconMeta);

        editor.setItem(14, InventoryItem.of(dropIcon).callback(ClickType.LEFT, click ->{
            if(click.getPlayer().hasPermission("nmaquinas.drops")) {
                MachineDrops dropsGUI = new MachineDrops().init();
                dropsGUI.openInventory(click.getPlayer());
            }else{
                click.getPlayer().sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO"));
            }
        }));


        ItemStack machineIcon = new ItemStack(Material.getMaterial(plugin.languageManager.getYml().getString("Menus.Informações.Maquinas.Icone")));
        ItemMeta machineIconMeta = machineIcon.getItemMeta();;
        machineIconMeta.setDisplayName(plugin.languageManager.getYml().getString("Menus.Informações.Maquinas.Nome").replaceAll("&", "§"));
        machineIconMeta.setLore(plugin.languageManager.getYml().getStringList("Menus.Informações.Maquinas.Descrição").stream().map(str -> str.replaceAll("&", "§")).collect(Collectors.toList()));
        machineIcon.setItemMeta(machineIconMeta);

        editor.setItem(15, InventoryItem.of(machineIcon).callback(ClickType.LEFT, click ->{
            if(plugin.getConfig().getBoolean("Informações.Maquinas.Vender")) {
                MachineShop shopGUI = new MachineShop().init();
                shopGUI.openInventory(click.getPlayer());
            }
        }));

        ItemStack fuelIcon = new ItemStack(Material.getMaterial(plugin.languageManager.getYml().getString("Menus.Informações.Combustiveis.Icone")));
        ItemMeta fuelIconMeta = fuelIcon.getItemMeta();;
        fuelIconMeta.setDisplayName(plugin.languageManager.getYml().getString("Menus.Informações.Combustiveis.Nome").replaceAll("&", "§"));
        fuelIconMeta.setLore(plugin.languageManager.getYml().getStringList("Menus.Informações.Combustiveis.Descrição").stream().map(str -> str.replaceAll("&", "§")).collect(Collectors.toList()));
        fuelIcon.setItemMeta(fuelIconMeta);

        editor.setItem(16, InventoryItem.of(fuelIcon).callback(ClickType.LEFT, click ->{
            if(plugin.getConfig().getBoolean("Informações.Maquinas.Vender")) {
                FuelShop fuelShopMenu = new FuelShop().init();
                fuelShopMenu.openInventory(click.getPlayer());
            }
        }));

    }



    }
