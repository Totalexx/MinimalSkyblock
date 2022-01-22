package ru.totalexx.plugins.minimalskyblock.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.totalexx.plugins.minimalskyblock.IslandEvents;
import ru.totalexx.plugins.minimalskyblock.utils.Config;
import ru.totalexx.plugins.minimalskyblock.utils.IslandDB;
import ru.totalexx.plugins.minimalskyblock.utils.WorldEditGuard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandMenu extends InventoryGUI {

    public IslandMenu(UUID playerID) {
        inventory = Bukkit.createInventory(this, 9, "Ваш остров");
        updateInventory(playerID);
    }

    private void updateInventory(UUID playerID) {
        inventory.setItem(0,
                createItem(Material.BOAT,0,
                        Config.getString("inv.go_island.name"),
                        Config.getList("inv.go_island.lore")));

        inventory.setItem(2,
                createItem(Material.BED, 0,
                        Config.getString("inv.set_spawn.name"),
                        Config.getList("inv.set_spawn.lore")));

        IslandDB islandDB = new IslandDB();
        int islandID = islandDB.getIslandID(playerID);
        int visitStatus = islandDB.getVisitStatus(islandID).ordinal();
        islandDB.closeConnection();

        List<String> descriptionVisits = new ArrayList<>();
        descriptionVisits.add(
                (visitStatus == 0 ? "§6§l" : "§7") + Config.getString("inv.visits.anybody"));
        descriptionVisits.add(
                (visitStatus == 1 ? "§6§l" : "§7") + Config.getString("inv.visits.by_invitation"));
        descriptionVisits.add(
                (visitStatus == 2 ? "§6§l" : "§7") + Config.getString("inv.visits.nobody"));

        inventory.setItem(3,
                createItem(Material.WOOD_DOOR, 0,
                        Config.getString("inv.visits.name"),
                        descriptionVisits));

        List<String> descriptionMembers = new ArrayList<>(5);
        descriptionMembers.add(Config.getString("inv.members.max"));
        List<String> members = new ArrayList<>(); /////////

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

        String pvpStatus = "inv.pvp." + (WorldEditGuard.getPVP(islandID) ? "enable" : "disable");
        inventory.setItem(5,
                createItem(Material.DIAMOND_SWORD,0,
                        Config.getString(pvpStatus),
                        Config.getList("inv.pvp.lore")));

        inventory.setItem(8,
                createItem(Material.TNT, 0,
                        Config.getString("inv.leave_island.name"),
                        Config.getList("inv.leave_island.lore")));

    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        IslandDB islandDB = new IslandDB();
        int islandID = islandDB.getIslandID(player.getUniqueId());
        IslandDB.VisitStatus visitStatus = islandDB.getVisitStatus(islandID);
        islandDB.closeConnection();

        if (islandID == 0) {
            player.closeInventory();
            return;
        }

        switch (e.getSlot()) {
            case 0:
                IslandEvents.teleportToMyIsland(player);
                break;
            case 2:
                IslandEvents.setIslandSpawn(islandID, player.getLocation());
                break;
            case 3:
                islandDB.openConnection();
                islandDB.setVisitStatus(islandID, visitStatus.nextStatus());
                islandDB.closeConnection();
                break;
            case 4:
//                player.sendMessage(Config.getMessage("is_howJoinAndKick"));
                break;
            case 5:
                WorldEditGuard.changePVP(islandID);
                break;
            case 8:
                player.openInventory(new LeaveIslandMenu().getInventory());
                return;
        }
        updateInventory(player.getUniqueId());
    }
}
