package com.github.mittenmc.serverutils.gui;

import com.github.mittenmc.serverutils.ColoredItems;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.ItemStackUtils;
import com.github.mittenmc.serverutils.Numbers;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

/**
 * An inventory implementation which displays contents across pages.
 * The default menu formatting can be changed through setter methods.
 * @author GavvyDizzle
 * @version 1.0.9
 * @since 1.0.8
 */
@SuppressWarnings("unused")
public abstract class PagesMenu<E extends Comparable<E>> implements ClickableMenu {

    private String inventoryName;
    private final int inventorySize;
    @Setter private List<Integer> slots;

    private int pageDownSlot, pageInfoSlot, pageUpSlot;
    @Setter private ItemStack pageInfoItem, previousPageItem, nextPageItem, pageRowFiller;

    private final HashMap<UUID, Integer> playerPages;
    private final List<E> itemList;
    private final Comparator<E> comparator;

    private int backButtonSlot;
    private ItemStack backButton;

    public PagesMenu(String inventoryName) {
        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        comparator = null;

        inventorySize = 54;
        slots = IntStream.rangeClosed(0, 44).boxed().toList();

        this.inventoryName = Colors.conv(inventoryName);
        loadDefaultItems();
    }

    public PagesMenu(String inventoryName, Comparator<E> comparator) {
        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        this.comparator = comparator;

        inventorySize = 54;
        slots = IntStream.rangeClosed(0, 44).boxed().toList();

        this.inventoryName = Colors.conv(inventoryName);
        loadDefaultItems();
    }

    public PagesMenu(String inventoryName, List<Integer> slots) {
        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        comparator = null;

        inventorySize = 54;
        this.slots = slots;

        this.inventoryName = Colors.conv(inventoryName);
        loadDefaultItems();
    }

    public PagesMenu(String inventoryName, List<Integer> slots, Comparator<E> comparator) {
        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        this.comparator = comparator;

        inventorySize = 54;
        this.slots = slots;

        this.inventoryName = Colors.conv(inventoryName);
        loadDefaultItems();
    }

    private void loadDefaultItems() {
        pageDownSlot = 48;
        pageInfoSlot = 49;
        pageUpSlot = 50;

        pageInfoItem = new ItemStack(Material.PAPER);
        ItemMeta meta = pageInfoItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&ePage {page}/{max_page}"));
        pageInfoItem.setItemMeta(meta);

        previousPageItem = new ItemStack(Material.PAPER);
        meta = previousPageItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&ePrevious Page"));
        previousPageItem.setItemMeta(meta);

        nextPageItem = new ItemStack(Material.PAPER);
        meta = nextPageItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eNext Page"));
        nextPageItem.setItemMeta(meta);

        pageRowFiller = ColoredItems.WHITE.getGlass();
    }

    /**
     * Overrides the items stored in this menu and sorts them.
     * @param collection The collection of items
     */
    public void setItems(Collection<E> collection) {
        int oldMax = getMaxPage();
        itemList.clear();
        itemList.addAll(collection);
        sortItems();

        updateOpenInventories(oldMax);
    }

    /**
     * Adds a new item to this menu and sorts the list.
     * @param item The item to add
     */
    public void addItem(E item) {
        int oldMax = getMaxPage();
        itemList.add(item);
        sortItems();

        updateOpenInventories(oldMax);
    }

    /**
     * Adds news item to this menu and sorts the list.
     * @param items The items to add
     */
    public void addItems(Collection<E> items) {
        int oldMax = getMaxPage();
        itemList.addAll(items);
        sortItems();

        updateOpenInventories(oldMax);
    }

    /**
     * Removes an item from this menu
     * @param item The item to remove
     */
    public void removeItem(E item) {
        int oldMax = getMaxPage();
        itemList.remove(item);

        updateOpenInventories(oldMax);
    }

    /**
     * Since modifying the list will cause a de-sync between the open inventories and the in-memory list
     * all open inventories belonging to this menu should have their page updated.
     * If they are on the max page but the number of pages decreased, move them down a page.
     * @param oldMax The max page before any updates
     */
    private void updateOpenInventories(int oldMax) {
        int newMax = getMaxPage();

        for (Map.Entry<UUID, Integer> entry : playerPages.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;

            int newPage = entry.getValue();
            if (oldMax > newMax && newPage == oldMax) newPage = newMax;

            playerPages.put(entry.getKey(), Math.max(1, newPage));
            updatePage(player);
        }
    }

    /**
     *
     */
    private void sortItems() {
        if (comparator != null) {
            itemList.sort(comparator);
        }
        else {
            Collections.sort(itemList);
        }
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, inventorySize, inventoryName);

        for (int i = inventorySize-9; i < inventorySize; i++) {
            inventory.setItem(i, pageRowFiller);
        }

        if (backButton != null) inventory.setItem(backButtonSlot, backButton);

        player.openInventory(inventory);
        playerPages.put(player.getUniqueId(), 1);
        updatePage(player);
    }

    @Override
    public void closeInventory(Player player) {
        playerPages.remove(player.getUniqueId());
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        if (backButton != null && e.getSlot() == backButtonSlot) {
            onBackButtonClick(player);
        }
        else if (e.getSlot() == pageUpSlot) {
            if (playerPages.get(player.getUniqueId()) < getMaxPage()) {
                onPageUp(player);
            }
        }
        else if (e.getSlot() == pageDownSlot) {
            if (playerPages.get(player.getUniqueId()) > 1) {
                onPageDown(player);
            }
        }
        else {
            E item = getItemBySlot(playerPages.get(player.getUniqueId()), e.getSlot());
            if (item != null) onItemClick(e, player, item);
        }
    }

    /**
     * Handles what happens when the player clicks the back button.
     * By default, it will close this inventory.
     * @param player The player
     */
    public void onBackButtonClick(Player player) {
        player.closeInventory();
    }

    /**
     * Moves the player up by one page.
     * If this method is overwritten, you must call super()!
     * @param player The player
     */
    public void onPageUp(Player player) {
        playerPages.put(player.getUniqueId(), playerPages.get(player.getUniqueId()) + 1);
        updatePage(player);
    }

    /**
     * Moves the player down by one page.
     * If this method is overwritten, you must call super()!
     * @param player The player
     */
    public void onPageDown(Player player) {
        playerPages.put(player.getUniqueId(), playerPages.get(player.getUniqueId()) - 1);
        updatePage(player);
    }

    /**
     * Handles what happens when the player clicks a custom item.
     * @param e The original click event
     * @param player The player
     * @param item The clicked item
     */
    public abstract void onItemClick(InventoryClickEvent e, Player player, E item);

    /**
     * Sets the contents of this page and updates the page info item
     * @param player The player's inventory to update
     */
    private void updatePage(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        int page = playerPages.get(player.getUniqueId());

        for (int slot : slots) {
            inventory.setItem(slot, onItemClear(slot));
        }

        for (int i = 0; i < getNumItemsOnPage(page); i++) {
            int slot = slots.get(i);

            E item = getItemBySlot(page, slot);
            if (item != null) inventory.setItem(slot, onItemAdd(item, player));
        }

        inventory.setItem(pageDownSlot, createPreviousPageItem(page));
        inventory.setItem(pageInfoSlot, createPageItem(page));
        inventory.setItem(pageUpSlot, createNextPageItem(page));
    }

    /**
     * Called when an item is removed from the inventory.
     * By default, a null value is returned.
     * @param slot The item slot
     * @return The resulting ItemStack
     */
    @Nullable
    public ItemStack onItemClear(int slot) {
        return null;
    }

    /**
     * Called when an item is added to the inventory.
     * Implementations must define how to obtain an ItemStack from {@link E}
     * @param item The generic item belonging to this inventory
     * @param player The player who will view this item
     * @return The resulting ItemStack
     */
    public abstract ItemStack onItemAdd(E item, Player player);

    /**
     * Called when the previous page item is inserted
     * @param page The page
     * @return The item to add to the menu
     */
    public ItemStack createPreviousPageItem(int page) {
        return previousPageItem;
    }

    /**
     * Called when the next page item is inserted
     * @param page The page
     * @return The item to add to the menu
     */
    public ItemStack createNextPageItem(int page) {
        return nextPageItem;
    }

    /**
     * Called when the page info item is inserted
     * @param page The page
     * @return The item to add to the menu
     */
    public ItemStack createPageItem(int page) {
        ItemStack pageInfo = pageInfoItem.clone();

        Map<String, String> map = new HashMap<>();
        map.put("{page}", String.valueOf(page));
        map.put("{max_page}", String.valueOf(getMaxPage()));

        ItemStackUtils.replacePlaceholders(pageInfo, map);
        return pageInfo;
    }

    /**
     * Calculates the last page by the amount of items in the list.
     * @return The max page
     */
    public int getMaxPage() {
        return (itemList.size() - 1) / slots.size() + 1;
    }

    /**
     * Determines the number of items on this page
     * @param page The page
     * @return The number of items filling this page
     */
    private int getNumItemsOnPage(int page) {
        return Math.min(slots.size(), itemList.size() - (page - 1) * slots.size());
    }

    private E getItemBySlot(int page, int slot) {
        if (!slots.contains(slot)) return null;

        int index = (page - 1) * slots.size() + slots.indexOf(slot);
        if (!Numbers.isWithinRange(index, 0, itemList.size()-1)) return null;

        return itemList.get(index);
    }

    //***** GETTERS & SETTERS *****//

    /**
     * @return A clone of the previous page item
     */
    public ItemStack getPreviousPageItem() {
        return previousPageItem.clone();
    }

    /**
     * @return A clone of the next page item
     */
    public ItemStack getNextPageItem() {
        return nextPageItem.clone();
    }

    /**
     * @return A clone of the page info item
     */
    public ItemStack getPageInfoItem() {
        return pageInfoItem.clone();
    }

    public void setPageDownSlot(int pageDownSlot) {
        if (Numbers.isWithinRange(pageDownSlot, slots.size()-1, inventorySize-1)) {
            this.pageDownSlot = pageDownSlot;
        }
    }

    public void setPageUpSlot(int pageUpSlot) {
        if (Numbers.isWithinRange(pageDownSlot, slots.size()-1, inventorySize-1)) {
            this.pageUpSlot = pageUpSlot;
        }
    }

    public void setPageInfoSlot(int pageInfoSlot) {
        if (Numbers.isWithinRange(pageDownSlot, slots.size()-1, inventorySize-1)) {
            this.pageInfoSlot = pageInfoSlot;
        }
    }

    /**
     * Tells the inventory how to display the back button.
     * @param slot The slot
     * @param itemStack The item to display
     */
    public void setBackButton(int slot, ItemStack itemStack) {
        backButtonSlot = Numbers.constrain(slot, 45, 53);
        backButton = itemStack;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = Colors.conv(inventoryName);
    }
}
