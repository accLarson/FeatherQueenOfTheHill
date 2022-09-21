package com.zerek.featherqueenofthehill.managers;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import org.javalite.activejdbc.Base;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private static Connection connection;
    private final FeatherQueenOfTheHill plugin;
    private File file;

    public DatabaseManager(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
        this.initConnection();
        this.initTables();
    }

    public Connection getConnection() {
        try {
            if(connection.isClosed()) {
                this.initConnection();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Unable to receive connection.");
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                Base.close();
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Unable to close DatabaseManager connection.");
            }
        }
    }

    private void initConnection() {
        File folder = this.plugin.getDataFolder();
        if(!folder.exists()) {
            folder.mkdir();
        }
        this.file = new File(folder.getAbsolutePath() + File.separator +  "FeatherQOTH.db");
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
            Base.attach(this.connection);
        } catch (SQLException e) {
            plugin.getLogger().severe("Unable to initialize DatabaseManager connection.");
        }
    }

    private boolean existsTable(String table) {
        try {
            if(!connection.isClosed()) {
                ResultSet rs = connection.getMetaData().getTables(null, null, table, null);
                return rs.next();
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Unable to query table metadata.");
            return false;
        }
    }

    private void initTables() {
        if(!this.existsTable("SCORES")) {
            plugin.getLogger().info("Creating SCORES table.");
            String query = "CREATE TABLE IF NOT EXISTS `SCORES` ("
                    + " `mojang_uuid`               VARCHAR(255) PRIMARY KEY NOT NULL, "
                    + " `updated_at`                DATETIME, "
                    + " `score`                     FLOAT );";
            try {
                if(!connection.isClosed()) {
                    connection.createStatement().execute(query);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Unable to create SCORES table.");
            }
        }
    }
}
