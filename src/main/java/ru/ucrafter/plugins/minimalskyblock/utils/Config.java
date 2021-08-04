package ru.ucrafter.plugins.minimalskyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.ucrafter.plugins.minimalskyblock.MinimalSkyblock;

public class Config {
    public static World getIslandsWorld() {
        return Bukkit.getWorld(getString("islands.world"));
    }

    public static int getIslandHeight() {
        return getInt("islands.height");
    }

    public static int getInt(String name) {
        return MinimalSkyblock.getInstance().getConfig().getInt(name);
    }

    public static String getString(String name) {
        return MinimalSkyblock.getInstance().getConfig().getString(name).replace('&', '\u00a7');
    }

    public static boolean getBoolean(String name) {
        return MinimalSkyblock.getInstance().getConfig().getBoolean(name);
    }

    public static String getMessage(String name) {
        return getBoolean("settings.print_prefix") ?
                getString("messages.chat_prefix") + getString(name) : getString(name);
    }
}
