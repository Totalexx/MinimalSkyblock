package ru.ucrafter.plugins.ucrafterislands.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.ucrafter.plugins.ucrafterislands.UCrafterIslands;

public class Config {
    public static World getIslandsWorld() {
        return Bukkit.getWorld(getString("islands.world"));
    }

    public static int getIslandHeight() {
        return getInt("islands.height");
    }

    public static int getInt(String name) {
        return UCrafterIslands.getInstance().getConfig().getInt(name);
    }

    public static String getString(String name) {
        return UCrafterIslands.getInstance().getConfig().getString(name).replace('&', '\u007a');
    }
}
