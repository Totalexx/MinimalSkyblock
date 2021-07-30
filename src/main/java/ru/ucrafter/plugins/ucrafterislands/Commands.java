package ru.ucrafter.plugins.ucrafterislands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            if (args.length == 0) {
                return false;
            } else {
                switch (args[0]) {
                    case "invite":
                        if (args.length == 2) {
                            sender.sendMessage(UCrafterIslands.getInstance().getConfig().getString("messages.invite"));
                            return true;
                        }
                        return false;
                    default:
                    case "help":
                        sender.sendMessage("Хелпа");
                        return true;

                }
            }
        } else {
            sender.sendMessage("Сорри, консолька((");
            return true;
        }
    }
}
