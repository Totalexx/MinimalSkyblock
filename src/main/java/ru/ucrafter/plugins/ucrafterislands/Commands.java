package ru.ucrafter.plugins.ucrafterislands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandPosition;

public class Commands implements CommandExecutor {
    public Commands() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            String nicknameLeader;
            if (args.length == 0) {
                nicknameLeader = sender.getName();
                IslandPosition position = UCrafterIslands.getDatabase().getIslandPositionByLeader(nicknameLeader);
                IslandPosition ip = IslandEvents.createIsland(nicknameLeader);
                sender.sendMessage(UCrafterIslands.getFromConfiguration("messages.is_create_island") + " X:" + ip.z + " Y:" + ip.x + " nd:" + ip.nextDirection.toString());
                return true;
            } else {
                nicknameLeader = args[0];
                byte var6 = -1;
                switch(nicknameLeader.hashCode()) {
                    case -1183699191:
                        if (nicknameLeader.equals("invite")) {
                            var6 = 0;
                        }
                    default:
                        switch(var6) {
                            case 0:
                                if (args.length == 2) {
                                    IslandEvents.joinPlayerToIsland(sender, args[1]);
                                    return true;
                                }

                                return false;
                            default:
                                return false;
                        }
                }
            }
        } else {
            sender.sendMessage(UCrafterIslands.getFromConfiguration("message.sender_not_a_player"));
            return true;
        }
    }
}