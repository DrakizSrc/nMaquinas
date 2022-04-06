package me.dkz.plugins.nmaquinas.commands;

import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.managers.FuelManager;
import me.dkz.plugins.nmaquinas.menu.FuelShop;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FCommand implements CommandExecutor {

    public Main plugin = Main.getMain;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(args.length > 0)) {
            if (sender.hasPermission("nmaquinas.comprar")) {
                if (sender instanceof Player) {
                    FuelShop fuelShopMenu = new FuelShop().init();
                    Player player = (Player) sender;
                    fuelShopMenu.openInventory(player);
                    return false;
                }
            } else {
                sender.sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO"));
            }
            return false;

        }

        if (!sender.hasPermission("nmaquinas.give")) {
            sender.sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO"));
            return false;
        }

        switch (args[0]) {
            case "get":
                if (!(args.length > 2)) return false;
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§b[nMaquinas] §cApenas jogadores executam esse comadno.");
                    return false;
                }
                ;
                Player player = (Player) sender;
                try {
                    if (isValid(args[1])) {
                        player.getInventory().addItem(FuelManager.getFuel(args[1], Integer.parseInt(args[2])));
                    } else {
                        sender.sendMessage(Main.getMain.prefix + "§c O Combustivel \"" + args[1] + "\" não foi econtrado.");
                        sendFuelInvalidMessage(sender);
                        return false;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    sender.sendMessage(Main.getMain.prefix + " §cOcorreu um erro ao receber esse item, tente novamente.");
                    return false;
                }
                break;
            case "give":
                if (!(args.length > 3)) return false;
                if (isValid(args[2])) {
                    try {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            Bukkit.getPlayer(args[1]).getInventory().addItem(FuelManager.getFuel(args[2], Integer.parseInt(args[3])));
                            Bukkit.getPlayer(args[1]).sendMessage(Main.getMain.prefix + " §aVocê obteve §e\"" + args[2] + "§e\" §acom sucesso.");
                        } else {
                            sender.sendMessage(Main.getMain.prefix + " §cEsse jogador não foi encontrado.");
                            return false;
                        }
                    } catch (Exception ex) {
                        sender.sendMessage(Main.getMain.prefix + " §cOcorreu um erro ao receber esse item, tente novamente.");
                        return false;
                    }

                } else {
                    sender.sendMessage(Main.getMain.prefix + "§c O Combustivel \"" + args[2] + "\" não foi econtrado.");
                    sendFuelInvalidMessage(sender);
                }
                break;
            case "listar":
                sendFuelInvalidMessage(sender);
                break;
        }

        return false;
    }

    void sendFuelInvalidMessage(CommandSender sender) {
        ArrayList<String> load = new ArrayList<>();
        for (String ids : plugin.getConfig().getConfigurationSection("Combustiveis").getKeys(false)) {
            load.add(ids);
        }
        sender.sendMessage("§eCombustiveis Disponiveis: §7" + load);
    }


    boolean isValid(String name) {
        return plugin.getConfig().getConfigurationSection("Combustiveis").getKeys(false).contains(name);
    }
}
