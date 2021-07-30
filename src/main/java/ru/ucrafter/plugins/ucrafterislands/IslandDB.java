package ru.ucrafter.plugins.ucrafterislands;

import ru.ucrafter.plugins.ucrafterislands.utils.Vector2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class IslandDB {

    private final String urlDB;
    private final String nameDB = "islands";

    public IslandDB()  {
        urlDB = "jdbc:sqlite:" + UCrafterIslands.getInstance().getDataFolder() + File.separator + "islands.db";
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            Connection connection  = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + nameDB + " (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `posX` INTEGER, `posY` INTEGER, `leader` VARCHAR, `members` VARCHAR DEFAULT NULL)");

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addIsland(int posX, int posY, String leader) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO %s (posX, posY, leader) VALUES (%d, %d, '%s')", nameDB, posX, posY, leader));

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Vector2 getIslandXYByLeader(String nicknameLeader) {
        try {
            Connection connection  = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(String.format("SELECT posX, posY FROM %s WHERE leader == '%s'", nameDB, nicknameLeader));

            Vector2 position = new Vector2(result.getInt("posX"), result.getInt("posY"));

            statement.close();
            connection.close();
            return position;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

}
