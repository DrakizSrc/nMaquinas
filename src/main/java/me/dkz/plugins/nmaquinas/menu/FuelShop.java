package me.dkz.plugins.nmaquinas.menu;

import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import me.dkz.plugins.nmaquinas.Main;
import me.dkz.plugins.nmaquinas.listener.ChatShopEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class FuelShop extends PagedInventory {

    public Main plugin = Main.getMain;

    public FuelShop() {
        super("uplugins.machine.fuel", "Loja de Combustiveis", 9*4);
    }



    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        List<InventoryItemSupplier> itemSuppliers = new LinkedList<>();
        plugin.manager._loadedFuel.forEach(fuel ->{
            if(itemSuppliers.contains(fuel)) return;
            itemSuppliers.add(() -> {
                ItemStack itemStack = new ItemStack(fuel.toShopGUI());
                InventoryItem inventoryItem = InventoryItem.of(itemStack);
                inventoryItem.callback(ClickType.LEFT, call ->{
                    if(fuel.toShopGUI().equals(call.getItemStack())){
                        ChatShopEvent.buyMachine.remove(call.getPlayer());
                        call.getPlayer().closeInventory();
                        ChatShopEvent.buyFuel.put(call.getPlayer(), fuel);
                        call.getPlayer().sendMessage("");
                        call.getPlayer().sendMessage("§7➥ §eVocê está comprando [ "+fuel.getName().replaceAll("&", "§")+"§e ] ");
                        call.getPlayer().sendMessage("§7➥ §eDigite no chat a quantidade que pretende comprar.");
                        call.getPlayer().sendMessage("§7➥ §ePara sair digite §c§lCANCELAR");
                        call.getPlayer().sendMessage("");

                    }
                });
                return inventoryItem;
            });
        });

        return itemSuppliers;

    }


}
