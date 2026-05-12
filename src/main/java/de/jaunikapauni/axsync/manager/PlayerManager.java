package de.jaunikapauni.axsync.manager;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

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

    public void setPlayerExperience(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET level = ?, progress = ? WHERE uuid = ?")){
                ps.setInt(1, p.getLevel());
                ps.setFloat(2, p.getExp());
                ps.setString(3, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerAirLevel(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET airlevel = ? WHERE uuid = ?")){
                ps.setInt(1, p.getRemainingAir());
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String serializeInventory(Inventory inv) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)){
            boos.writeInt(inv.getSize());
            for(ItemStack item : inv.getContents()){
                boos.writeObject(item);
            }
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public void setPlayerInventory(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET inventory = ? WHERE uuid = ?")){
                ps.setString(1, serializeInventory(p.getInventory()));
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerEnderChest(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET enderchest = ? WHERE uuid = ?")){
                ps.setString(1, serializeInventory(p.getEnderChest()));
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deserializeInventory(Inventory inv, String data){
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try(BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)){
            int size = bois.readInt();
            ItemStack[] items = new ItemStack[size];
            for(int i = 0; i < size; i++){
                items[i] = (ItemStack) bois.readObject();
            }
            inv.setContents(items);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadPlayerInventory(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT inventory FROM playerdata WHERE uuid = ?")){
                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String data = rs.getString("inventory");
                    if(data != null){
                        deserializeInventory(p.getInventory(), data);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadPlayerEnderChest(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT enderchest FROM playerdata WHERE uuid = ?")){
                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String data = rs.getString("enderchest");
                    if(data != null){
                        deserializeInventory(p.getEnderChest(), data);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
