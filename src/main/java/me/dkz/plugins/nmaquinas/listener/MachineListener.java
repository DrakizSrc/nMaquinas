package me.dkz.plugins.nmaquinas.listener;

import de.tr7zw.nbtapi.NBTItem;
import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.economy.VaultAPI;
import me.dkz.plugins.nmaquinas.menu.MainMachine;
import me.dkz.plugins.nmaquinas.models.Machine;
import me.dkz.plugins.nmaquinas.models.WMachine;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class MachineListener implements Listener {

    public static Main plugin = Main.getMain;

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        ItemStack inHand = player.getItemInHand();
        Block block = e.getBlockPlaced();

        if (!inHand.hasItemMeta()) return;
        if (!inHand.getItemMeta().hasDisplayName()) return;

        for (Machine machine : plugin.manager._loadedMachines) {
            if (inHand.getItemMeta().getDisplayName().equals(machine.getName())) {
                player.sendMessage(plugin.prefix + " §aVocê posicionou a " + machine.getName() + "§a com sucesso.");
                if(!inHand.getType().equals(machine.getBlock().getType())){
                    block.setType(machine.getBlock().getType());
                }
                WMachine wMachine = _buildWorldMachine(machine, player, block);

                NBTItem nbti = new NBTItem(player.getItemInHand());
                if(nbti.hasKey("Level")){
                    wMachine.set_level(nbti.getInteger("Level"));
                    wMachine.setAmount(nbti.getInteger("Amount"));
                    wMachine.setFuel(nbti.getInteger("Fuel"));
                    block.setMetadata("Tipo", new FixedMetadataValue(plugin, machine.getId()));
                    block.setMetadata("Dono", new FixedMetadataValue(plugin, player.getDisplayName()));
                    block.setMetadata("Quantidade", new FixedMetadataValue(plugin, nbti.getInteger("Amount")));
                    block.setMetadata("Nivel", new FixedMetadataValue(plugin, nbti.getInteger("Level")));
                    wMachine.set_updateValue((int) (wMachine.get_level() * wMachine.get_updatePrice()));
                }else {
                    wMachine.setFuel(0);
                    wMachine.setAmount(1);
                    block.setMetadata("Tipo", new FixedMetadataValue(plugin, machine.getId()));
                    block.setMetadata("Dono", new FixedMetadataValue(plugin, player.getDisplayName()));
                    block.setMetadata("Quantidade", new FixedMetadataValue(plugin, 1));
                    block.setMetadata("Nivel", new FixedMetadataValue(plugin, 1));
                }
                wMachine.createHologram();
                plugin.manager.update(wMachine);
                break;
            }
        }

    }


    public WMachine _buildWorldMachine(Machine machine, Player player, Block block) {
        WMachine wMachine = new WMachine(machine.getName());
        wMachine.setIcon(machine.getIcon());
        wMachine.setLocation(block.getLocation());
        wMachine.setPrice(machine.getPrice());
        wMachine.setOwer(player.getDisplayName());
        wMachine.setDrops(machine.getDrop());
        wMachine.setFuelType(machine.getFuelType());
        wMachine.set_updatePrice(machine.get_updatePrice());
        wMachine.set_maxLevel(machine.get_maxLevel());
        wMachine.setDelay(machine.getDelay());
        wMachine.setDropsValue(machine.getDropValue());
        wMachine.setId(machine.getId());
        wMachine.setTime(machine.getTime());
        wMachine.set_velocity(machine.get_velocity());
        wMachine.set_drops(machine.get_drops());
        wMachine.set_consumption(machine.get_consumption());
        wMachine.setBlock(machine.getBlock());
        wMachine.set_updateValue((int) (wMachine.get_level() * machine.get_updatePrice()));
        plugin.manager.saveMachine(wMachine);

        return wMachine;
    }


    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block b = e.getClickedBlock();
        if (!b.hasMetadata("Dono")) return;
        Player player = e.getPlayer();
        for (WMachine _wMachine : plugin.manager._inWMachines) {
            if (_wMachine.getLocation().equals(b.getLocation())) {
                if (_wMachine.getOwer().equals(player.getDisplayName())) {
                    if (player.getItemInHand().hasItemMeta()) {
                        if (player.getItemInHand().getItemMeta().hasDisplayName()) {
                            NBTItem mac = new NBTItem(_wMachine.toItemStack(player, _wMachine.get_level()));
                            if(mac.getInteger("Level").equals(new NBTItem(player.getItemInHand()).getInteger("Level"))) {
//                            if (player.getItemInHand().equals(_worldMachine.toItemStack(player, player.getItemInHand().getAmount(), _worldMachine.get_level()))) {
                                if (Main.getMain.getConfig().getBoolean("Maquinas." + _wMachine.getId() + ".Stack")) {
                                    int addval = new NBTItem(player.getItemInHand()).getInteger("Amount");
                                        if (_wMachine.getAmount() + addval <= plugin.getConfig().getInt("Maquinas." + _wMachine.getId() + ".Stack-Máximo")) {
                                            if (player.getItemInHand().getAmount() > 1) {
                                                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                                            } else {
                                                player.setItemInHand(null);
                                            }
                                            _wMachine.setAmount(_wMachine.getAmount() + addval);

                                        } else {
                                            player.sendMessage(Main.getMain.languageManager.getStringMessage("MAQUINA-STACK-CHEIO"));
                                            e.setCancelled(true);
                                            return;
                                        }

                                    plugin.manager.update(_wMachine);
                                    _wMachine.createHologram();

                                    b.setMetadata("Quantidade", new FixedMetadataValue(plugin, _wMachine.getAmount()));
                                    e.setCancelled(true);
                                    return;
                                } else {
                                    player.sendMessage(plugin.prefix + "§c Essa maquina não pode ser stackada.");
                                    e.setCancelled(true);
                                    return;
                                }
                            }


                            for (String id : plugin.getConfig().getConfigurationSection("Combustiveis").getKeys(false)) {
                                if (plugin.getConfig().getString("Combustiveis." + id + ".Nome").replaceAll("&", "§").equals(player.getItemInHand().getItemMeta().getDisplayName())) {
                                    int addFuel = plugin.getConfig().getInt("Combustiveis." + id + ".Quantidade");
                                    if (_wMachine.getFuelType().equals(plugin.getConfig().getString("Combustiveis." + id + ".Tipo"))) {


                                        if(player.isSneaking()){
                                            if ((_wMachine.getFuel() + (addFuel * player.getItemInHand().getAmount())) <= _wMachine.getTime()) {
                                                _wMachine.setFuel(_wMachine.getFuel() + (addFuel * player.getItemInHand().getAmount()));
                                                plugin.manager.update(_wMachine);
                                                _wMachine.createHologram();
                                                player.setItemInHand(null);
                                                e.setCancelled(true);
                                                return;
                                            } else {
                                                player.sendMessage(plugin.languageManager.getStringMessage("MAQUINA-CHEIA"));
                                            }
                                        }else{
                                            if ((_wMachine.getFuel() + addFuel) <= _wMachine.getTime()) {
                                                _wMachine.setFuel(_wMachine.getFuel() + addFuel);
                                                plugin.manager.update(_wMachine);
                                                _wMachine.createHologram();

                                                if (player.getItemInHand().getAmount() > 1) {
                                                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                                                } else {
                                                    player.setItemInHand(null);
                                                }
                                                e.setCancelled(true);
                                                return;
                                            } else {
                                                player.sendMessage(plugin.languageManager.getStringMessage("MAQUINA-CHEIA"));
                                            }
                                        }


                                    } else {
                                        player.sendMessage(plugin.languageManager.getStringMessage("COMBUSTIVEL-INCORRETO"));
                                    }
                                }
                            }
                        }
                    }
                    MainMachine gui = new MainMachine(_wMachine);
                    gui.openInventory(player);
                } else {
                    player.sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO-MAQUINA"));
                    e.setCancelled(true);
                    break;
                }
            }


        }


        e.setCancelled(true);
        e.setUseInteractedBlock(Event.Result.DENY);


    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if (!(block.hasMetadata("Tipo"))) return;
        for (WMachine machine : plugin.manager._inWMachines) {
            if (block.getLocation().equals(machine.getLocation())) {
                if (machine.getOwer().equals(player.getDisplayName())) {
                    player.sendMessage(plugin.prefix + " §aVocê removeu a " + machine.getName() + "§a com sucesso.");

                    if(machine.getItens() > 0) {
                        VaultAPI.economy.depositPlayer(player, machine.getItens() * machine.getDropValue());
                    }

                    machine.stopMachine();
                    plugin.manager._inWMachines.remove(machine);
                    plugin.manager.removeMachine(machine);
                    machine.deleteHologram();

                    block.removeMetadata("Tipo", plugin);
                    block.removeMetadata("Combustivel", plugin);
                    block.removeMetadata("Dono", plugin);
                    e.getBlock().setType(Material.AIR);
                    player.getInventory().addItem(machine.toItemStack(player, block.getMetadata("Nivel").get(0).asInt()));
                    block.removeMetadata("Quantidade", plugin);
                    block.removeMetadata("Nivel", plugin);
                } else {
                    player.sendMessage(plugin.languageManager.getStringMessage("SEM-PERMISSÃO-MAQUINA"));
                    e.setCancelled(true);
                }
                break;
            }
            e.setCancelled(true);

        }
    }
}
