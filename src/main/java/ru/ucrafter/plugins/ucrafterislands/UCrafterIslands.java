package ru.ucrafter.plugins.ucrafterislands;

import java.io.File;
import java.util.logging.Logger;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandDB;
import ru.ucrafter.plugins.ucrafterislands.utils.IslandPosition;
import ru.ucrafter.plugins.ucrafterislands.utils.WESchematic;

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
