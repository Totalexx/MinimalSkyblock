package ru.ucrafter.plugins.minimalskyblock.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

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

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS islands (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `posX` INTEGER, `poxZ` INTEGER, `leader` VARCHAR, `members` VARCHAR DEFAULT NULL, `nextDirection` VARCHAR DEFAULT 'TOP', `canAnyoneVisit` BOOLEAN DEFAULT FALSE)");

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

    public boolean isMemberIsland(String nicknameLeader, String nicknamePlayer) {
        return  getMembers(nicknameLeader).contains(nicknamePlayer);
    }

    public void setAnyoneVisitIsland(String nicknameLeader, boolean canAnyoneVisit) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            statement.executeUpdate(String.format("UPDATE islands SET `canAnyoneVisit` = '%b' WHERE `leader` = '%s'", canAnyoneVisit, nicknameLeader));

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasIsland(String nicknameLeader) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(String.format("SELECT id FROM islands WHERE leader == '%s'", nicknameLeader));
            boolean canAnyoneVisit = result.isBeforeFirst();

            statement.close();
            connection.close();

            return canAnyoneVisit;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean canAnyoneVisitIsland(String nicknameLeader) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(String.format("SELECT canAnyoneVisit FROM islands WHERE leader == '%s'", nicknameLeader));
            boolean canAnyoneVisit = false;
            if (result.isBeforeFirst()) {
                canAnyoneVisit = Boolean.parseBoolean(result.getString("canAnyoneVisit"));
            }

            statement.close();
            connection.close();

            return canAnyoneVisit;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public IslandPosition getIslandPositionByLeader(String nicknameLeader) {
        return getIslandPosition(String.format("WHERE leader == '%s'", nicknameLeader));
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

    public List<String> getMembers(String nicknameLeader) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(String.format("SELECT members FROM islands WHERE leader == '%s'", nicknameLeader));
            List<String> members = new ArrayList<>();
            if (result.isBeforeFirst()) {
                String stringMembers = result.getString("members");
                if (!(stringMembers == null || stringMembers.equals(""))) {
                    members.addAll(Arrays.asList(stringMembers.split(",")));
                }
            }

            statement.close();
            connection.close();

            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setMembers(String nicknameLeader, List<String> members) {
        try {
            Connection connection = DriverManager.getConnection(urlDB);
            Statement statement = connection.createStatement();

            StringJoiner sjMembers = new StringJoiner(",");
            for(String member : members) {
                sjMembers.add(member);
            }
            statement.executeUpdate(String.format("UPDATE islands SET `members` = '%s' WHERE `leader` = '%s'", sjMembers, nicknameLeader));

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}