package ru.totalexx.plugins.minimalskyblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.totalexx.plugins.minimalskyblock.utils.*;
import ru.totalexx.plugins.minimalskyblock.utils.IslandPosition.NextDirection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IslandEvents {

    private static HashSet<IslandEvent> events = new HashSet<>();

    public static void createIsland(Player creator) {
        UUID playerID = creator.getUniqueId();
        IslandDB islandDB = new IslandDB();
        IslandPosition newIsland = islandDB.getPositionLastIsland();
        islandDB.closeConnection();

        if (newIsland != null) {
            newIsland.id++;

            switch(newIsland.nextDirection) {
                case RIGHT:
                    newIsland.z++;
                    break;
                case DOWN:
                    newIsland.x--;
                    break;
                case LEFT:
                    newIsland.z--;
                    break;
                case TOP:
                    newIsland.x++;
                    break;
            }

            if (newIsland.z == newIsland.x
                    || newIsland.z == Math.abs(newIsland.x)
                    || newIsland.z + newIsland.x == 1
                    && newIsland.z < newIsland.x) {
                newIsland.nextDirection = newIsland.nextDirection.rotateDirection();
            }
        } else {
            newIsland = new IslandPosition(1,0, 0, NextDirection.TOP);
        }

        boolean islandCreated = WorldEditGuard.createIsland(newIsland, playerID);
        if (islandCreated) {
            islandDB.openConnection();
            islandDB.addIsland(newIsland.id, newIsland.x, newIsland.z, newIsland.nextDirection.toString());
            islandDB.joinToIsland(playerID, newIsland.id);
            islandDB.closeConnection();
            creator.sendMessage(Config.getMessage("is.create_successfully"));
        } else {
            creator.sendMessage(Config.getMessage("is.create_failed"));
        }
    }

    public static void leaveIsland(Player player) {
        UUID playerID = player.getUniqueId();

        IslandDB islandDB = new IslandDB();
        int islandID = islandDB.getIslandID(playerID);
        islandDB.joinToIsland(playerID, 0);
        islandDB.closeConnection();

        WorldEditGuard.leaveIsland(islandID, playerID);

        player.getInventory().clear();
        player.getEnderChest().clear();
        player.setHealth(0);

        player.sendMessage(Config.getMessage("is.leave"));
    }

    public static void teleportToMyIsland(Player player) {
        IslandDB islandDB = new IslandDB();
        player.teleport(islandDB.getSpawnPosition(player.getUniqueId()));
        islandDB.closeConnection();

        player.sendMessage(Config.getMessage("is.home"));
    }

    public static void setIslandSpawn(int islandID, Location location) {
        IslandDB islandDB = new IslandDB();
        islandDB.setSpawnPosition(islandID, location);
        islandDB.closeConnection();
    }

    public static void inviteIsland(Player inviterPlayer, String invited) {
        Player invitedPlayer = Bukkit.getPlayerExact(invited);

        if (invitedPlayer == null) {
            inviterPlayer.sendMessage(Config.getMessage("is.player_not_found", invited));
            return;
        }

        UUID inviterID = inviterPlayer.getUniqueId();
        UUID invitedID = invitedPlayer.getUniqueId();

        if (invitedID.equals(inviterID)) {
            inviterPlayer.sendMessage(Config.getMessage("is.event.himself"));
            return;
        }

        IslandEvent event = new IslandEvent(IslandEvent.Event.INVITE, inviterID, invitedID);

        if (events.contains(event)) {
            inviterPlayer.sendMessage(Config.getMessage("is.invite.already_invited"));
            return;
        }

        IslandDB islandDB = new IslandDB();
        boolean hasIslandInviter = islandDB.hasIsland(inviterPlayer.getUniqueId());
        boolean hasIslandInvited = islandDB.hasIsland(invitedPlayer.getUniqueId());
        Set<UUID> players = islandDB.getPlayers(islandDB.getIslandID(inviterID));
        boolean containsPlayer = players.contains(invitedID);
        int countPlayers = players.size();
        islandDB.closeConnection();

        if (!hasIslandInviter) {
            inviterPlayer.sendMessage(Config.getMessage("is.not_found"));
            return;
        }

        if (hasIslandInvited) {
            inviterPlayer.sendMessage(Config.getMessage("is.invite.inviter_has_island"));
            return;
        }

        if (containsPlayer) {
            inviterPlayer.sendMessage(Config.getMessage("is.invite.already_added", invited));
            return;
        }

        if (countPlayers > Config.getMaxPlayersOnIsland()) {
            inviterPlayer.sendMessage(Config.getMessage("is.invite.too_many_players"));
            return;
        }

        events.add(event);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
               events.remove(event);
               String message = Config.getMessage("is.event.cancel");
               invitedPlayer.sendMessage(message);
               inviterPlayer.sendMessage(message);
            }
        };
        runnable.runTaskLaterAsynchronously(MinimalSkyblock.getInstance(), 2400);

        inviterPlayer.sendMessage(Config.getMessage("is.invite.send_to_inviter", invited));
        invitedPlayer.sendMessage(
                Config.getMessage("is.invite.send_to_invited", inviterPlayer.getName(), inviterPlayer.getName()));
    }

    public static void inviteAccept(Player invitedPlayer, String inviter) {
        Player inviterPlayer = Bukkit.getPlayerExact(inviter);

        if (inviterPlayer == null) {
            invitedPlayer.sendMessage(Config.getMessage("is.invite.not_found"));
            return;
        }

        IslandDB islandDB = new IslandDB();
        boolean hasIslandInvited = islandDB.hasIsland(invitedPlayer.getUniqueId());
        boolean hasIslandInviter = islandDB.hasIsland(inviterPlayer.getUniqueId());
        islandDB.closeConnection();

        if (hasIslandInvited) {
            invitedPlayer.sendMessage(Config.getMessage("is.invite.already_have_island"));
            return;
        }

        if (!hasIslandInviter) {
            invitedPlayer.sendMessage(Config.getMessage("is.invite.not_found"));
            return;
        }

        UUID inviterID = inviterPlayer.getUniqueId();
        UUID invitedID = invitedPlayer.getUniqueId();

        IslandEvent event = new IslandEvent(IslandEvent.Event.INVITE, inviterID, invitedID);

        if (events.contains(event)) {
            inviterPlayer.sendMessage(Config.getMessage("is.invite.successfully_inviter", invitedPlayer.getName()));
            invitedPlayer.sendMessage(Config.getMessage("is.invite.successfully_invited"));

            islandDB.openConnection();
            int islandID = islandDB.getIslandID(inviterID);
            islandDB.joinToIsland(invitedID, islandID);
            islandDB.closeConnection();

            WorldEditGuard.joinPlayerToRegion(islandID, invitedID);
            return;
        }

        invitedPlayer.sendMessage(Config.getMessage("is.invite.not_found"));
    }

    public static void visitIsland(Player visitorPlayer, String visited) {
        Player visitedPlayer = Bukkit.getPlayerExact(visited);

        if (visitedPlayer == null) {
            visitorPlayer.sendMessage(Config.getMessage("is.player_not_found", visited));
            return;
        }

        UUID visitedID = visitedPlayer.getUniqueId();
        UUID visitorID = visitorPlayer.getUniqueId();

        if (visitedID.equals(visitorID)) {
            visitorPlayer.sendMessage(Config.getMessage("is.event.himself"));
            return;
        }

        IslandEvent event = new IslandEvent(IslandEvent.Event.TP, visitorID, visitedID);

        if (events.contains(event)) {
            visitorPlayer.sendMessage(Config.getMessage("is.invite.already_invited"));
            return;
        }

        IslandDB islandDB = new IslandDB();
        int islandID = islandDB.getIslandID(visitedID);
        islandDB.closeConnection();

        if (islandID == 0) {
            visitorPlayer.sendMessage(Config.getMessage("is.event.no_island"));
            return;
        }

        islandDB.openConnection();
        IslandDB.VisitStatus visitStatus = islandDB.getVisitStatus(islandID);
        Location islandLocation = islandDB.getSpawnPosition(visitedID);
        islandDB.closeConnection();

        switch (visitStatus) {
            case BY_INVITATION:
                events.add(event);
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        events.remove(event);
                        String message = Config.getMessage("is.invite.cancel");
                        visitedPlayer.sendMessage(message);
                        visitorPlayer.sendMessage(message);
                    }
                };
                runnable.runTaskLaterAsynchronously(MinimalSkyblock.getInstance(), 2400);
                break;
            case NOBODY:
                visitorPlayer.sendMessage(Config.getMessage("is.visit.not_allowed"));
                break;
            case ANYBODY:
                visitorPlayer.teleport(islandLocation);
                visitorPlayer.sendMessage(Config.getMessage("is.visit.successfully"));
                break;
        }
    }

    public static void visitAllow(Player visitedPlayer, String visitor) {
        Player visitorPlayer = Bukkit.getPlayerExact(visitor);

        if (visitorPlayer == null) {
            visitedPlayer.sendMessage(Config.getMessage("is.visit.not_found"));
            return;
        }

        IslandDB islandDB = new IslandDB();
        boolean hasIsland = islandDB.hasIsland(visitedPlayer.getUniqueId());
        islandDB.closeConnection();

        if (!hasIsland) {
            visitedPlayer.sendMessage(Config.getMessage("is.visit.not_found"));
        }

        UUID visitorID = visitorPlayer.getUniqueId();
        UUID visitedID = visitedPlayer.getUniqueId();

        IslandEvent event = new IslandEvent(IslandEvent.Event.TP, visitorID, visitedID);

        if (events.contains(event)) {
            visitorPlayer.sendMessage(Config.getMessage("is.visit.successfully_visitor", visitedPlayer.getName()));
            visitedPlayer.sendMessage(Config.getMessage("is.visit.successfully_visited"));

            islandDB.openConnection();
            Location location = islandDB.getSpawnPosition(visitedID);
            islandDB.closeConnection();

            visitorPlayer.teleport(location);

            return;
        }

        visitedPlayer.sendMessage(Config.getMessage("is.invite.not_found"));
    }
}