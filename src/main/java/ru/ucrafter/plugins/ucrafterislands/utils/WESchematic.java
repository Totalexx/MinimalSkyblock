package ru.ucrafter.plugins.ucrafterislands.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import ru.ucrafter.plugins.ucrafterislands.UCrafterIslands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WESchematic {

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

    public static void pasteClipboard(File file, World world, IslandPosition position) {
        Clipboard clipboard = loadClipboard(file, world);
        WorldData data = world.getWorldData();
        try {
            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
            Operation operation = new ClipboardHolder(clipboard, data)
                    .createPaste(session, data)
                    .to(BlockVector.toBlockPoint(position.x, Integer.parseInt(UCrafterIslands.getFromConfiguration("islands.height")), position.z))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }
}
