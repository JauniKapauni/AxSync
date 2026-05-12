package de.jaunikapauni.axsync.listener;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    AxSync reference;
    public PlayerQuitListener(AxSync reference){
        this.reference = reference;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);
        Player p = e.getPlayer();
        reference.getPlayerManager().setPlayerHealth(p);
        reference.getPlayerManager().setPlayerFoodLevel(p);
    }
}
