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

public class MachineShop extends PagedInventory {

    public Main plugin = Main.getMain;

    public MachineShop() {
        super("uplugins.machine.shop", "Loja de Maquinas", 9*4);
    }



    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        List<InventoryItemSupplier> itemSuppliers = new LinkedList<>();
        plugin.manager._loadedMachines.forEach(machine ->{
            itemSuppliers.add(() -> {
                ItemStack itemStack = new ItemStack(machine.machineToShopIcon());
                InventoryItem inventoryItem = InventoryItem.of(itemStack);
                inventoryItem.callback(ClickType.LEFT, call ->{
                    if(machine.machineToShopIcon().equals(call.getItemStack())){
                        ChatShopEvent.buyFuel.remove(call.getPlayer());

                        call.getPlayer().closeInventory();
                        ChatShopEvent.buyMachine.put(call.getPlayer(), machine);
                        call.getPlayer().sendMessage("");
                        call.getPlayer().sendMessage("§7➥ §eVocê está comprando [ "+machine.getName().replaceAll("&", "§")+"§e ] ");
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
