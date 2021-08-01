package ru.ucrafter.plugins.ucrafterislands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import org.bukkit.command.CommandSender;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandConfig;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandDB;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandPosition;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandPosition.NextDirection;
import ru.ucrafter.plugins.ucrafterislands.utils.WESchematic;

import java.io.File;

public class IslandEvents {

    private static IslandDB database = UCrafterIslands.getDatabase();

    public static IslandPosition createIsland(String nicknameLeader) {
        IslandPosition newIsland = database.getPositionLastIsland();
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

        database.addIsland(newIsland.x, newIsland.z, newIsland.nextDirection.toString(), nicknameLeader);

        File schematic = new File(UCrafterIslands.getFolder()
                + File.separator
                + IslandConfig.getString("islands.name_schematic_file")
                + ".schematic");
        World islandsWorld = new BukkitWorld(IslandConfig.getIslandsWorld());
        WESchematic.pasteClipboard(schematic, islandsWorld, newIsland);

        return newIsland;
    }

    public static void teleportToIsland(String nicknameLeader) {
    }

    public static void joinPlayerToIsland(CommandSender sender, String joinPlayer) {
        sender.sendMessage(String.format(IslandConfig.getString("messages.invite_successfully"), joinPlayer));
    }
}