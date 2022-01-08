package ru.ucrafter.plugins.minimalskyblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.ucrafter.plugins.minimalskyblock.gui.GUIEvents;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;

import java.util.ArrayList;


public class Commands implements CommandExecutor, TabCompleter {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.getString("messages.sender_not_a_player"));
            return true;
        }
        Player player = (Player) sender;
        GUIEvents.openGUI(player);
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