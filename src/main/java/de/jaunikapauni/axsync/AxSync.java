package de.jaunikapauni.axsync;

import de.jaunikapauni.axsync.command.EnderChestCommand;
import de.jaunikapauni.axsync.command.InventoryCommand;
import de.jaunikapauni.axsync.listener.PlayerJoinListener;
import de.jaunikapauni.axsync.listener.PlayerQuitListener;
import de.jaunikapauni.axsync.manager.DatabaseManager;
import de.jaunikapauni.axsync.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

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
        getCommand("inventory").setExecutor(new InventoryCommand(this));
        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        getLogger().info("");
        getLogger().info("----------------------------------------");
        getLogger().info("Name: " + getName());
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info(String.join("Authors: " + ", ", getDescription().getAuthors()));
        getLogger().info("----------------------------------------");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Player p : Bukkit.getOnlinePlayers()){
            try {
                playerManager.setPlayerData(p.getUniqueId(), p.getHealth(), p.getFoodLevel(), p.getGameMode(), p.getSaturation(), p.getRemainingAir(), p.getLevel(), p.getExp(), playerManager.serializeInventory(p.getInventory()), playerManager.serializeInventory(p.getEnderChest()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        databaseManager.close();
    }
}
