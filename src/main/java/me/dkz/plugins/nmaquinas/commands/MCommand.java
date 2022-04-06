package me.dkz.plugins.nmaquinas.commands;

import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.menu.MachineGlobal;
import me.dkz.plugins.nmaquinas.models.Machine;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MCommand implements CommandExecutor {

    public static Main plugin = Main.getMain;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {




        if(!(args.length > 0)) {
                if (sender instanceof Player) {
                    MachineGlobal machineGlobalMenu = new MachineGlobal().init();
                    Player player = (Player) sender;
                    machineGlobalMenu.openInventory(player);
                }

            return false;
        }

        switch (args[0]){
            case "get":
                if(!(args.length > 2)) return false;
                if(!(sender instanceof Player)) {sender.sendMessage("§b[nmaquinas] §cApenas jogadores executam esse comadno."); return false;};
                Player player = (Player) sender;

                if(!sender.hasPermission("nmaquinas.give")){
                    sender.sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO"));
                    return false;
                }

                try {
                    if (isValid(args[1]) != null) {
                        player.getInventory().addItem(isValid(args[1]).toItemStack(player, Integer.parseInt(args[2])));
                        player.sendMessage(Main.getMain.prefix+" §aVocê obteve §e\""+isValid(args[1]).getName()+"§e\" §acom sucesso.");
                    } else {
                        player.sendMessage(Main.getMain.prefix+"§c A maquina \"" + args[1] + "\" não foi econtrada.");
                        sendMachineInvalidMessage(sender);
                        return false;
                    }
                }catch (Exception ex){
                    sender.sendMessage(Main.getMain.prefix+" §cOcorreu um erro ao receber esse item, tente novamente.");
                    return false;                }
                break;
            case "give":
                if(!(args.length > 3)) return false;
                if(isValid(args[2]) != null) {
                    if(Bukkit.getPlayer(args[1]) != null) {

                        if(!sender.hasPermission("nmaquinas.give")){
                            sender.sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO"));
                            return false;
                        }

                        try {
                            Player player1 = Bukkit.getPlayer(args[1]);
                            player1.getInventory().addItem(isValid(args[2]).toItemStack(player1, Integer.parseInt(args[3])));
                            player1.sendMessage(Main.getMain.prefix+" §aVocê obteve §e\""+isValid(args[2]).getName()+"§e\" §acom sucesso.");
                        }catch (Exception ex){
                            sender.sendMessage(Main.getMain.prefix+" §cOcorreu um erro ao receber esse item, tente novamente.");
                            return false;
                        }

                    }else{
                        sender.sendMessage(Main.getMain.prefix + " §cEsse jogador não foi encontrado.");
                        return false;
                    }
                }else{
                    sender.sendMessage(Main.getMain.prefix+"§c A maquina \"" + args[2] + "\" não foi econtrada.");
                    sendMachineInvalidMessage(sender);
                }
                break;
            case "listar":
                if(!sender.hasPermission("nmaquinas.give")){
                    sender.sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO"));
                    return false;
                }
                sendMachineInvalidMessage(sender);
                break;
            default:
                if(sender.hasPermission("nmaquinas.give")) {
                    for (String str : plugin.languageManager.getStringListMessage("COMANDO-INCORRETO")) {
                        sender.sendMessage(str);
                    }
                }else{
                    MachineGlobal machineGlobalMenu = new MachineGlobal().init();
                    Player player2 = (Player) sender;
                    machineGlobalMenu.openInventory(player2);
                }
                return false;

        }

        return false;
    }

    Machine isValid(String id){
        for(Machine load : plugin.manager._loadedMachines){
            if(load.getId().equals(id)){
                return load;
            }
        }
        return null;
    }

    void sendMachineInvalidMessage(CommandSender sender){
        ArrayList<String> load = new ArrayList<>();
        for(Machine loaded : plugin.manager._loadedMachines){
            load.add(loaded.getId());
        }
        sender.sendMessage("§eMaquinas Disponiveis: §7"+load);
    }
}
