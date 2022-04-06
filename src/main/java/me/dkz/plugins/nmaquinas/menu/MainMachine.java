package me.dkz.plugins.nmaquinas.menu;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.event.impl.CustomInventoryClickEvent;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.ViewerConfiguration;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.simple.SimpleViewer;
import me.dkz.plugins.nmaquinas.economy.VaultAPI;
import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.models.WMachine;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class MainMachine extends SimpleInventory {

    public WMachine machine;
    public static Main plugin = Main.getMain;


    public MainMachine(WMachine machine){
        super(machine.getId(), machine.getName(), 9 * 3);
        this.machine = machine;
        configuration(configuration -> {
            configuration.secondUpdate(1);
        });
    }




    @Override
    protected void configureViewer(SimpleViewer viewer) {
        ViewerConfiguration configuration = viewer.getConfiguration();

    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        super.configureInventory(viewer, editor);
        ItemStack onButton = null;
        if(plugin.menuManager.getYml("Maquina").getString("Status.Icone").equals("MAQUINA")) {
            onButton = machine.getBlock();
        }else{
            onButton = new ItemStack(Material.getMaterial(plugin.menuManager.getYml("Maquina").getString("Status.Icone")));
        }
        ItemMeta onMeta = onButton.getItemMeta();
        int percentage = machine.getFuel() * 100 / machine.getTime();
        double b = Math.round(percentage * 10.0) / 10.0;
        onMeta.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Status.Nome").replaceAll("&", "§"));
        onMeta.setLore(plugin.menuManager.getYml("Maquina").getStringList("Status.Descrição").stream().map(str -> str.replaceAll("&", "§")
                .replaceAll("@Status", machine.status).replaceAll("@Tempo", machine.getProgressBar(machine.getFuel(), machine.getTime(), 15, '|', ChatColor.GREEN, ChatColor.RED) + "§8 - "+b+"%")
                .replaceAll("@Quantidade", format(machine.getAmount()))
                .replaceAll("@Nivel", String.valueOf(machine.get_level()))).collect(Collectors.toList()));
        onButton.setItemMeta(onMeta);


        editor.setItem(plugin.menuManager.getYml("Maquina").getInt("Status.Slot"), InventoryItem.of(onButton).callback(ClickType.LEFT, toggle -> {
            if (machine.running) {
                machine.stopMachine();
            } else {
                machine.runMachine();
            }
        }).updateCallback(itemStack -> {
            ItemMeta onMeta2 = itemStack.getItemMeta();
            int percentageu = machine.getFuel() * 100 / machine.getTime();
            double bu = Math.round(percentageu * 10.0) / 10.0;


            onMeta.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Status.Nome").replaceAll("&", "§"));
            onMeta.setLore(plugin.menuManager.getYml("Maquina").getStringList("Status.Descrição").stream().map(str -> str.replaceAll("&", "§")
                    .replaceAll("@Status", machine.status).replaceAll("@Tempo", machine.getProgressBar(machine.getFuel(), machine.getTime(), 15, '|', ChatColor.GREEN, ChatColor.RED) + "§8 - "+bu+"%")
                    .replaceAll("@Quantidade", format(machine.getAmount()))
                    .replaceAll("@Nivel", String.valueOf(machine.get_level()))).collect(Collectors.toList()));
            itemStack.setItemMeta(onMeta);

//            onMeta2.setDisplayName(plugin.languageManager.getYml().getString("Menus.Maquina.Status.Nome").replaceAll("&", "§"));
//            onMeta2.setLore(plugin.languageManager.getYml().getStringList("Menus.Maquina.Status.Descrição").stream().map(str -> str.replaceAll("&", "§")
//                    .replaceAll("@Status", machine.status).replaceAll("@Tempo", machine.getProgressBar(machine.getFuel(), machine.getTime(), 15, '|', ChatColor.GREEN, ChatColor.RED) + "§8 - "+b+"%")
//                    .replaceAll("@Quantidade", format(machine.getAmount()))
//                    .replaceAll("@Nivel", String.valueOf(machine.get_level()))).collect(Collectors.toList()));
//            itemStack.setItemMeta(onMeta);
        }));

        //Drops
        ItemStack items = null;
        if(machine.getItens() == 0) {
            items = new ItemStack(Material.getMaterial(plugin.menuManager.getYml("Maquina").getString("Drops.SEM-ITENS.Icone")));
        }else{
            if(plugin.menuManager.getYml("Maquina").getString("Drops.COM-ITENS.Icone").equals("DROP")){
                items = new ItemStack(machine.getDrop());
            }else{
                items = new ItemStack(Material.getMaterial(plugin.menuManager.getYml("Maquina").getString("Drops.COM-ITENS.Icone")));
            }
        }



        ItemMeta itemsMeta = items.getItemMeta();
        if(machine.getItens() > 0) {
            itemsMeta.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Drops.COM-ITENS.Nome").replaceAll("&", "§"));
            itemsMeta.setLore(plugin.menuManager.getYml("Maquina").getStringList("Drops.COM-ITENS.Descrição").stream().map(str -> str.replaceAll("&", "§")
                    .replaceAll("@Quantidade", format(machine.getItens())).replaceAll("@Valor", VaultAPI.economy.format(machine.getSellValue()))
             ).collect(Collectors.toList()));

            items.setAmount(machine.getItens());
        }else{
            itemsMeta.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Drops.SEM-ITENS.Nome").replaceAll("&", "§"));
        }
        items.setItemMeta(itemsMeta);

        //Informaçõões
        if (Main.getMain.getConfig().getBoolean("Informações.Maquinas.Armazem")) {
                editor.setItem(plugin.menuManager.getYml("Maquina").getInt("Drops.Slot"), InventoryItem.of(items).callback(ClickType.LEFT, sell -> {
                    sellMachineDrops(sell, machine, plugin);
                }).updateCallback(itemStack -> {
                        if(machine.getItens() > 0) {

                            itemStack.setType(machine.getDrop().getType());
                            if(plugin.menuManager.getYml("Maquina").getString("Drops.COM-ITENS.Icone").equals("DROP")){
                                itemStack.setType(machine.getDrop().getType());
                            }else{
                                itemStack.setType(Material.getMaterial(plugin.menuManager.getYml("Maquina").getString("Drops.COM-ITENS.Icone")));
                            }

                            itemStack.setAmount(machine.getItens());
                            ItemMeta itemStackMeta = itemStack.getItemMeta();
                            itemStackMeta.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Drops.COM-ITENS.Nome").replaceAll("&", "§"));
                            itemStackMeta.setLore(plugin.menuManager.getYml("Maquina").getStringList("Drops.COM-ITENS.Descrição").stream().map(str -> str.replaceAll("&", "§")
                                    .replaceAll("@Quantidade", format(machine.getItens())).replaceAll("@Valor", VaultAPI.economy.format(machine.getSellValue()))
                            ).collect(Collectors.toList()));

//                            items.setAmount(machine.getItens());
//
//                            ItemMeta itemStackMeta = itemStack.getItemMeta();
//                            itemStackMeta.setDisplayName(plugin.languageManager.getYml().getString("Menus.Maquina.Drops.COM-ITENS.Nome").replaceAll("&", "§"));
//                            itemStackMeta.setLore(plugin.languageManager.getYml().getStringList("Menus.Maquina.Drops.COM-ITENS.Descrição").stream().map(str -> str.replaceAll("&", "§")
//                                    .replaceAll("@Quantidade", format(machine.getItens())).replaceAll("@Valor", VaultAPI.economy.format(machine.getSellValue()))
//                            ).collect(Collectors.toList()));
                            itemStack.setItemMeta(itemStackMeta);
                        }else{
                            ItemMeta mt = itemStack.getItemMeta();
                            mt.setLore(null);
                            mt.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Drops.SEM-ITENS.Nome").replaceAll("&", "§"));
                            itemStack.setItemMeta(mt);
                            itemStack.setType(Material.getMaterial(plugin.menuManager.getYml("Maquina").getString("Drops.SEM-ITENS.Icone")));
                        }


                }));

        }

        // Hologram
        if (Main.getMain._useHologram) {
            ItemStack onHolo = new ItemStack(Material.getMaterial(plugin.menuManager.getYml("Maquina").getString("Holograma.Icone")));
            ItemMeta onHoloMeta = onHolo.getItemMeta();
            String s = "§cDesativado";
            if (machine.getUseHologram()) {
                s = "§aAtivado";
            }
            onHoloMeta.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Holograma.Nome").replaceAll("&", "§"));
            String finalS = s;
            onHoloMeta.setLore(plugin.menuManager.getYml("Maquina").getStringList("Holograma.Descrição").stream().map(str -> str.replaceAll("&", "§")
                    .replaceAll("@Status", finalS)).collect(Collectors.toList()));
            onHolo.setItemMeta(onHoloMeta);
            editor.setItem(plugin.menuManager.getYml("Maquina").getInt("Holograma.Slot"), InventoryItem.of(onHolo).callback(ClickType.LEFT, ev -> {
                machine.setUseHologram(!machine.getUseHologram());
                machine.createHologram();
                ev.getPlayer().closeInventory();
                Main.getMain.manager.update(machine);
            }));
        }



        // Update
        ItemStack updateButton = new ItemStack(Material.getMaterial(plugin.menuManager.getYml("Maquina").getString("Nivel.Icone")));
        ItemMeta updateMeta = updateButton.getItemMeta();
        updateMeta.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Nivel.Nome").replaceAll("&", "§"));
        updateMeta.setLore(plugin.menuManager.getYml("Maquina").getStringList("Nivel.Descrição").stream().map(str -> str.replaceAll("&", "§")
        .replaceAll("@Velocidade", String.valueOf(machine.get_velocity()))
        .replaceAll("@Drops", String.valueOf(machine.getDrop()))
        .replaceAll("@Consumo", String.valueOf(machine.get_consumption()))).collect(Collectors.toList()));
        updateButton.setItemMeta(updateMeta);
        editor.setItem(16, InventoryItem.of(updateButton).callback(ClickType.LEFT, update ->{
            if(machine.get_maxLevel() > machine.get_level()) {
                if(VaultAPI.economy.getBalance(viewer.getPlayer()) >= machine.get_updateValue()) {
                    machine.set_level(machine.get_level() + 1);
                    machine.set_updateValue((int) (machine.get_level() * Main.getMain.getConfig().getInt("Maquinas." + machine.getId() + ".Niveis.Custo")));
                    Main.getMain.manager.update(machine);
                    _updateEffect(machine.getLocation());
                    VaultAPI.economy.withdrawPlayer(viewer.getPlayer(), machine.get_updateValue());
                    if(machine.running){
                        machine.stopMachine();
                    }

                }else{
                    viewer.getPlayer().sendMessage("§cVocê não tem o valor necessário para isso.");
                    viewer.getPlayer().playSound(viewer.getPlayer().getLocation(), Sound.NOTE_BASS_DRUM, 1 , 1);
                }
            }else{
                viewer.getPlayer().sendMessage("§cA maquina já está no nivel máximo.");
            }
        }).updateCallback(itemStack -> {
            if(machine.get_maxLevel() > machine.get_level()) {
                ItemMeta updateMeta2 = updateButton.getItemMeta();
                updateMeta2.setDisplayName(plugin.menuManager.getYml("Maquina").getString("Nivel.Nome").replaceAll("&", "§"));
                updateMeta2.setLore(plugin.menuManager.getYml("Maquina").getStringList("Nivel.Descrição").stream().map(str -> str.replaceAll("&", "§")
                        .replaceAll("@Velocidade", String.valueOf(machine.get_velocity()))
                        .replaceAll("@Drops", String.valueOf(machine.get_drops()))
                        .replaceAll("@Valor", VaultAPI.economy.format(machine.get_updateValue()))
                        .replaceAll("@Consumo", String.valueOf(machine.get_consumption()))).collect(Collectors.toList()));
                itemStack.setItemMeta(updateMeta2);
            }else{
                ItemMeta updateMeta2 = itemStack.getItemMeta();
                updateMeta2.setDisplayName("§aSua maquina está no nivel máximo.");
                updateMeta2.setLore(null);
                itemStack.setItemMeta(updateMeta2);
            }


        }));





    }

    static void sellMachineDrops(CustomInventoryClickEvent sell, WMachine machine, Main plugin) {
        if(machine.getItens() <= 0) return;
        sell.getPlayer().sendMessage(plugin.prefix+" §aVocê vendeu seus drops por " + Main.getMain.economy.format(machine.getSellValue()));
        Main.getMain.economy.depositPlayer(sell.getPlayer(), machine.getSellValue());
        sell.getPlayer().closeInventory();
        machine.setItens(0);
        Main.getMain.manager.update(machine);
    }


    public void _updateEffect(Location location){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(1);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<3; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
