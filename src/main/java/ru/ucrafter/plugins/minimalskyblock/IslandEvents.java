package ru.ucrafter.plugins.minimalskyblock;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandDB;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition.NextDirection;
import ru.ucrafter.plugins.minimalskyblock.utils.WorldEditGuardAPI;

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
        WorldEditGuardAPI.createIsland(nicknameLeader, newIsland);
        DATABASE.addIsland(newIsland.x, newIsland.z, newIsland.nextDirection.toString(), nicknameLeader);
        player.sendMessage(Config.getMessage("messages.is_create_island"));
    }

    public static void teleportToIsland(Player player, IslandPosition position) {
        int islandSizeX = Config.getInt("islands.size_x");
        int islandSizeZ = Config.getInt("islands.size_z");
        int islandBetween = Config.getInt("islands.distance_between");
        player.teleport(new Location(
                Config.getIslandsWorld(),
                (islandSizeX + islandBetween) * position.x + Config.getInt("islands.teleport_deviation.x") + 0.5d,
                Config.getIslandHeight() + Config.getInt("islands.teleport_deviation.y"),
                (islandSizeZ + islandBetween) * position.z + Config.getInt("islands.teleport_deviation.z") + 0.5d));
        player.sendMessage(Config.getMessage("messages.is_home"));
    }

    public static void joinPlayerToIsland(CommandSender sender, String joinPlayer) {
        sender.sendMessage(String.format(Config.getString("messages.invite_successfully"), joinPlayer));
    }
}