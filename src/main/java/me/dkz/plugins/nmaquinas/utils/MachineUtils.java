package me.dkz.plugins.nmaquinas.utils;

import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.models.WMachine;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class MachineUtils {


    public static int getMachinesInWorld(Player player){
        int value = 0;
        for(WMachine machine : Main.getMain.manager._inWMachines){
            if(machine.getOwer().equals(player.getDisplayName())){
                value += machine.getAmount();
            }
        }
        return value;
    }
    public static int getMachinesInWorldValue(Player player){
        int value = 0;
        for(WMachine machine : Main.getMain.manager._inWMachines){
            if(machine.getOwer().equals(player.getDisplayName())){
                value += (machine.getPrice() * machine.getAmount());
            }
        }
        return value;
    }

    public static ItemStack createCustomSkull(int amount, String displayName, String texture) {
        texture = "http://textures.minecraft.net/texture/" + texture;

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
        if (texture.isEmpty()) {
            return skull;
        }

        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        skullMeta.setDisplayName(displayName);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        }
        catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        assert profileField != null;
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
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
