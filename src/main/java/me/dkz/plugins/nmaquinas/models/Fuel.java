package me.dkz.plugins.nmaquinas.models;

import me.dkz.plugins.nmaquinas.economy.VaultAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Fuel {

    public ItemStack icon;
    public String type;
    public ArrayList<String> description;
    public int amount;
    public String  name;
    public String id;
    public int value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ItemStack toShopGUI(){
        ItemStack itemStack = new ItemStack(getIcon());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(getDescription().stream().map(str -> str.replaceAll("@Preço", VaultAPI.economy.format(getValue()))
        .replaceAll("@Quantidade", getAmount()+getType().replaceAll("m", " Minuto(s)").replaceAll("l", " Litro(s)"))).collect(Collectors.toList()));
        meta.setDisplayName(getName());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack toItemStack(int amount){
        ItemStack itemStack = new ItemStack(getIcon());
        itemStack.setAmount(amount);
        ItemMeta meta = itemStack.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Combustivel para maquinas.");
        lore.add("");
        lore.add("§e➥ Quantidade: §b"+getAmount()+getType());
        lore.add("§8Combustivel mantem suas maquinas vivas.");
        meta.setDisplayName(getName());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Fuel(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
