package me.needenoughsleep.displaytopplayers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisplayTopPlayers extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().addDefault("period", 20L);
        getConfig().options().copyDefaults(true);
        saveConfig();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            TopplayerExpansion expansion = new TopplayerExpansion(this);
            expansion.register();
            expansion.start();
        }
    }
}
