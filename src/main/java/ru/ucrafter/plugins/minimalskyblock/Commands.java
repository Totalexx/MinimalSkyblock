package ru.ucrafter.plugins.minimalskyblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandDB;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;

import java.util.ArrayList;


public class Commands implements CommandExecutor, TabCompleter {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.getString("messages.sender_not_a_player"));
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            IslandPosition position = IslandDB.getIslandPositionByLeader(player.getName());
            if (position == null) {
                IslandEvents.createIsland(player);
            } else {
                IslandEvents.teleportToMyIsland(player, position);
            }
            return true;
        } else {
            if (args.length == 1 && args[0].equals("allowvisits")) {
                player.sendMessage(Boolean.toString(IslandDB.canAnyoneVisitIsland(player.getName())));
                return true;
            }
            if (args.length == 2) {
                switch (args[0]) {
                    case "join":
                        IslandEvents.joinPlayerToIsland(player, args[1]);
                        break;
                    case "kick":
                        IslandEvents.kickPlayerToIsland(player, args[1]);
                        break;
                    case "tp":
                        IslandEvents.teleportToIsland(player, args[1]);
                        break;
                    case "pvp":
                        break;
                    case "allowvisits":
                        boolean canAnyoneVisit = Boolean.parseBoolean(args[1]);
                        IslandEvents.setCanAnyoneVisit(player, canAnyoneVisit);
                        break;
                }
            } else {
                player.performCommand("help MinimalSkyblock");
            }
        }
        return true;
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("join");
        commands.add("kick");
        commands.add("tp");
        commands.add("pvp");
        return commands;
    }
}