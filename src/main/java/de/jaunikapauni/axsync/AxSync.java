package de.jaunikapauni.axsync;

import de.jaunikapauni.axsync.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxSync extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        try{
            databaseManager = new DatabaseManager(this);
            if(databaseManager.initDatabaseTable1() == false){
                getLogger().severe("Error creating playerdata table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
