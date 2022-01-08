package ru.ucrafter.plugins.minimalskyblock.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.ucrafter.plugins.minimalskyblock.IslandEvents;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;

public class DeleteIslandMenu extends InventoryGUI{

    public DeleteIslandMenu() {
        inventory = Bukkit.createInventory(this, 9, "Вы уверены?");
        inventory.setItem(2,
                createItem(Material.TNT,0,
                        Config.getString("inv.delete_island.confirm.name"),
                        Config.getList("inv.delete_island.confirm.lore")));
        inventory.setItem(6,
                createItem(Material.WOOL,0,
                        Config.getString("inv.delete_island.cancel.name"),
                        Config.getList("inv.delete_island.cancel.lore")));
    }

    @Override
    void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        switch (e.getSlot()) {
            case 2:
                IslandEvents.deleteIsland(player);
            case 6:
                player.closeInventory();
        }
    }
}
