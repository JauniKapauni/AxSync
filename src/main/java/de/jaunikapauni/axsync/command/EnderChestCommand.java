package de.jaunikapauni.axsync.command;

import de.jaunikapauni.axsync.AxSync;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EnderChestCommand implements CommandExecutor {

    AxSync reference;
    public EnderChestCommand(AxSync reference){
        this.reference = reference;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player)){
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("axsync.enderchest")){
            p.sendMessage(ChatColor.RED + "You are missing the permission! [axsync.enderchest]");
            return true;
        }
        if(args.length != 1){
            p.sendMessage(ChatColor.RED + "Please provide a player!");
            return true;
        }
        UUID uuid;
        try{
            uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            try(Connection conn = reference.getDatabaseManager().getConnection()){
                try(PreparedStatement ps = conn.prepareStatement("SELECT enderchest FROM playerdata WHERE uuid = ?")){
                    ps.setString(1, uuid.toString());
                    try(ResultSet rs = ps.executeQuery()){
                        if(!rs.next()){
                            Bukkit.getScheduler().runTask(reference, () -> {
                                p.sendMessage(ChatColor.RED + "No data found!");
                            });
                            return;
                        }
                        String data = rs.getString("enderchest");
                        ItemStack[] contents = reference.getPlayerManager().deserializeInventory(data);
                        Bukkit.getScheduler().runTask(reference, () -> {
                            Inventory inv = Bukkit.createInventory(null, 54, args[0]);
                            inv.setContents(contents);
                            p.openInventory(inv);
                        });
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return true;
    }
}
