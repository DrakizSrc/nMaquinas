package me.dkz.plugins.nmaquinas.models;

import com.google.common.base.Strings;
import de.tr7zw.nbtapi.NBTItem;
import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.economy.VaultAPI;
import me.dkz.plugins.nmaquinas.utils.MachineUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Machine {

    public Boolean running = false;
    public String name;
    public String id;
    public ArrayList<String> description;
    public int time, delay;
    public ItemStack icon;
    public ItemStack block;
    public ItemStack drop;
    public int runningID = 0;
    public int dropID = 0;
    public String fuelType;
    public double _velocity;
    public double _drops;
    public double _consumption;
    public double _updatePrice;
    public int _maxLevel;

    public void set_maxLevel(int _maxLevel) {
        this._maxLevel = _maxLevel;
    }

    public int get_maxLevel() {
        return _maxLevel;
    }

    public void set_updatePrice(double _updatePrice) {
        this._updatePrice = _updatePrice;
    }

    public double get_updatePrice() {
        return _updatePrice;
    }

    public double get_velocity() {
        return _velocity;
    }

    public void set_velocity(double _velocity) {
        this._velocity = _velocity;
    }

    public double get_drops() {
        return _drops;
    }

    public void set_drops(double _drops) {
        this._drops = _drops;
    }

    public double get_consumption() {
        return _consumption;
    }

    public void set_consumption(double _consumption) {
        this._consumption = _consumption;
    }

    public Machine(String name){
        if(name.length() <= 32) {
            this.name = name.replaceAll("&", "§");
        }else{
            throw new IllegalArgumentException("O Nome da maquina esta longo demais, ("+name.length()+") > 32");
        }
    }

    public void setFuelType(String fuelType) {
        if(fuelType.equals("m") || fuelType.equals("l")) {
            this.fuelType = fuelType;
        }else{
            this.fuelType = "m";
        }
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public Boolean getRunning() {
        return running;
    }


    public String getName() {
        return name.replaceAll("&", "§");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public int getTime() {
        return time;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public ItemStack getBlock() {
        return block;
    }



    public ItemStack getDrop(){
        return this.drop;
    }



    public void setId(String id) {
        this.id = id;
    }

    public void toggleRunning(){

        this.running = !this.running;

    }

    public void setDescription(ArrayList<String> description) {
        this.description = (ArrayList<String>) description.stream().map(str -> str.replaceAll("&", "§")).collect(Collectors.toList());
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setIcon(ItemStack icon) {
            this.icon = icon;
    }

    public double price;
    public int maxStack;

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ItemStack machineToShopIcon(){
        ItemStack itemStack = new ItemStack(getBlock());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(getName().replaceAll("&", "§"));
        meta.setLore(Main.getMain.getConfig().getStringList("Maquinas."+getId()+".Descrição").stream().map(str ->
                str.replaceAll("&", "§")
                .replaceAll("@Preço", String.valueOf(VaultAPI.economy.format(getPrice())))
                .replaceAll("@Drop", VaultAPI.economy.format(getDropValue()))
                .replaceAll("@Nivel", String.valueOf(get_maxLevel()))
        ).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public ItemStack toItemStack(Player player, int amount){
        ItemStack itemStack = new ItemStack(getBlock());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(getName());

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Informações: ");
        int percentage = 0 * 100 / getTime();
        double b = Math.round(percentage * 10.0) / 10.0;
        if(getFuelType().equals("l")) {
            lore.add("§e➥ Litros: "+getProgressBar(0, getTime(), 15, '|', ChatColor.GREEN, ChatColor.RED)+"§8 - "+b+"%");
        }else{
            lore.add("§e➥ Tempo: "+getProgressBar(0, getTime(), 15, '|', ChatColor.GREEN, ChatColor.RED)+"§8 - "+b+"%");
        }
        lore.add("§e➥ Nível: §b1");
        lore.add("§e➥ Quantidade: §7"+ MachineUtils.format(amount));
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("Level", 1);
        nbtItem.setInteger("Amount", amount);
        return nbtItem.getItem();
    }
    public String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

    public void setBlock(ItemStack block) {
        if(block.getType().isSolid() || block.getType().equals(Material.SKULL_ITEM) || block.getType().equals(Material.SKULL)){
            this.block = block;
            return;
        }
        throw new IllegalArgumentException("O item escolhido deve ser um bloco.");
    }

    public void setDrops(ItemStack drop) {
      this.drop = drop;
    }

    public int dropValue = 0;

    public void setDropsValue(int value) {
        this.dropValue =value;
    }

    public int getDropValue() {
        return dropValue;
    }
}
