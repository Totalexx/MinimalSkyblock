package ru.ucrafter.plugins.minimalskyblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;

public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                Player player = (Player) sender;
                IslandPosition position = MinimalSkyblock.getDatabase().getIslandPositionByLeader(player.getName());
                if (position == null) {
                    IslandEvents.createIsland((Player) sender);
                } else{
                    IslandEvents.teleportToIsland(player, position);
                }
                return true;
            } else {
                return false;
            }
        } else {
            sender.sendMessage(Config.getString("messages.sender_not_a_player"));
            return true;
        }
    }
}