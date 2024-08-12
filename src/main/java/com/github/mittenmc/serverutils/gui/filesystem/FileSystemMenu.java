package com.github.mittenmc.serverutils.gui.filesystem;

import com.github.mittenmc.serverutils.ColoredItems;
import com.github.mittenmc.serverutils.gui.filesystem.tree.Node;
import com.github.mittenmc.serverutils.gui.pages.ClickableItem;
import com.github.mittenmc.serverutils.gui.pages.PagesMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class FileSystemMenu extends PagesMenu<Node> {

    private Node curr;

    public FileSystemMenu(String inventoryName, Node root) {
        super(new PagesMenuBuilder<>(inventoryName, 54));
        this.curr = root;

        addClickableItem(0, new ClickableItem<>(ColoredItems.RED.getGlass("&c<< Back")) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                openParent();
            }
        });

        update(curr);
    }

    /**
     * Handles when the item set had reloaded.
     * Attempts to open the player to the same location.
     * If the matching location does not exist, then they will be placed in the root directory.
     * @param newRoot The new root node
     */
    public void refresh(Node newRoot) {
        Node node = newRoot.getRoot().find(curr);
        update(Objects.requireNonNullElse(node, newRoot));
    }

    /**
     * Updates the menu contents to display this node.
     * @param node The node
     */
    private void update(Node node) {
        curr = node;
        setItems(curr.getChildren());
    }

    /**
     * Opens the parent menu if one exists.
     */
    private void openParent() {
        if (curr.getParent() != null) {
            update(curr.getParent());
        }
    }

    @Override
    public void onItemClick(InventoryClickEvent inventoryClickEvent, Player player, @NotNull Node node) {
        if (node.isLeaf()) {
            ItemStack itemStack = node.getPlayerItem(player);
            if (itemStack != null) {
                ItemStack playerItem = node.getPlayerItem(player);
                if (playerItem != null) {
                    player.getInventory().addItem(playerItem);
                }
            }
        }
        else {
            update(node);
        }
    }
}