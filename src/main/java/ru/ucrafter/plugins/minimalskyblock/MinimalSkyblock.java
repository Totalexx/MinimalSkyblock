package ru.ucrafter.plugins.minimalskyblock;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import ru.ucrafter.plugins.minimalskyblock.utils.Config;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandDB;

public final class MinimalSkyblock extends JavaPlugin {
    private static MinimalSkyblock instance;
    private static Logger log;
    private static IslandDB database;

    public void onEnable() {
        instance = this;
        log = getLogger();
        YMLFiles();
        logPluginInfo();
        database = new IslandDB();
        getCommand("is").setExecutor(new Commands());
        createNotFoundSchematic();
    }

    public void onDisable() {
    }

    public void YMLFiles() {
        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            log.info("Config.yml file not found. Creating...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    }

    public static Logger getLog() {
        return log;
    }

    public static MinimalSkyblock getInstance() {
        return instance;
    }

    public static IslandDB getDatabase() {
        return database;
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
            MinimalSkyblock.getInstance().saveResource("default.schematic", false);
        }
        log.info("Schematic file not found. Creating...");
    }
}
