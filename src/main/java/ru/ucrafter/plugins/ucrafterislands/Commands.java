package ru.ucrafter.plugins.ucrafterislands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandPosition;
import ru.ucrafter.plugins.ucrafterislands.utils.Config;

public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            String nicknameLeader;
            if (args.length == 0) {
                nicknameLeader = sender.getName();
                IslandPosition position = UCrafterIslands.getDatabase().getIslandPositionByLeader(nicknameLeader);
                if (position == null) {
                    IslandEvents.createIsland(nicknameLeader);
                    sender.sendMessage(Config.getString("messages.is_create_island"));
                } else{
                    Player player = (Player) sender;
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