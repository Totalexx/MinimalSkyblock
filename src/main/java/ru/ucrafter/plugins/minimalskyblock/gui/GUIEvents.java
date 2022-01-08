package ru.ucrafter.plugins.minimalskyblock.gui;

import org.bukkit.entity.Player;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandDB;

public class GUIEvents {
    public static void openGUI(Player player) {
        InventoryGUI menu = IslandDB.hasIsland(player.getName()) ?
                new IslandMenu(player.getName()) : new IslandCreateMenu();
        player.openInventory(menu.getInventory());
    }
}
