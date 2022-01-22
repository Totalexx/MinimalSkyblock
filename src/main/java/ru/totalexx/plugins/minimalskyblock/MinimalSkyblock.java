package ru.totalexx.plugins.minimalskyblock;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.totalexx.plugins.minimalskyblock.gui.InventoryGUIListener;
import ru.totalexx.plugins.minimalskyblock.utils.Config;
import ru.totalexx.plugins.minimalskyblock.utils.IslandDB;
import ru.totalexx.plugins.minimalskyblock.utils.WorldEditGuard;

public final class MinimalSkyblock extends JavaPlugin {

    private static MinimalSkyblock instance;
    private static Logger log;

    public void onEnable() {
        instance = this;
        log = getLogger();

        saveDefaultConfig();
        createNotFoundSchematic();
        IslandDB islandDB = new IslandDB();
        islandDB.createDB();
        islandDB.closeConnection();

        Bukkit.getPluginManager().registerEvents(new InventoryGUIListener(), this);
        getCommand("is").setExecutor(new Commands());
        WorldEditGuard.setFlagsGlobalRegion();

        logPluginInfo();
    }

    public void onDisable() {}

    public static Logger getLog() {
        return log;
    }

    public static MinimalSkyblock getInstance() {
        return instance;
    }

    public static File getFolder() {
        return instance.getDataFolder();
    }

    public static void logPluginInfo() {
        String nameAndVersion = "|     MinimalSkyblock-" + Config.getString("version") + "     |";
        String headerAndFooter = "+" + new String(new char[nameAndVersion.length() - 2]).replace("\0", "-") + "+";
        log.info(headerAndFooter);
        log.info(nameAndVersion);
        log.info(headerAndFooter);
    }

    public static void createNotFoundSchematic() {
        File schematic = new File(MinimalSkyblock.getFolder()
                + File.separator
                + Config.getString("islands.name_schematic_file")
                + ".schematic");
        if (!schematic.exists()) {
            instance.saveResource("default.schematic", false);
            log.info("Schematic file not found. Creating...");
        }
    }
}