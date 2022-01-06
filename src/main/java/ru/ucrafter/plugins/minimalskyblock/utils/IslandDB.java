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

    private static final String URL_DB = "jdbc:sqlite:"
            + MinimalSkyblock.getInstance().getDataFolder()
            + File.separator + "islands.db";

    public static void createDB() {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();

            File fileDB = new File(URL_DB.substring(12));
            if(!fileDB.exists())
                fileDB.createNewFile();

            Statement statement = openConnection();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS islands (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `posX` INTEGER, `poxZ` INTEGER, `leader` VARCHAR, `members` VARCHAR DEFAULT NULL, `nextDirection` VARCHAR DEFAULT 'TOP', `canAnyoneVisit` BOOLEAN DEFAULT FALSE)");

            closeConnection(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addIsland(int posX, int poxZ, String nextDirection, String leader) {
        try {
            Statement statement = openConnection();

            statement.executeUpdate(String.format("INSERT INTO islands (posX, poxZ, nextDirection, leader) VALUES (%d, %d, '%s', '%s')", posX, poxZ, nextDirection, leader));

            closeConnection(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMemberIsland(String nicknameLeader, String nicknamePlayer) {
        return getMembers(nicknameLeader).contains(nicknamePlayer);
    }

    public static void setAnyoneVisitIsland(String nicknameLeader, boolean canAnyoneVisit) {
        try {
            Statement statement = openConnection();

            statement.executeUpdate(String.format("UPDATE islands SET `canAnyoneVisit` = '%b' WHERE `leader` = '%s'", canAnyoneVisit, nicknameLeader));

            closeConnection(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasIsland(String nicknameLeader) {
        try {
            Statement statement = openConnection();

            ResultSet result = statement.executeQuery(String.format("SELECT id FROM islands WHERE leader == '%s'", nicknameLeader));
            boolean canAnyoneVisit = result.isBeforeFirst();

            closeConnection(statement);

            return canAnyoneVisit;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean canAnyoneVisitIsland(String nicknameLeader) {
        try {
            Statement statement = openConnection();

            ResultSet result = statement.executeQuery(String.format("SELECT canAnyoneVisit FROM islands WHERE leader == '%s'", nicknameLeader));
            boolean canAnyoneVisit = false;
            if (result.isBeforeFirst()) {
                canAnyoneVisit = Boolean.parseBoolean(result.getString("canAnyoneVisit"));
            }

            closeConnection(statement);

            return canAnyoneVisit;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static IslandPosition getIslandPositionByLeader(String nicknameLeader) {
        return getIslandPosition(String.format("WHERE leader == '%s'", nicknameLeader));
    }

    public static IslandPosition getPositionLastIsland() {
        return getIslandPosition("ORDER BY id DESC LIMIT 1");
    }

    private static IslandPosition getIslandPosition(String addSQL) {
        try {
            Statement statement = openConnection();

            ResultSet result = statement.executeQuery("SELECT posX, poxZ, nextDirection FROM islands " + addSQL);
            IslandPosition position = null;
            if (result.isBeforeFirst()) {
                position = new IslandPosition(result.getInt("posX"),
                        result.getInt("poxZ"), NextDirection.valueOf(result.getString("nextDirection")));
            }

            closeConnection(statement);
            return position;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getMembers(String nicknameLeader) {
        try {
            Statement statement = openConnection();

            ResultSet result = statement.executeQuery(String.format("SELECT members FROM islands WHERE leader == '%s'", nicknameLeader));
            List<String> members = new ArrayList<>();

            if (result.isBeforeFirst()) {
                String stringMembers = result.getString("members");
                if (!(stringMembers == null || stringMembers.equals(""))) {
                    members.addAll(Arrays.asList(stringMembers.split(",")));
                }
            }

            closeConnection(statement);

            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setMembers(String nicknameLeader, List<String> members) {
        try {
            Statement statement = openConnection();

            StringJoiner sjMembers = new StringJoiner(",");
            for(String member : members) {
                sjMembers.add(member);
            }

            statement.executeUpdate(String.format("UPDATE islands SET `members` = '%s' WHERE `leader` = '%s'", sjMembers, nicknameLeader));

            closeConnection(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Statement openConnection() throws SQLException {
        return DriverManager.getConnection(URL_DB).createStatement();
    }

    private static void closeConnection(Statement statement) throws SQLException {
        Connection connection = statement.getConnection();
        statement.close();
        connection.close();
    }
}