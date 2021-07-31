package ru.ucrafter.plugins.ucrafterislands;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandDB;

public final class UCrafterIslands extends JavaPlugin {
    private static UCrafterIslands instance;
    private static Logger log;
    private static IslandDB database;

    public void onEnable() {
        instance = this;
        log = getLogger();

        log.info("*-----------------------*");
        log.info("*    UCrafterIslands    *");
        log.info("*-----------------------*");

        YMLFiles();
        database = new IslandDB();
        getCommand("is").setExecutor(new Commands());
    }

    public void onDisable() {
    }

    public void YMLFiles() {
        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            log.info("Файл конфигурации не найден, генерируем новый...");
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

    public static String getFromConfiguration(String name) {
        return instance.getConfig().getString(name);
    }
}
