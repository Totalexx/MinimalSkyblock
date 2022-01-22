package ru.totalexx.plugins.minimalskyblock.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class InventoryGUI implements InventoryHolder {

    protected Inventory inventory;

    abstract void onInventoryClick(InventoryClickEvent e);

    protected ItemStack createItem(Material material, int subID, String name, List<String> description) {
        ItemStack itemStack = new ItemStack(material, 1, (short) subID);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(description);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}
