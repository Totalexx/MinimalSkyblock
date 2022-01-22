package ru.totalexx.plugins.minimalskyblock;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.totalexx.plugins.minimalskyblock.gui.GUIEvents;
import ru.totalexx.plugins.minimalskyblock.utils.Config;
import ru.totalexx.plugins.minimalskyblock.utils.IslandDB;

import java.util.ArrayList;


public class Commands implements CommandExecutor, TabCompleter {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args[0].equals("uuid")) {
                sender.sendMessage(Bukkit.getPlayerExact(args[1]).getUniqueId().toString());
                return true;
            }
            sender.sendMessage(Config.getString("messages.sender_not_a_player"));
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            GUIEvents.openGUI(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("invite") && args.length > 1) {
            IslandEvents.inviteIsland(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("inviteaccept") && args.length > 1) {
            IslandEvents.inviteAccept(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("visit") && args.length > 1) {
            IslandEvents.visitIsland(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("visitallow") && args.length > 1) {
            IslandEvents.visitAllow(player, args[1]);
            return true;
        }

        return false;
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