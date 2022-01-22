package ru.totalexx.plugins.minimalskyblock.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.totalexx.plugins.minimalskyblock.utils.Config;
import ru.totalexx.plugins.minimalskyblock.IslandEvents;

public class LeaveIslandMenu extends InventoryGUI{

    public LeaveIslandMenu() {
        inventory = Bukkit.createInventory(this, 9, "Вы уверены?");
        inventory.setItem(2,
                createItem(Material.TNT,0,
                        Config.getString("inv.leave_island.name"),
                        Config.getList("inv.leave_island.lore")));
        inventory.setItem(6,
                createItem(Material.WOOL,0,
                        Config.getString("inv.leave_island.cancel.name"),
                        Config.getList("inv.leave_island.cancel.lore")));
    }

    @Override
    void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        switch (e.getSlot()) {
            case 2:
                IslandEvents.leaveIsland(player);
            case 6:
                player.closeInventory();
        }
    }
}
