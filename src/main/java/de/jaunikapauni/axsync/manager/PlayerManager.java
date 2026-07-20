package de.jaunikapauni.axsync.manager;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.GameMode;
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
import java.util.UUID;

public class PlayerManager {
    AxSync reference;
    public PlayerManager(AxSync reference){
        this.reference = reference;
    }

    public void setPlayerData(UUID uuid, double health, int foodLevel, GameMode gameMode, float saturation, int remainingAir, int level, float exp, String inventory, String enderchest) throws SQLException {
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET health = ?, foodlevel = ?, gamemode = ?, saturation = ?, airlevel = ?, level = ?, progress = ?, inventory = ?, enderchest = ? WHERE uuid = ?")){
                ps.setDouble(1, health);
                ps.setInt(2, foodLevel);
                ps.setString(3, gameMode.toString());
                ps.setFloat(4, saturation);
                ps.setInt(5, remainingAir);
                ps.setInt(6, level);
                ps.setFloat(7, exp);
                ps.setString(8, inventory);
                ps.setString(9, enderchest);
                ps.setString(10, uuid.toString());
            }
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

    public ItemStack[] deserializeInventory(String data){
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try(BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)){
            int size = bois.readInt();
            ItemStack[] items = new ItemStack[size];
            for(int i = 0; i < size; i++){
                items[i] = (ItemStack) bois.readObject();
            }
            return items;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
