package com.github.mittenmc.serverutils.gui.filesystem.tree;

import com.github.mittenmc.serverutils.gui.pages.ItemGenerator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generic node class which must be an {@link ItemGenerator}.
 * This node does not accept children.
 * @author GavvyDizzle
 * @version 1.0.10
 * @since 1.0.10
 */
@SuppressWarnings("unused")
public class ItemNode<E extends ItemGenerator> extends Node {

    private final E item;

    public ItemNode(@Nullable Node parent, String name, @NotNull E item) {
        super(parent, name);
        this.item = item;
    }

    @Override
    public int getSortWeight() {
        return 10;
    }

    @Override
    public void add(Node treeNode) {
        throw new UnsupportedOperationException("Adding children to an ItemNode is not permitted.");
    }

    @Override
    public @NotNull ItemStack getMenuItem(Player player) {
        return item.getMenuItem(player);
    }

    @Override
    public @Nullable ItemStack getPlayerItem(Player player) {
        return item.getPlayerItem(player);
    }
}