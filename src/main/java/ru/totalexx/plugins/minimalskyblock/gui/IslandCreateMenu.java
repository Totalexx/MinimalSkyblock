package ru.totalexx.plugins.minimalskyblock.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import ru.totalexx.plugins.minimalskyblock.IslandEvents;
import ru.totalexx.plugins.minimalskyblock.utils.Config;

public class IslandCreateMenu extends InventoryGUI {

    public IslandCreateMenu() {
        inventory = Bukkit.createInventory(this, InventoryType.DISPENSER, "Создать остров");
        inventory.setItem(4,
                createItem(Material.BOAT, 0,
                        Config.getString("inv.create_island.name"),
                        Config.getList("inv.create_island.lore")));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getSlot() == 4) {
            IslandEvents.createIsland(player);
            player.closeInventory();
            player.openInventory(new IslandMenu(player.getUniqueId()).getInventory());
        }
    }
}
