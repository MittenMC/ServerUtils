package com.github.mittenmc.serverutils.gui.filesystem.tree;

import com.github.mittenmc.serverutils.Colors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A generic node class which represents a folder and file in a file system.
 * @author GavvyDizzle
 * @version 1.0.10
 * @since 1.0.10
 */
@SuppressWarnings("unused")
public class ItemFileNode extends Node {

    @Getter @Setter private Material folderMaterial, fileMaterial;
    private final boolean isFolder;
    private ItemStack itemStack;

    public ItemFileNode(@Nullable Node parent, String name, boolean isFolder) {
        super(parent, name);
        this.isFolder = isFolder;
        this.folderMaterial = Material.CHEST;
        this.fileMaterial = Material.PAPER;
    }

    public ItemFileNode(@Nullable Node parent, String name, boolean isFolder, Material folderMaterial, Material fileMaterial) {
        super(parent, name);
        this.isFolder = isFolder;
        this.folderMaterial = folderMaterial;
        this.fileMaterial = fileMaterial;
    }

    @Override
    public int getSortWeight() {
        return isFolder ? 0 : 1;
    }

    /**
     * Generates the display item for this node.
     * @return The item to display in menus.
     */
    public ItemStack generateDisplayItem() {
        ItemStack itemStack = new ItemStack(isFolder() ? folderMaterial : fileMaterial);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        meta.setDisplayName(Colors.conv("&e" + getName()));
        if (isLeaf())  {
            meta.setLore(Colors.conv(List.of("&7Empty")));
        }
        else if (isFolder()) {
            List<Node> children = getChildren();
            int folders = (int) children.stream().filter(node -> ((ItemFileNode) node).isFolder()).count();

            meta.setLore(Colors.conv(List.of(
                    "&7" + folders + " Folders",
                    "&7" + (getChildren().size() - folders) + " Files"
            )));
        } else {
            meta.setLore(Colors.conv(List.of("&7" + getChildren().size() + " Items")));
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack getMenuItem(Player player) {
        if (itemStack == null) itemStack = generateDisplayItem();
        return itemStack;
    }

    @Override
    public @Nullable ItemStack getPlayerItem(Player player) {
        return null;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public boolean isFile() {
        return !isFolder;
    }
}