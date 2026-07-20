package de.jaunikapauni.axsync.listener;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    AxSync reference;
    public PlayerJoinListener(AxSync reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        double health = p.getHealth();
        int foodlevel = p.getFoodLevel();
        GameMode gameMode = p.getGameMode();
        float saturation = p.getSaturation();
        int airlevel = p.getRemainingAir();
        int level = p.getLevel();
        float progress = p.getExp();
        String inventory = reference.getPlayerManager().serializeInventory(p.getInventory());
        String enderchest = reference.getPlayerManager().serializeInventory(p.getEnderChest());
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            try(Connection conn = reference.getDatabaseManager().getConnection()){
                try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM playerdata WHERE uuid = ?")){
                    ps.setString(1, uuid.toString());
                    ResultSet rs = ps.executeQuery();
                    if(!rs.next()){
                        try(PreparedStatement fillTable = conn.prepareStatement("INSERT INTO playerdata (uuid, health, foodlevel, gamemode, saturation, level, progress, airlevel, inventory, enderchest) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
                            fillTable.setString(1, uuid.toString());
                            fillTable.setDouble(2, health);
                            fillTable.setInt(3, foodlevel);
                            fillTable.setString(4,gameMode.toString());
                            fillTable.setFloat(5, saturation);
                            fillTable.setInt(6, level);
                            fillTable.setFloat(7, progress);
                            fillTable.setInt(8, airlevel);
                            fillTable.setString(9, inventory);
                            fillTable.setString(10, enderchest);
                            fillTable.executeUpdate();
                            Bukkit.getScheduler().runTask(reference, () -> {
                                p.sendActionBar(ChatColor.GREEN + "Playerdata were created!");
                            });
                        }
                    } else {
                        double DBhealth = rs.getDouble("health");
                        int DBfoodlevel = rs.getInt("foodlevel");
                        String DBgameMode = rs.getString("gamemode");
                        float DBsaturation = rs.getFloat("saturation");
                        int DBlevel = rs.getInt("level");
                        float DBprogress = rs.getFloat("progress");
                        int DBairlevel = rs.getInt("airlevel");
                        String DBinventory = rs.getString("inventory");
                        String DBenderchest = rs.getString("enderchest");
                        Bukkit.getScheduler().runTask(reference, () -> {
                            p.setHealth(DBhealth);
                            p.setFoodLevel(DBfoodlevel);
                            p.setGameMode(GameMode.valueOf(DBgameMode));
                            p.setSaturation(DBsaturation);
                            p.setLevel(DBlevel);
                            p.setExp(DBprogress);
                            p.setRemainingAir(DBairlevel);
                            p.getInventory().setContents(reference.getPlayerManager().deserializeInventory(DBinventory));
                            p.getEnderChest().setContents(reference.getPlayerManager().deserializeInventory(DBenderchest));
                            p.sendActionBar(ChatColor.GREEN + "Your data was loaded!");
                        });
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
