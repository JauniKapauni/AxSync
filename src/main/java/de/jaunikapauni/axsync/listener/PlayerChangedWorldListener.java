package de.jaunikapauni.axsync.listener;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.io.IOException;
import java.sql.SQLException;

public class PlayerChangedWorldListener implements Listener {

    AxSync reference;
    public PlayerChangedWorldListener(AxSync reference){
        this.reference = reference;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        boolean fromDisabled = reference.isDisabledWorld(e.getFrom().getName());
        boolean toDisabled = reference.isDisabledWorld(p.getWorld().getName());
        if(fromDisabled && !toDisabled){
            reference.getServer().getScheduler().runTask(reference, () -> {
                reference.getPlayerManager().loadPlayerData(p);
            });
        }
        if(!fromDisabled && toDisabled){
            reference.getServer().getScheduler().runTaskAsynchronously(reference, () -> {
                try {
                    reference.getPlayerManager().savePlayerData(p);
                    reference.getServer().getScheduler().runTask(reference, () -> {
                        p.getInventory().clear();
                        p.getEnderChest().clear();
                    });
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
}
