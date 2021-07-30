package ru.ucrafter.plugins.ucrafterislands;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class UCrafterIslands extends JavaPlugin {

    Logger log = getLogger();

    @Override
    public void onEnable() {
        log.info("*-----------------------*");
        log.info("*    UCrafterIslands    *");
        log.info("*-----------------------*");

        Bukkit.getPluginManager().registerEvents(new Handler(), this);
        getCommand("is").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {

    }
}
