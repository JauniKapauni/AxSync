package de.jaunikapauni.axsync.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.jaunikapauni.axsync.AxSync;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    HikariDataSource hikari;

    public DatabaseManager(JavaPlugin plugin){
        FileConfiguration fileConfiguration = plugin.getConfig();

        String host = fileConfiguration.getString("database.host");
        int port = fileConfiguration.getInt("database.port");
        String database = fileConfiguration.getString("database.database");
        String username = fileConfiguration.getString("database.username");
        String password = fileConfiguration.getString("database.password");

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikari = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

    public boolean initDatabaseTable1(){
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS playerdata (uuid VARCHAR(255), health DOUBLE, foodlevel INT, gamemode ENUM('SURVIVAL', 'CREATIVE', 'ADVENTURE', 'SPECTATOR'), saturation FLOAT, level INT, progress FLOAT, airlevel INT, inventory TEXT, enderchest TEXT)")){
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
