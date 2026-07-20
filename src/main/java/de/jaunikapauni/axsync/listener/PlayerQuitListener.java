package de.jaunikapauni.axsync.listener;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerQuitListener implements Listener {
    AxSync reference;
    public PlayerQuitListener(AxSync reference){
        this.reference = reference;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException {
        e.setQuitMessage(null);
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        double health = p.getHealth();
        int foodlevel = p.getFoodLevel();
        GameMode gameMode = p.getGameMode();
        float saturation = p.getSaturation();
        int remainingAir = p.getRemainingAir();
        int level = p.getLevel();
        float exp = p.getExp();
        String inventory = reference.getPlayerManager().serializeInventory(p.getInventory());
        String enderchest = reference.getPlayerManager().serializeInventory(p.getEnderChest());
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            try {
                reference.getPlayerManager().setPlayerData(uuid, health, foodlevel, gameMode, saturation, remainingAir, level, exp, inventory, enderchest);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
