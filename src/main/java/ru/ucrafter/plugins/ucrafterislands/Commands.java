package ru.ucrafter.plugins.ucrafterislands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandConfig;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandPosition;

public class Commands implements CommandExecutor {
    public Commands() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            String nicknameLeader;
            if (args.length == 0) {
                nicknameLeader = sender.getName();
//                IslandPosition position = UCrafterIslands.getDatabase().getIslandPositionByLeader(nicknameLeader);
//                if (position == null) {
                    IslandEvents.createIsland(nicknameLeader);
                    sender.sendMessage(IslandConfig.getString("messages.is_create_island"));
//                } else{
//                    Player player = (Player) sender;
//                    player.teleport(new Location(
//                            IslandConfig.getIslandsWorld(),
//                            position.x + IslandConfig.getInt("islands.teleport_deviation.x") + 0.5d,
//                            IslandConfig.getIslandHeight() + IslandConfig.getInt("islands.teleport_deviation.y"),
//                            position.z + +IslandConfig.getInt("islands.teleport_deviation.z") + 0.5d));
//                    sender.sendMessage(IslandConfig.getString("messages.is_home"));
//                }
                return true;
            } else {
                return false;
            }
        } else {
            sender.sendMessage(IslandConfig.getString("messages.sender_not_a_player"));
            return true;
        }
    }
}