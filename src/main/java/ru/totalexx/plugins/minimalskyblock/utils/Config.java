package ru.totalexx.plugins.minimalskyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.totalexx.plugins.minimalskyblock.MinimalSkyblock;

import java.util.List;

public final class Config {
    public static World getIslandsWorld() {
        return Bukkit.getWorld(getString("islands.world"));
    }

    public static int getIslandHeight() {
        return getInt("islands.height");
    }

    public static int getInt(String name) {
        return MinimalSkyblock.getInstance().getConfig().getInt(name);
    }

    public static String getString(String name, Object... args) {
        return String.format(MinimalSkyblock.getInstance().getConfig().getString(name).replaceAll("&", "\u00a7"), args);
    }

    public static boolean getBoolean(String name) {
        return MinimalSkyblock.getInstance().getConfig().getBoolean(name);
    }

    public static String getMessage(String name, Object... args) {
        String message = "messages." + name;
        return getBoolean("settings.print_prefix") ?
                getString("messages.chat_prefix") + getString(message, args) : getString(message, args);
    }
    public static List<String> getList(String name) {
        List<String> list = MinimalSkyblock.getInstance().getConfig().getStringList(name);
        list.replaceAll(s -> s.replaceAll("&", "\u00a7"));
        return list;
    }

    public static int getMaxPlayersOnIsland() {
        return getInt("settings.max_players_on_island");
    }

}
