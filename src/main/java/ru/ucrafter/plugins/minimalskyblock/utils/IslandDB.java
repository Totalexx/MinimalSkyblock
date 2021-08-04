package ru.ucrafter.plugins.minimalskyblock.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import ru.ucrafter.plugins.minimalskyblock.MinimalSkyblock;
import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition.NextDirection;

public class IslandDB {
    private final String urlDB;

    public IslandDB() {
        urlDB = "jdbc:sqlite:" + MinimalSkyblock.getInstance().getDataFolder() + File.separator + "islands.db";

        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS islands (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `posX` INTEGER, `poxZ` INTEGER, `leader` VARCHAR, `members` VARCHAR DEFAULT NULL, `nextDirection` VARCHAR DEFAULT 'TOP')");

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addIsland(int posX, int poxZ, String nextDirection, String leader) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            statement.executeUpdate(String.format("INSERT INTO islands (posX, poxZ, nextDirection, leader) VALUES (%d, %d, '%s', '%s')", posX, poxZ, nextDirection, leader));

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public IslandPosition getIslandPositionByLeader(String nicknameLeader) {
        return this.getIslandPosition(String.format("WHERE leader == '%s' ORDER BY id DESC LIMIT 1", nicknameLeader));
    }

    public IslandPosition getPositionLastIsland() {
        return getIslandPosition("ORDER BY id DESC LIMIT 1");
    }

    private IslandPosition getIslandPosition(String addSQL) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT posX, poxZ, nextDirection FROM islands " + addSQL);
            IslandPosition position = null;
            if (result.isBeforeFirst()) {
                position = new IslandPosition(result.getInt("posX"),
                        result.getInt("poxZ"), NextDirection.valueOf(result.getString("nextDirection")));
            }

            statement.close();
            connection.close();

            return position;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}