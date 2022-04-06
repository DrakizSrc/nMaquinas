package me.dkz.plugins.nmaquinas.menu;

import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import me.dkz.plugins.nmaquinas.Main;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class MachineDrops extends PagedInventory {

    public Main plugin = Main.getMain;

    public MachineDrops() {
        super("uplugins.machine.drops", "Suas Maquinas", 9*4);
        configuration(configuration -> {
            configuration.secondUpdate(1);
        });
    }



    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        List<InventoryItemSupplier> itemSuppliers = new LinkedList<>();
        plugin.manager._inWMachines.forEach(machine ->{
            if(machine.getOwer().equals(viewer.getPlayer().getDisplayName())) {
                itemSuppliers.add(() -> {
                    InventoryItem inventoryItem = InventoryItem.of(new ItemStack(machine.dropInvotoryMachine()));
                    inventoryItem.callback(ClickType.LEFT, call -> {
                        if (machine.dropInvotoryMachine().equals(call.getItemStack())) {

                            MainMachine.sellMachineDrops(call, machine, plugin);

                        }
                    });
                    return inventoryItem;
                });
            }
        });

        return itemSuppliers;

    }
}
