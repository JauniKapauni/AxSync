package de.jaunikapauni.axsync.manager;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerManager {
    AxSync reference;
    public PlayerManager(AxSync reference){
        this.reference = reference;
    }

    public void setPlayerHealth(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET health = ? WHERE uuid = ?")){
                ps.setDouble(1, p.getHealth());
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerFoodLevel(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET foodlevel = ? WHERE uuid = ?")){
                ps.setInt(1, p.getFoodLevel());
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerGameMode(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET gamemode = ? WHERE uuid = ?")){
                ps.setString(1, p.getGameMode().toString());
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerSaturation(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET saturation = ? WHERE uuid = ?")){
                ps.setFloat(1, p.getSaturation());
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
