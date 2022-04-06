package me.dkz.plugins.nmaquinas.listener;

import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.economy.VaultAPI;
import me.dkz.plugins.nmaquinas.models.Machine;
import me.dkz.plugins.nmaquinas.utils.MachineUtils;
import me.dkz.plugins.nmaquinas.models.Fuel;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.HashMap;

public class ChatShopEvent implements Listener {

    public static HashMap<Player, Machine> buyMachine = new HashMap<>();
    public static HashMap<Player, Fuel> buyFuel = new HashMap<>();
    public static Main plugin = Main.getMain;


    @EventHandler
    void onChatMessage(PlayerChatEvent e) {



        if (buyMachine.containsKey(e.getPlayer())) {
            try {
                if (!e.getMessage().equalsIgnoreCase("cancelar")) {
                    if (NumberUtils.isDigits(e.getMessage()) && Integer.parseInt(e.getMessage()) > 0) {
                        double price = Integer.parseInt(e.getMessage()) * buyMachine.get(e.getPlayer()).getPrice();
                        if (price <= VaultAPI.economy.getBalance(e.getPlayer())) {
                            if (buyMachine.get(e.getPlayer()).getMaxStack() >= Integer.parseInt(e.getMessage())) {
                                e.getPlayer().getInventory().addItem(buyMachine.get(e.getPlayer()).toItemStack(e.getPlayer(), Integer.parseInt(e.getMessage())));
                                buyMachine.remove(e.getPlayer());

                                e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("SUCESSO-COMPRA").replaceAll("@Quantidade", MachineUtils.format(Long.parseLong(e.getMessage())))
                                        .replaceAll("@Compra", "maquina(s)").replaceAll("@Valor", VaultAPI.economy.format(price)));
                                VaultAPI.economy.withdrawPlayer(e.getPlayer(), price);
                            } else {
                                e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("FALHA-COMPRA-STACK"));
                            }
                        } else {
                            e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("FALHA-COMPRA-DINHEIRO").replaceAll("@Valor", VaultAPI.economy.format(price)));
                        }
                    } else {
                        e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("FALHA-COMPRA-VALOR"));
                    }
                } else {
                    e.getPlayer().sendMessage(Main.getMain.prefix + " §cVocê cancelou a compra da maquina.");
                    buyMachine.remove(e.getPlayer());
                }

                e.setCancelled(true);
            } catch (Exception ex) {
                e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("FALHA-COMPRA-VALOR"));
                e.setCancelled(true);
            }

        }


        if (buyFuel.containsKey(e.getPlayer())) {
            try {
                if (!e.getMessage().equalsIgnoreCase("cancelar")) {
                    if (NumberUtils.isDigits(e.getMessage()) && Integer.parseInt(e.getMessage()) > 0) {
                        double price = Integer.parseInt(e.getMessage()) * buyFuel.get(e.getPlayer()).getValue();
                        if (price <= VaultAPI.economy.getBalance(e.getPlayer())) {
                            e.getPlayer().getInventory().addItem(buyFuel.get(e.getPlayer()).toItemStack(Integer.parseInt(e.getMessage())));
                            buyFuel.remove(e.getPlayer());

                            e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("SUCESSO-COMPRA").replaceAll("@Quantidade", MachineUtils.format(Long.parseLong(e.getMessage())))
                            .replaceAll("@Compra", "combustiveis").replaceAll("@Valor", VaultAPI.economy.format(price)));

                            VaultAPI.economy.withdrawPlayer(e.getPlayer(), price);

                        } else {
                            e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("FALHA-COMPRA-DINHEIRO").replaceAll("@Valor", VaultAPI.economy.format(price)));

                        }
                    } else {
                        e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("FALHA-COMPRA-VALOR"));
                    }
                } else {
                    e.getPlayer().sendMessage(Main.getMain.prefix + " §cVocê cancelou a compra do combustivel.");
                    buyFuel.remove(e.getPlayer());
                }
                e.setCancelled(true);
            } catch (Exception ex) {
                e.getPlayer().sendMessage(plugin.languageManager.getStringMessage("FALHA-COMPRA-VALOR"));
                e.setCancelled(true);
            }

        }
    }
}
