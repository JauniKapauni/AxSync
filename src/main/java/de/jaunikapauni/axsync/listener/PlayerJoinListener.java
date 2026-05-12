package de.jaunikapauni.axsync.listener;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
                    try(PreparedStatement fillTable = conn.prepareStatement("INSERT INTO playerdata (uuid, health, foodlevel, gamemode) VALUES (?, ?, ?, ?)")){
                        fillTable.setString(1, p.getUniqueId().toString());
                        fillTable.setDouble(2, p.getHealth());
                        fillTable.setInt(3, p.getFoodLevel());
                        fillTable.setString(4, p.getGameMode().toString());
                        fillTable.executeUpdate();
                        p.sendMessage("Playerdata were created!");
                    }
                } else {
                    double health = rs.getDouble("health");
                    int foodlevel = rs.getInt("foodlevel");
                    String gameMode = rs.getString("gamemode");
                    p.setHealth(health);
                    p.setFoodLevel(foodlevel);
                    p.setGameMode(GameMode.valueOf(gameMode));
                    p.sendMessage("Your health, foodlevel and gamemode were loaded!");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
