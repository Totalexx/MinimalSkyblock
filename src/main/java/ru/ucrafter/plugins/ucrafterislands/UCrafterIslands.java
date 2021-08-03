package ru.ucrafter.plugins.ucrafterislands;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import ru.ucrafter.plugins.ucrafterislands.utils.Config;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandDB;

public final class UCrafterIslands extends JavaPlugin {
    private static UCrafterIslands instance;
    private static Logger log;
    private static IslandDB database;

    public void onEnable() {
        instance = this;
        log = getLogger();
        YMLFiles();
        logPluginInfo();
        database = new IslandDB();
        getCommand("is").setExecutor(new Commands());
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

    public static UCrafterIslands getInstance() {
        return instance;
    }

    public static IslandDB getDatabase() {
        return database;
    }

    public static File getFolder() {
        return instance.getDataFolder();
    }

    public static void logPluginInfo() {
        String nameAndVersion = "|     UCrafterIslands-" + Config.getString("version") + "     |";
        String headerAndFooter = "+" + new String(new char[nameAndVersion.length() - 2]).replace("\0", "-") + "+";
        log.info(headerAndFooter);
        log.info(nameAndVersion);
        log.info(headerAndFooter);
    }
}
