package de.jaunikapauni.axsync;

import de.jaunikapauni.axsync.listener.PlayerJoinListener;
import de.jaunikapauni.axsync.listener.PlayerQuitListener;
import de.jaunikapauni.axsync.manager.DatabaseManager;
import de.jaunikapauni.axsync.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxSync extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    PlayerManager playerManager;
    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        try{
            databaseManager = new DatabaseManager(this);
            playerManager = new PlayerManager(this);
            if(databaseManager.initDatabaseTable1() == false){
                getLogger().severe("Error creating playerdata table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
