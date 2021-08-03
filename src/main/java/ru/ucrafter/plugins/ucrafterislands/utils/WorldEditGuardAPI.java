package ru.ucrafter.plugins.ucrafterislands.utils;

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
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import ru.ucrafter.plugins.ucrafterislands.UCrafterIslands;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.bukkit.Bukkit.getServer;

public class WorldEditGuardAPI {

    public static void createIsland(String nicknameLeader, IslandPosition position) {
        File schematic = new File(UCrafterIslands.getFolder()
                + File.separator
                + Config.getString("islands.name_schematic_file")
                + ".schematic");
        if (!schematic.exists()) {
            UCrafterIslands.getInstance().saveResource("/default.schematic", false);
        }
        World islandsWorld = new BukkitWorld(Config.getIslandsWorld());
        pasteClipboard(schematic, islandsWorld, position);

        int islandSizeX = Config.getInt("islands.size_x");
        int islandSizeZ = Config.getInt("islands.size_z");
        int islandBetween = Config.getInt("islands.distance_between");
        int x1 = (islandSizeX + islandBetween) * position.x + islandSizeX / 2;
        int z1 = (islandSizeZ + islandBetween) * position.z + islandSizeZ / 2;
        int x2 = (islandSizeX + islandBetween) * position.x - islandSizeX / 2;
        int z2 = (islandSizeZ + islandBetween) * position.z - islandSizeZ / 2;
        createRegion(nicknameLeader, x1, z1, x2, z2);
        denyPutBlockGlobalRegion();
    }

    public static void denyPutBlockGlobalRegion() {
        RegionContainer container = getWorldGuard().getRegionContainer();
        RegionManager regions = container.get(Config.getIslandsWorld());
        regions.getRegion("__global__").setFlag(DefaultFlag.BUILD, StateFlag.State.DENY);
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


    private static void pasteClipboard(File file, World world, IslandPosition position) {
        Clipboard clipboard = loadClipboard(file, world);
        WorldData data = world.getWorldData();
        int x = position.x == 0 ?
                position.x : Config.getInt("islands.size_x") * position.x + Config.getInt("islands.distance_between");
        int z = position.z == 0 ?
                position.z : Config.getInt("islands.size_z") * position.z + Config.getInt("islands.distance_between");
        try {
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

    private static void createRegion(String nicknameLeader, int posX1, int posZ1, int posX2, int posZ2) {
        BlockVector min = new BlockVector(posX1, 0, posZ1);
        BlockVector max = new BlockVector(posX2, 255, posZ2);
        ProtectedRegion region = new ProtectedCuboidRegion("is_" + nicknameLeader, min, max);
        DefaultDomain member = region.getMembers();
        member.addPlayer(nicknameLeader);

        RegionContainer container = getWorldGuard().getRegionContainer();
        RegionManager regions = container.get(Config.getIslandsWorld());
        regions.addRegion(region);
    }

    private static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

}
