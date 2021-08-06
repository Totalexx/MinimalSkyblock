package ru.ucrafter.plugins.minimalskyblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandDB;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition.NextDirection;
import ru.ucrafter.plugins.minimalskyblock.utils.WorldEditGuardAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IslandEvents {

    private static final IslandDB DATABASE = MinimalSkyblock.getDatabase();

    public static void createIsland(Player player) {
        String nicknameLeader = player.getName();
        IslandPosition newIsland = DATABASE.getPositionLastIsland();

        if (newIsland != null) {
            switch(newIsland.nextDirection) {
                case RIGHT:
                    newIsland.z++;
                    break;
                case DOWN:
                    newIsland.x--;
                    break;
                case LEFT:
                    newIsland.z--;
                    break;
                case TOP:
                    newIsland.x++;
                    break;
            }

            if (newIsland.z == newIsland.x || newIsland.z == Math.abs(newIsland.x) || newIsland.z + newIsland.x == 1 && newIsland.z < newIsland.x) {
                newIsland.nextDirection = newIsland.nextDirection.rotateDirection();
            }
        } else {
            newIsland = new IslandPosition(0, 0, NextDirection.TOP);
        }
        boolean islandCreated = WorldEditGuardAPI.createIsland(nicknameLeader, newIsland);
        if (islandCreated) {
            DATABASE.addIsland(newIsland.x, newIsland.z, newIsland.nextDirection.toString(), nicknameLeader);
            player.sendMessage(Config.getMessage("is_create_successfully"));
        } else {
            player.sendMessage(Config.getMessage("is_create_failed"));
        }
    }

    public static void setCanAnyoneVisit(Player player, boolean canAnyoneVisit) {
        if (DATABASE.hasIsland(player.getName())) {
            DATABASE.setAnyoneVisitIsland(player.getName(), canAnyoneVisit);
            String message = canAnyoneVisit ? Config.getMessage("is_allowvisits_true") :
                    Config.getMessage("is_allowvisits_false");
            player.sendMessage(message);
        } else {
            playerHasNotIsland(player);
        }
    }

    public static void teleportToMyIsland(Player player, IslandPosition position) {
        teleportToIsland(player, position);
        player.sendMessage(Config.getMessage("is_home"));
    }

    public static void teleportToIsland(Player player, String nicknameLeader) {
        IslandPosition position = DATABASE.getIslandPositionByLeader(nicknameLeader);
        if (position == null) {
            player.sendMessage(Config.getMessage("is_tp_failed_island_not_found", nicknameLeader));
            return;
        }
        if (player.getName().equalsIgnoreCase(nicknameLeader)) {
            teleportToMyIsland(player, position);
            return;
        }
        if (Config.getBoolean("settings.can_not_member_tp_island") ||
                DATABASE.canAnyoneVisitIsland(nicknameLeader) ||
                DATABASE.isMemberIsland(nicknameLeader, player.getName())) {
            teleportToIsland(player, position);
            player.sendMessage(Config.getMessage("is_tp_successfully", nicknameLeader));
        } else {
            player.sendMessage(Config.getMessage("is_tp_failed_player_not_a_member", nicknameLeader));
        }
    }

    public static void joinPlayerToIsland(Player player, String joinPlayer) {
        String nicknameLeader = player.getName();
        if (DATABASE.hasIsland(nicknameLeader)) {
            if (player.getName().equalsIgnoreCase(joinPlayer)) {
                player.sendMessage(Config.getMessage("is_join_yourself", joinPlayer));
                return;
            }
            if (Bukkit.getPlayer(joinPlayer) == null) {
                player.sendMessage(Config.getMessage("is_player_not_found", joinPlayer));
                return;
            }
            List<String> members = DATABASE.getMembers(nicknameLeader);
            if (members.contains(joinPlayer)){
                player.sendMessage(Config.getMessage("is_join_already_added", joinPlayer));
                return;
            }
            members.add(joinPlayer);
            DATABASE.setMembers(nicknameLeader, members);
            player.sendMessage(Config.getMessage("is_join_successfully", joinPlayer));
        } else {
            playerHasNotIsland(player);
        }
    }

    public static void kickPlayerToIsland(Player player, String kickPlayer) {
        String nicknameLeader = player.getName();
        if (DATABASE.hasIsland(nicknameLeader)) {
            if (player.getName().equalsIgnoreCase(kickPlayer)) {
                player.sendMessage(Config.getMessage("is_kick_yourself"));
                return;
            }
            List<String> members = DATABASE.getMembers(nicknameLeader);
            if (members.contains(kickPlayer)){
                members.remove(kickPlayer);
                DATABASE.setMembers(nicknameLeader, members);
                player.sendMessage(Config.getMessage("is_kick_successfully", kickPlayer));
            } else {
                player.sendMessage(Config.getMessage("is_kick_player_not_added", kickPlayer));
                return;
            }
        } else {
            playerHasNotIsland(player);
        }
    }

    private static void teleportToIsland(Player player, IslandPosition position) {
        int islandSizeX = Config.getInt("islands.size_x");
        int islandSizeZ = Config.getInt("islands.size_z");
        int islandBetween = Config.getInt("islands.distance_between");
        player.teleport(new Location(
                Config.getIslandsWorld(),
                (islandSizeX + islandBetween) * position.x + Config.getInt("islands.teleport_deviation.x") + 0.5d,
                Config.getIslandHeight() + Config.getInt("islands.teleport_deviation.y"),
                (islandSizeZ + islandBetween) * position.z + Config.getInt("islands.teleport_deviation.z") + 0.5d));
    }

    private static void playerHasNotIsland(Player player) {
        player.sendMessage(Config.getMessage("is_not_found"));
    }
}