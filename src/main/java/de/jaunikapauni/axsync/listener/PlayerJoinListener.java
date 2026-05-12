package de.jaunikapauni.axsync.listener;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    AxSync reference;
    public PlayerJoinListener(AxSync reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM playerdata WHERE uuid = ?")){
                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                if(!rs.next()){
                    try(PreparedStatement fillTable = conn.prepareStatement("INSERT INTO playerdata (uuid, health, foodlevel, gamemode, saturation, level, progress, airlevel, inventory, enderchest) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
                        fillTable.setString(1, p.getUniqueId().toString());
                        fillTable.setDouble(2, p.getHealth());
                        fillTable.setInt(3, p.getFoodLevel());
                        fillTable.setString(4, p.getGameMode().toString());
                        fillTable.setFloat(5, p.getSaturation());
                        fillTable.setInt(6, p.getLevel());
                        fillTable.setFloat(7, p.getExp());
                        fillTable.setInt(8, p.getRemainingAir());
                        fillTable.setString(9, reference.getPlayerManager().serializeInventory(p.getInventory()));
                        fillTable.setString(10, reference.getPlayerManager().serializeInventory(p.getEnderChest()));
                        fillTable.executeUpdate();
                        p.sendMessage("Playerdata were created!");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    double health = rs.getDouble("health");
                    int foodlevel = rs.getInt("foodlevel");
                    String gameMode = rs.getString("gamemode");
                    float saturation = rs.getFloat("saturation");
                    int level = rs.getInt("level");
                    float progress = rs.getFloat("progress");
                    int airlevel = rs.getInt("airlevel");
                    reference.getPlayerManager().loadPlayerInventory(p);
                    reference.getPlayerManager().loadPlayerEnderChest(p);
                    p.setHealth(health);
                    p.setFoodLevel(foodlevel);
                    p.setGameMode(GameMode.valueOf(gameMode));
                    p.setSaturation(saturation);
                    p.setLevel(level);
                    p.setExp(progress);
                    p.setRemainingAir(airlevel);
                    p.sendMessage("Your health, foodlevel, gamemode, saturation, experience, airlevel, inventory and enderchest were loaded!");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
