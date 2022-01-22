package ru.totalexx.plugins.minimalskyblock.gui;

import org.bukkit.entity.Player;
import ru.totalexx.plugins.minimalskyblock.utils.IslandDB;

public class GUIEvents {
    public static void openGUI(Player player) {
        IslandDB islandDB = new IslandDB();
        InventoryGUI menu = islandDB.hasIsland(player.getUniqueId()) ?
                new IslandMenu(player.getUniqueId()) : new IslandCreateMenu();
        islandDB.closeConnection();
        player.openInventory(menu.getInventory());
    }
}
