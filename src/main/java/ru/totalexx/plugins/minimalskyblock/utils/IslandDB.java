package ru.totalexx.plugins.minimalskyblock.utils;

import org.bukkit.Location;
import ru.totalexx.plugins.minimalskyblock.MinimalSkyblock;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class IslandDB {

    public enum VisitStatus{
        ANYBODY {
            public VisitStatus nextStatus() {
                return BY_INVITATION;
            }
        },
        BY_INVITATION {
            public VisitStatus nextStatus() {
                return NOBODY;
            }
        },
        NOBODY {
            public VisitStatus nextStatus() {
                return ANYBODY;
            }
        };

        public abstract VisitStatus nextStatus();
    }

    private Statement statement;

    private static final String URL_DB = "jdbc:sqlite:"
            + MinimalSkyblock.getInstance().getDataFolder()
            + File.separator + "islands.db";

    public IslandDB() {
        openConnection();
    }

    public void openConnection() {
        try {
            statement = DriverManager.getConnection(URL_DB).createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            Connection connection = statement.getConnection();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDB() {
        try {
            Class.forName("org.sqlite.JDBC");

            File fileDB = new File(URL_DB.substring(12));
            if(!fileDB.exists())
                fileDB.createNewFile();

            Statement statement = DriverManager.getConnection(URL_DB).createStatement();

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS islands " +
                            "(`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "`posX` INTEGER, " +
                            "`posZ` INTEGER, " +
                            "`spawnPosition` VARCHAR," +
                            "`nextDirection` VARCHAR DEFAULT 'TOP', " +
                            "`visitStatus` VARCHAR DEFAULT 'BY_INVITATION', " +
                            "`softDelete` INTEGER DEFAULT 0," +
                            "`timestamp` DATETIME)");

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS players " +
                            "(`UUID` VARCHAR PRIMARY KEY, " +
                            "`island` INTEGER DEFAULT 0," +
                            "`timestamp` DATETIME)");

            Connection connection = statement.getConnection();
            statement.close();
            connection.close();

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addIsland(int id, int posX, int posZ, String nextDirection) {
        int islandSizeX = Config.getInt("islands.size_x");
        int islandSizeZ = Config.getInt("islands.size_z");
        int islandBetween = Config.getInt("islands.distance_between");

        double xSpawn = (islandSizeX + islandBetween) * posX + Config.getInt("islands.teleport_deviation.x") + 0.5d;
        double ySpawn = Config.getIslandHeight() + Config.getInt("islands.teleport_deviation.y");
        double zSpawn = (islandSizeZ + islandBetween) * posZ + Config.getInt("islands.teleport_deviation.z") + 0.5d;

        String spawnPosition = xSpawn + ";" + ySpawn + ";" + zSpawn;

        try {
            statement.execute(
                    String.format(
                            "INSERT INTO islands " +
                                    "(id, posX, posZ, nextDirection, spawnPosition, timestamp) " +
                                    "VALUES (%d, %d, %d, '%s', '%s', '%s')",
                            id,
                            posX,
                            posZ,
                            nextDirection,
                            spawnPosition,
                            new Date()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void joinToIsland(UUID playerID, int idIsland) {
        try {
            statement.execute(String.format(
                    "UPDATE players SET island = %d WHERE UUID = '%s'",
                    idIsland,
                    playerID));

            statement.execute(String.format(
                    "INSERT OR IGNORE INTO players (UUID, island, timestamp) VALUES ('%s', %d, '%s')",
                    playerID,
                    idIsland,
                    new Date()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasIsland(UUID playerID) {
        boolean hasIsland = false;

        try {
            ResultSet result = statement.executeQuery(String.format("SELECT island FROM players WHERE UUID = '%s'", playerID.toString()));
            if (result.isBeforeFirst()) {
                hasIsland = result.getInt("island") != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasIsland;
    }

    public void setVisitStatus(int islandID, VisitStatus status) {
        try {
            statement.execute(String.format("UPDATE islands SET visitStatus = '%s' WHERE id = %d", status.toString(), islandID));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSpawnPosition(int islandID, Location location) {
        try {
            double x = location.getBlockX() + 0.5d;
            double y = location.getBlockY();
            double z = location.getBlockZ() + 0.5d;
            String spawnPosition = x + ";" + y + ";" + z;
            statement.execute(String.format("UPDATE islands SET spawnPosition = '%s' WHERE id = %d",
                    spawnPosition,
                    islandID));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location getSpawnPosition(UUID playerID) {
        Location location = null;
        try {
            ResultSet result = statement.executeQuery(
                    String.format("SELECT spawnPosition FROM islands WHERE id = " +
                                    "(SELECT island FROM players WHERE UUID = '%s')",
                    playerID));

            if (result.isBeforeFirst()) {
                String[] position = result.getString("spawnPosition").split(";");
                location = new Location(Config.getIslandsWorld(),
                        Double.valueOf(position[0]),
                        Double.valueOf(position[1]),
                        Double.valueOf(position[2]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return location;
    }

    public IslandPosition getPositionLastIsland() {
        IslandPosition position = null;
        try{
            ResultSet result = statement.executeQuery("SELECT id, posX, posZ, nextDirection FROM islands ORDER BY id DESC LIMIT 1");
            if (result.isBeforeFirst()) {
                position = getPosition(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return position;
    }

    public HashSet<UUID> getPlayers(int islandID) {
        HashSet<UUID> uuids = new HashSet<>();

        try {
            ResultSet result = statement.executeQuery(
                    String.format("SELECT UUID FROM players WHERE island = %d", islandID));

            if (result.isBeforeFirst()) {
                ////
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uuids;
    }

    public int getIslandID(UUID playerID) {
        int islandID = 0;

        try {
            ResultSet result = statement.executeQuery(String.format("SELECT island FROM players WHERE UUID = '%s'", playerID.toString()));
            if (result.isBeforeFirst()) {
                islandID = result.getInt("island");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return islandID;
    }

    public VisitStatus getVisitStatus(int islandID) {
        VisitStatus visitStatus = VisitStatus.BY_INVITATION;

        try {
            ResultSet result = statement.executeQuery(String.format("SELECT visitStatus FROM islands WHERE id = %d", islandID));
            if (result.isBeforeFirst()) {
                visitStatus = VisitStatus.valueOf(result.getString("visitStatus"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return visitStatus;
    }

    private IslandPosition getPosition(ResultSet result) throws SQLException {
        return new IslandPosition(
                result.getInt("id"),
                result.getInt("posX"),
                result.getInt("posZ"),
                IslandPosition.NextDirection.valueOf(result.getString("nextDirection")));
    }
}
