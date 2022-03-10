package de.stuart.speedometer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    public static Main instance;
    public static final Logger log = Bukkit.getLogger();

    @Override
    public void onEnable() {
        instance = this;
        
        log.log(Level.SEVERE, "[Speedometer] Don't drink and drive!");
                
        loadConfig();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        getCommand("SpeedometerReload").setExecutor(new ReloadCommand());

    }

    @Override
    public void onDisable() {
        saveConfig();
    }
    
    public void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
