package ru.ucrafter.plugins.ucrafterislands;

import org.bukkit.plugin.java.JavaPlugin;
import ru.ucrafter.plugins.ucrafterislands.utils.Vector2;

import java.io.File;
import java.util.logging.Logger;

public final class UCrafterIslands extends JavaPlugin {

    private static UCrafterIslands instance;
    private static Logger log;

    @Override
    public void onEnable(){
        instance = this;
        log = getLogger();

        log.info("*-----------------------*");
        log.info("*    UCrafterIslands    *");
        log.info("*-----------------------*");

//        Bukkit.getPluginManager().registerEvents(new Handler(), this);
        getCommand("is").setExecutor(new Commands());

        YMLFiles();
        IslandDB db = new IslandDB();
        db.addIsland(-1, -2, "totalexx");
        Vector2 pos = db.getIslandXYByLeader("totalexx");
        log.info("X position:" + pos.x);
        log.info("Y position:" + pos.y);
    }

    @Override
    public void onDisable() {

    }

    public void YMLFiles() {
        File config = new File(getDataFolder() + File.separator + "config.yml");
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
}
