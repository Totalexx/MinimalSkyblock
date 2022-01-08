package ru.ucrafter.plugins.minimalskyblock.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null || inventory.getHolder() == null) {
            return;
        }

        if (inventory.getHolder() instanceof InventoryGUI) {
            event.setCancelled(true);
            ((InventoryGUI) inventory.getHolder()).onInventoryClick(event);
        }
    }
}
