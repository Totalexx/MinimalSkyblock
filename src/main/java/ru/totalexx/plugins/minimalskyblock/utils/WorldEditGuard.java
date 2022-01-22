package ru.totalexx.plugins.minimalskyblock.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import ru.totalexx.plugins.minimalskyblock.MinimalSkyblock;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class WorldEditGuard {

    public static boolean createIsland(IslandPosition position, UUID playerID) {
        File schematic = new File(MinimalSkyblock.getFolder()
                + File.separator
                + Config.getString("islands.name_schematic_file")
                + ".schematic");

        if (!schematic.exists()) {
            MinimalSkyblock.getLog().warning(Config.getString("islands.name_schematic_file") +
                    ".schematic not found! The island will not be created.");
            return false;
        }

        createRegion(position, playerID);
        pasteClipboard(schematic, position);
        return true;
    }

    public static void setFlagsGlobalRegion() {
        ProtectedRegion global = getRegionManager().getRegion("__global__");

        if (global == null) {
            global = new GlobalProtectedRegion("__global__");
            getRegionManager().addRegion(global);
        }

        global.setFlag(DefaultFlag.BUILD, StateFlag.State.DENY);
        global.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
    }

    public static void joinPlayerToRegion(int islandID, UUID playerID) {
        getRegionManager().getRegion("is_" + islandID).getMembers().addPlayer(playerID);
    }

    public static boolean getPVP(int islandID) {
        ProtectedRegion region = getRegionManager().getRegion("is_" + islandID);
        return region.getFlag(DefaultFlag.PVP) == StateFlag.State.ALLOW ? true : false;
    }

    public static void changePVP(int islandID) {
        ProtectedRegion region = getRegionManager().getRegion("is_" + islandID);
        StateFlag.State pvpFlag = region.getFlag(DefaultFlag.PVP) == StateFlag.State.ALLOW ?
                StateFlag.State.DENY : StateFlag.State.ALLOW;
        region.setFlag(DefaultFlag.PVP, pvpFlag);
    }

    public static void leaveIsland(int islandID, UUID playerID) {
        ProtectedRegion region = getRegionManager().getRegion("is_" + islandID);
        DefaultDomain members = region.getMembers();
        members.removePlayer(playerID);
        region.setMembers(members);
    }

    private static Clipboard loadClipboard(File file, World world) {
        Clipboard clipboard = null;
        ClipboardFormat format = ClipboardFormat.findByFile(file);

        try {
            ClipboardReader reader = format.getReader(new FileInputStream(file));
            clipboard = reader.read(world.getWorldData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clipboard;
    }

    private static void pasteClipboard(File file, IslandPosition position) {
        World world = new BukkitWorld(Config.getIslandsWorld());
        Clipboard clipboard = loadClipboard(file, world);

        if (clipboard == null) {
            MinimalSkyblock.getLog().warning(Config.getString("Load schematic failed. The island will not be created."));
            return;
        }

        int sizeX = Config.getInt("islands.size_x");
        int sizeZ = Config.getInt("islands.size_z");
        int between = Config.getInt("islands.distance_between");

        int x = position.x == 0 ?
                position.x : (sizeX + between) * position.x;
        int z = position.z == 0 ?
                position.z : (sizeZ + between) * position.z;

        try {
            WorldData data = world.getWorldData();
            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

            Operation operation = new ClipboardHolder(clipboard, data)
                    .createPaste(session, data)
                    .to(BlockVector.toBlockPoint(x, Config.getIslandHeight(), z))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private static void createRegion(IslandPosition position, UUID playerID) {
        int islandSizeX = Config.getInt("islands.size_x");
        int islandSizeZ = Config.getInt("islands.size_z");
        int islandBetween = Config.getInt("islands.distance_between");

        int x1 = (islandSizeX + islandBetween) * position.x + islandSizeX / 2;
        int z1 = (islandSizeZ + islandBetween) * position.z + islandSizeZ / 2;
        int x2 = (islandSizeX + islandBetween) * position.x - islandSizeX / 2;
        int z2 = (islandSizeZ + islandBetween) * position.z - islandSizeZ / 2;

        BlockVector min = new BlockVector(x1, 0, z1);
        BlockVector max = new BlockVector(x2, 255, z2);

        ProtectedRegion region = new ProtectedCuboidRegion("is_" + position.id, min, max);
        region.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
        region.getMembers().addPlayer(playerID);

        getRegionManager().addRegion(region);
    }

    private static RegionManager getRegionManager() {
        return getWorldGuard().getRegionContainer().get(Config.getIslandsWorld());
    }

    private static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }
}
