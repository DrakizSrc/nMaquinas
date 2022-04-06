package me.dkz.plugins.nmaquinas.models;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.base.Strings;
import de.tr7zw.nbtapi.NBTItem;
import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.utils.MachineUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;


public class WMachine extends Machine {

    //* TODO:
    //* - Refactor this class to be more generic
    //* - Understand the calculation of consumption



    public String ower;
    public Location location;
    public int fuel;
    public Hologram hologram = null;
    public Hologram iconHologram;
    public String status = "§cDesativado";
    public int itens = 0;
    public int amount = 0;
    public Boolean useHologram = false;
    public int sellValue = 0;





    public int _level = 1;
    public int _updateValue;

    public void set_updateValue(int _updateValue) {
        this._updateValue = _updateValue;
    }

    public int get_updateValue() {
        return _updateValue;
    }

    public int get_level() {
        return _level;
    }

    public void set_level(int _level) {
        this._level = _level;
    }

    public int getSellValue() {
        return sellValue;
    }

    public void setSellValue(int sellValue) {
        this.sellValue = sellValue;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Boolean getUseHologram() {
        return useHologram;
    }

    public void setUseHologram(Boolean useHologram) {
        this.useHologram = useHologram;
    }

    public void setItens(int amount) {
        itens = amount;
    }

    @Override
    public void setDrops(ItemStack drop) {
        super.setDrops(drop);
    }

    public int getItens(){
        return itens;
    }


    public WMachine(String name) {

        super(name);
    }


    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getFuel() {
        return fuel;
    }

    public String getOwer() {
        return ower;
    }

    public void setOwer(String ower) {
        this.ower = ower;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }



    public void createHologram(){

            if(Main.getMain._useHologram) {
            if (useHologram) {
                if (hologram == null) {
                    HologramLocation();
                } else {
                    hologram.delete();
                    HologramLocation();
                }

            } else {
                if (hologram != null) {
                    hologram.delete();
                    iconHologram.delete();
                    iconHologram = null;
                }
            }
        }

    }


    public ItemStack dropInvotoryMachine(){
        ItemStack itemStack = getBlock();
        ItemMeta meta =itemStack.getItemMeta();
        meta.setDisplayName(getName().replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>(Arrays.asList(" §eQuantidade: §7" + MachineUtils.format(getItens()), " §eValor de Venda: §a$" + Main.getMain.economy.format(getSellValue()), "§8Clique ESQUERDO para vender"));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void HologramLocation() {
        Location newLocation = new Location(getLocation().getWorld(), getLocation().getX(), getLocation().getY() + 1.6, getLocation().getZ()).add(+0.5, +1, +0.5);
        hologram = HologramsAPI.createHologram(Main.getMain, newLocation);
        if(iconHologram == null){
            iconHologram = HologramsAPI.createHologram(Main.getMain, newLocation.add(0, 0.8, 0));
            ItemLine icon = iconHologram.appendItemLine(getIcon());
        }
        TextLine name = hologram.appendTextLine(getName());
        TextLine ower = hologram.insertTextLine(1, "§eDono: §b"+getOwer());
        if(getFuelType().equals("l")){
            TextLine time = hologram.insertTextLine(2, "§eLitros Restante: " + getProgressBar(fuel, getTime(), 15, '|', ChatColor.GREEN, ChatColor.RED));
        }else {
            TextLine time = hologram.insertTextLine(2, "§eTempo Restante: " + getProgressBar(fuel, getTime(), 15, '|', ChatColor.GREEN, ChatColor.RED));
        }
        if(Main.getMain.getConfig().getBoolean("Maquinas."+getId()+".Stack")) {
            TextLine amount = hologram.insertTextLine(3, "§eQuantidade: §b" + MachineUtils.format(getAmount()));
            TextLine statss = hologram.insertTextLine(4, "§eStatus: "+status);
            return;
        }

        TextLine statss = hologram.insertTextLine(3, "§eStatus: "+status);

    }

    public void deleteHologram(){
        if(hologram != null)
        if(Main.getMain._useHologram) {
            try {
                hologram.delete();
                iconHologram.delete();
                iconHologram = null;
            } catch (Exception ex) {
            }
        }

    }


    public void runMachine(){
        long fuelTime = (long) (((get_consumption()*get_level())*10)*20L);
        int seconds = (int) (fuelTime/20);
        running = true;

        runningID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getMain, () ->{
            if(running) {
                status = "§aAtivado";
                createHologram();
                if(fuel > 0){
                    fuel--;
                    Main.getMain.manager.update(this);
                    createHologram();
                }else{
                    stopMachine();
                }
            }else{
                stopMachine();
            }
        }, 0L, fuelTime);

        dropID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getMain, () -> {
            if(fuel > 0) {
                Main.getMain.manager.update(this);
                if(Main.getMain.getConfig().getBoolean("Informações.Maquinas.Armazem")) {
                    setItens((getItens() + getAmount()) + ((int) get_drops() * get_level()));
                    setSellValue(getItens() * getDropValue());
                }else{
                    getLocation().getWorld().dropItemNaturally(location, getDrop());
                }
            }
        }, 20, delayBonus()*20L);
    }
    public void stopMachine(){
        if(running){
            status = "§cDesativado";
            running = false;
            Bukkit.getScheduler().cancelTask(dropID);
            Bukkit.getScheduler().cancelTask(runningID);
            if(hologram != null)
                createHologram();
        }
    }


    public int delayBonus(){
        if(getDelay() - ((get_level() * get_velocity())) > 0){
            return (int) (getDelay() - ((get_level() * get_velocity())));
        }
        return 0;
    }





}
