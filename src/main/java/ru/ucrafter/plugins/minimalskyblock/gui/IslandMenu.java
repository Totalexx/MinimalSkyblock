package ru.ucrafter.plugins.minimalskyblock.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.ucrafter.plugins.minimalskyblock.IslandEvents;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandDB;
import ru.ucrafter.plugins.minimalskyblock.utils.WorldEditGuard;

import java.util.ArrayList;
import java.util.List;

public class IslandMenu extends InventoryGUI {

    public IslandMenu(String playerName) {
        inventory = Bukkit.createInventory(this, 9, "Создать остров");
        updateInventory(playerName);
    }

    private void updateInventory(String playerName) {
        inventory.setItem(0,
                createItem(Material.BOAT,0,
                        Config.getString("inv.go_island.name"),
                        Config.getList("inv.go_island.lore")));

        inventory.setItem(2,
                createItem(Material.BED, 0,
                        Config.getString("inv.set_spawn.name"),
                        Config.getList("inv.set_spawn.lore")));

        int visitStatus = IslandDB.getVisitStatus(playerName).ordinal();
        List<String> descriptionVisits = new ArrayList<>();
        descriptionVisits.add(
                (visitStatus == 0 ? "§6§l" : "§7")
                        + Config.getString("inv.visits.anybody"));
        descriptionVisits.add(
                (visitStatus == 1 ? "§6§l" : "§7")
                        + Config.getString("inv.visits.by_invitation"));
        descriptionVisits.add(
                (visitStatus == 2 ? "§6§l" : "§7")
                        + Config.getString("inv.visits.nobody"));

        inventory.setItem(3,
                createItem(Material.WOOD_DOOR, 0,
                        Config.getString("inv.visits.name"),
                        descriptionVisits));

        List<String> descriptionMembers = new ArrayList<>(5);
        descriptionMembers.add(Config.getString("inv.members.max"));
        List<String> members = IslandDB.getMembers(playerName);

        if (members.isEmpty()) {
            descriptionMembers.add(Config.getString("inv.members.no_members"));
        } else {
            descriptionMembers.add(Config.getString("inv.members.have_members"));
            descriptionMembers.addAll(members);
        }
        inventory.setItem(4,
                createItem(Material.SKULL_ITEM, 3,
                        Config.getString("inv.members.name"),
                        descriptionMembers));

        String pvpStatus = "inv.pvp." + (WorldEditGuard.getPVP(playerName) ? "enable" : "disable");
        inventory.setItem(5,
                createItem(Material.DIAMOND_SWORD,0,
                        Config.getString(pvpStatus),
                        Config.getList("inv.pvp.lore")));

        inventory.setItem(8,
                createItem(Material.TNT, 0,
                        Config.getString("inv.reset_island.name"),
                        Config.getList("inv.reset_island.lore")));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        switch (e.getSlot()) {
            case 0:
                IslandEvents.teleportToMyIsland(player);
                break;
            case 2:
//                IslandEvents.setSpawnPoint();
                break;
            case 3:
                break;
            case 4:
//                player.sendMessage(Config.getMessage("is_howJoinAndKick"));
                break;
            case 5:
                WorldEditGuard.changePVP(player.getName());
                break;
            case 8:
                player.openInventory(new DeleteIslandMenu().getInventory());
                return;
        }
        updateInventory(player.getName());
    }
}
