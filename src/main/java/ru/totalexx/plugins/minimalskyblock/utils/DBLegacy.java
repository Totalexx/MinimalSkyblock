//package ru.ucrafter.plugins.minimalskyblock.utils;
//
//import java.io.File;
//import java.sql.*;
//import java.time.temporal.ValueRange;
//import java.util.*;
//import java.util.Date;
//
//import org.bukkit.Location;
//import ru.ucrafter.plugins.minimalskyblock.MinimalSkyblock;
//import ru.ucrafter.plugins.minimalskyblock.utils.IslandPosition.NextDirection;
//
//public class DBLegacy {
//
//    public enum VisitStatus{
//        ANYBODY,
//        BY_INVITATION,
//        NOBODY
//    }
//
//    private static final String URL_DB = "jdbc:sqlite:"
//            + MinimalSkyblock.getInstance().getDataFolder()
//            + File.separator + "islands.db";
//
//    public static void createDB() {
//        try {
//            Class.forName("org.sqlite.JDBC");
//
//            File fileDB = new File(URL_DB.substring(12));
//            if(!fileDB.exists())
//                fileDB.createNewFile();
//
//            Statement statement = openConnection();
//
//            statement.executeUpdate(
//                    "CREATE TABLE IF NOT EXISTS islands " +
//                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    "`posX` INTEGER, " +
//                    "`poxZ` INTEGER, " +
//                    "`spawnPosition` VARCHAR," +
//                    "`nextDirection` VARCHAR DEFAULT 'TOP', " +
//                    "`canVisitStatus` VARCHAR DEFAULT 'BY_INVITATION', " +
//                    "`softDelete` INTEGER DEFAULT 0," +
//                    "`timestamp` DATETIME)");
//
//            statement.executeUpdate(
//                    "CREATE TABLE IF NOT EXISTS players " +
//                            "(`UUID` VARCHAR PRIMARY KEY, " +
//                            "`island` INTEGER DEFAULT NULL," +
//                            "`timestamp` DATETIME)");
//
//            closeConnection(statement);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void addIsland(UUID playerID, int posX, int posZ, String nextDirection) {
//        try {
//            Statement statement = openConnection();
//
//            int islandSizeX = Config.getInt("islands.size_x");
//            int islandSizeZ = Config.getInt("islands.size_z");
//            int islandBetween = Config.getInt("islands.distance_between");
//
//            double xSpawn = (islandSizeX + islandBetween) * posX + Config.getInt("islands.teleport_deviation.x") + 0.5d;
//            double ySpawn = Config.getIslandHeight() + Config.getInt("islands.teleport_deviation.y");
//            double zSpawn = (islandSizeZ + islandBetween) * posZ + Config.getInt("islands.teleport_deviation.z") + 0.5d;
//
//            String spawnPosition = xSpawn + ";" + ySpawn + ";" + zSpawn;
//
//            statement.executeUpdate(
//                    String.format(
//                            "INSERT INTO islands (posX, poxZ, nextDirection, leader, spawnPosition, timestamp) VALUES (%d, %d, '%s', '%s', '%s')",
//                            posX,
//                            posZ,
//                            nextDirection,
//                            spawnPosition,
//                            new Date()));
//
//            closeConnection(statement);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static boolean isMemberIsland(String nicknameLeader, String nicknamePlayer) {
//        return getMembers(nicknameLeader).contains(nicknamePlayer);
//    }
//
//    public static void setAnyoneVisitIsland(String nicknameLeader, boolean canAnyoneVisit) {
//        try {
//            Statement statement = openConnection();
//
//            statement.executeUpdate(
//                    String.format(
//                            "UPDATE islands SET `canAnyoneVisit` = '%b' WHERE `leader` = '%s'",
//                            canAnyoneVisit,
//                            nicknameLeader));
//
//            closeConnection(statement);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static boolean hasIsland(String nicknameLeader) {
//        try {
//            Statement statement = openConnection();
//
//            ResultSet result = statement.executeQuery(
//                    String.format("SELECT id FROM islands WHERE leader = '%s' AND softDelete != 1", nicknameLeader));
//            boolean hasIsland = result.isBeforeFirst();
//
//            closeConnection(statement);
//
//            return hasIsland;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public static VisitStatus getVisitStatus(String nicknameLeader) {
//        try {
//            Statement statement = openConnection();
//
//            ResultSet result = statement.executeQuery(
//                    String.format("SELECT canVisitStatus FROM islands WHERE leader = '%s'", nicknameLeader));
//
//            VisitStatus visitStatus = VisitStatus.NOBODY;
//            if (result.isBeforeFirst()) {
//                visitStatus = VisitStatus.valueOf(result.getString("canVisitStatus"));
//            }
//
//            closeConnection(statement);
//
//            return visitStatus;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return VisitStatus.NOBODY;
//        }
//    }
//
//    @Deprecated
//    public static boolean canAnyoneVisitIsland(String nicknameLeader) {
//        try {
//            Statement statement = openConnection();
//
//            ResultSet result = statement.executeQuery(
//                    String.format("SELECT canAnyoneVisit FROM islands WHERE leader = '%s'", nicknameLeader));
//            boolean canAnyoneVisit = false;
//            if (result.isBeforeFirst()) {
//                canAnyoneVisit = Boolean.parseBoolean(result.getString("canAnyoneVisit"));
//            }
//
//            closeConnection(statement);
//
//            return canAnyoneVisit;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public static Location getIslandSpawnPoint() {
//        try {
//            Statement statement = openConnection();
//
//            ResultSet result = statement.executeQuery("SELECT spawnPosition nextDirection FROM islands ");
//            IslandPosition position = null;
//            if (result.isBeforeFirst()) {
//                position = new IslandPosition(result.getInt("posX"),
//                        result.getInt("poxZ"),
//                        NextDirection.valueOf(result.getString("nextDirection")));
//            }
//
//            closeConnection(statement);
//            return position;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static IslandPosition getIslandPositionByLeader(String nicknameLeader) {
//        return getIslandPosition(String.format("WHERE softDelete != 1 AND leader == '%s'", nicknameLeader));
//    }
//
//    public static IslandPosition getPositionLastIsland() {
//        return getIslandPosition("ORDER BY id DESC LIMIT 1");
//    }
//
//    private static IslandPosition getIslandPosition(String addSQL) {
//        try {
//            Statement statement = openConnection();
//
//            ResultSet result = statement.executeQuery("SELECT posX, poxZ, nextDirection FROM islands " + addSQL);
//            IslandPosition position = null;
//            if (result.isBeforeFirst()) {
//                position = new IslandPosition(result.getInt("posX"),
//                        result.getInt("poxZ"),
//                        NextDirection.valueOf(result.getString("nextDirection")));
//            }
//
//            closeConnection(statement);
//            return position;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static List<String> getMembers(String nicknameLeader) {
//        try {
//            Statement statement = openConnection();
//
//            ResultSet result = statement.executeQuery(
//                    String.format(
//                            "SELECT members FROM islands WHERE leader = '%s'",
//                            nicknameLeader));
//            List<String> members = new ArrayList<>();
//
//            if (result.isBeforeFirst()) {
//                String stringMembers = result.getString("members");
//                if (!(stringMembers == null || stringMembers.equals(""))) {
//                    members.addAll(Arrays.asList(stringMembers.split(",")));
//                }
//            }
//
//            closeConnection(statement);
//
//            return members;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
////    public static boolean getPVP(String nicknameLeader) {
////        try {
////            Statement statement = openConnection();
////
////            ResultSet result = statement.executeQuery(
////                    String.format(
////                            "SELECT canPVP FROM islands WHERE `leader` = '%s'",
////                            nicknameLeader));
////            boolean canPVP = false;
////            if (result.isBeforeFirst()) {
////                canPVP = Boolean.parseBoolean(result.getString("canPVP"));
////            }
////
////            closeConnection(statement);
////            return canPVP;
////        } catch (SQLException e) {
////            e.printStackTrace();
////            return false;
////        }
////    }
////
////    public static void changePVP(String nicknameLeader) {
////        try {
////            Statement statement = openConnection();
////
////            ResultSet result = statement.executeQuery(
////                    String.format(
////                            "SELECT canPVP FROM islands WHERE `leader` = '%s'",
////                            nicknameLeader));
////            boolean canPVP = false;
////            if (result.isBeforeFirst()) {
////                 canPVP = !Boolean.parseBoolean(result.getString("canPVP"));
////            }
////
////            statement.executeUpdate(
////                    String.format(
////                            "UPDATE islands SET `canPVP` = '%b' WHERE `leader` = '%s'",
////                            canPVP,
////                            nicknameLeader));
////
////            closeConnection(statement);
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
//
//    public static void deleteIsland(String nicknameLeader) {
//        try {
//            Statement statement = openConnection();
//
//            statement.executeUpdate(
//                    String.format(
//                            "UPDATE islands SET `softDelete` = 1 WHERE `leader` = '%s'",
//                            nicknameLeader));
//
//            closeConnection(statement);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void setMembers(String nicknameLeader, List<String> members) {
//        try {
//            Statement statement = openConnection();
//
//            StringJoiner sjMembers = new StringJoiner(",");
//            for(String member : members) {
//                sjMembers.add(member);
//            }
//
//            statement.executeUpdate(
//                    String.format(
//                            "UPDATE islands SET `members` = '%s' WHERE `leader` = '%s'",
//                            sjMembers,
//                            nicknameLeader));
//
//            closeConnection(statement);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Statement openConnection() throws SQLException {
//        return DriverManager.getConnection(URL_DB).createStatement();
//    }
//
//    private static void closeConnection(Statement statement) throws SQLException {
//        Connection connection = statement.getConnection();
//        statement.close();
//        connection.close();
//    }
//}