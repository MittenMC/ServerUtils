package com.github.mittenmc.serverutils.gui.pages;

import com.github.mittenmc.serverutils.ColoredItems;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.ItemStackUtils;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.gui.ClickableMenu;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

/**
 * An inventory implementation which displays contents across pages.
 * @author GavvyDizzle
 * @version 1.0.15
 * @since 1.0.8
 */
@SuppressWarnings("unused")
public class PagesMenu<E extends Comparable<? super E> & ItemGenerator> implements ClickableMenu {

    @Setter private String inventoryName;
    private final int inventorySize;
    @Nullable private Comparator<E> comparator;
    private List<Integer> slots;

    private final HashMap<UUID, Integer> playerPages;
    private final List<E> itemList;
    private final Map<Integer, ClickableItem<?>> extraItemsMap;

    @Setter private ItemStack filler, pageRowFiller;
    @Setter private boolean replaceEmptySlotsWithAir = false;

    public static class PagesMenuBuilder<E extends Comparable<? super E> & ItemGenerator> {
        //required
        private final String inventoryName;
        private final int inventorySize;

        //optional
        private Comparator<E> comparator = null;
        private List<Integer> slots = null;
        private ItemStack filler = null;
        private ItemStack pageRowFiller = null;
        private boolean replaceEmptySlotsWithAir = false;

        public PagesMenuBuilder(String inventoryName, int rows) {
            this.inventoryName = inventoryName;
            this.inventorySize = Numbers.constrain(rows, 2, 6) * 9;
        }

        public PagesMenuBuilder<E> comparator(Comparator<E> comparator) {
            this.comparator = comparator;
            return this;
        }

        public PagesMenuBuilder<E> slots(List<Integer> slots) {
            this.slots = slots;
            return this;
        }

        public PagesMenuBuilder<E> filler(ItemStack filler) {
            this.filler = filler;
            return this;
        }

        public PagesMenuBuilder<E> pageRowFiller(ItemStack pageRowFiller) {
            this.pageRowFiller = pageRowFiller;
            return this;
        }

        public PagesMenuBuilder<E> replaceEmptySlotsWithAir(boolean b) {
            this.replaceEmptySlotsWithAir = b;
            return this;
        }

        public PagesMenu<E> build() {
            return new PagesMenu<>(this);
        }
    }

    /**
     * Creates a new PagesMenu by using its builder.
     * Only subclasses should call this constructor.
     * Creating a menu on the fly should use {@link PagesMenuBuilder#build()}.
     * @param builder The builder
     */
    public PagesMenu(PagesMenuBuilder<E> builder) {
        this.inventoryName = Colors.conv(builder.inventoryName);
        this.inventorySize = builder.inventorySize;
        this.comparator = builder.comparator;
        this.filler = builder.filler;
        this.pageRowFiller = builder.pageRowFiller;
        this.replaceEmptySlotsWithAir = builder.replaceEmptySlotsWithAir;

        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        extraItemsMap = new HashMap<>();

        // Must set slots after initializing extraItemsMap
        setSlots(builder.slots != null ? builder.slots : IntStream.range(0, inventorySize-9).boxed().toList());

        loadDefaultItems();
    }

    /**
     * Creates a new pages menu
     * @deprecated Use {@link PagesMenuBuilder} instead of constructors
     */
    @Deprecated
    public PagesMenu(String inventoryName) {
        this.inventoryName = Colors.conv(inventoryName);
        this.inventorySize = 54;

        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        extraItemsMap = new HashMap<>();
        comparator = null;
        setSlots(IntStream.rangeClosed(0, 44).boxed().toList());

        loadDefaultItems();
    }

    /**
     * Creates a new pages menu
     * @deprecated Use {@link PagesMenuBuilder} instead of constructors
     */
    @Deprecated
    public PagesMenu(String inventoryName, @Nullable Comparator<E> comparator) {
        this.inventoryName = Colors.conv(inventoryName);
        this.inventorySize = 54;

        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        extraItemsMap = new HashMap<>();
        this.comparator = comparator;
        setSlots(IntStream.rangeClosed(0, 44).boxed().toList());

        loadDefaultItems();
    }

    /**
     * Creates a new pages menu
     * @deprecated Use {@link PagesMenuBuilder} instead of constructors
     */
    @Deprecated
    public PagesMenu(String inventoryName, List<Integer> slots) {
        this.inventoryName = Colors.conv(inventoryName);
        this.inventorySize = 54;

        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        extraItemsMap = new HashMap<>();
        comparator = null;
        setSlots(slots);

        loadDefaultItems();
    }

    /**
     * Creates a new pages menu
     * @deprecated Use {@link PagesMenuBuilder} instead of constructors
     */
    @Deprecated
    public PagesMenu(String inventoryName, List<Integer> slots, @Nullable Comparator<E> comparator) {
        this.inventoryName = Colors.conv(inventoryName);
        this.inventorySize = 54;

        playerPages = new HashMap<>();
        itemList = new ArrayList<>();
        extraItemsMap = new HashMap<>();
        this.comparator = comparator;
        setSlots(slots);

        loadDefaultItems();
    }

    private void loadDefaultItems() {
        int pageDownSlot = inventorySize-6;
        int pageInfoSlot = inventorySize-5;
        int pageUpSlot = inventorySize-4;

        ItemStack pageInfoItem = new ItemStack(Material.PAPER);
        ItemMeta meta = pageInfoItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&ePage {page}/{max_page}"));
        pageInfoItem.setItemMeta(meta);
        addClickableItem(pageInfoSlot, new DisplayItem<>(pageInfoItem) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                return createPageItem(pageInfoItem, playerPages.get(player.getUniqueId()));
            }
        });

        ItemStack previousPageItem = new ItemStack(Material.PAPER);
        meta = previousPageItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&ePrevious Page"));
        previousPageItem.setItemMeta(meta);
        addClickableItem(pageDownSlot, new ClickableItem<>(previousPageItem) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if (playerPages.get(player.getUniqueId()) > 1) {
                    onPageDown(player);
                }
            }
        });

        ItemStack nextPageItem = new ItemStack(Material.PAPER);
        meta = nextPageItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eNext Page"));
        nextPageItem.setItemMeta(meta);
        addClickableItem(pageUpSlot, new ClickableItem<>(nextPageItem) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if (playerPages.get(player.getUniqueId()) < getMaxPage()) {
                    onPageUp(player);
                }
            }
        });

        pageRowFiller = ColoredItems.WHITE.getGlass();
    }

    /**
     * Updates the slots that items will fill.
     * The order of the provided list is preserved.
     * @param newSlots The new list of slots
     */
    public void setSlots(List<Integer> newSlots) {
        slots = new ArrayList<>(newSlots.stream().filter(
                i -> Numbers.isWithinRange(i, 0, inventorySize-1) &&
                        !extraItemsMap.containsKey(i)
        ).toList());
    }

    private void sortItems() {
        if (comparator != null) {
            itemList.sort(comparator);
        } else {
            Collections.sort(itemList);
        }
    }

    /**
     * Set the sorting method for the item list.
     * A null comparator will use {@link E}'s compareTo method
     * @param comparator The new Comparator or null
     */
    public void updateSortingMethod(@Nullable Comparator<E> comparator) {
        updateSortingMethod(comparator, true);
    }

    /**
     * Set the sorting method for the item list.
     * A null comparator will use {@link E}'s compareTo method
     * @param comparator The new Comparator or null
     * @param setToFirstPage If all viewers should have their page set to 1
     */
    public void updateSortingMethod(@Nullable Comparator<E> comparator, boolean setToFirstPage) {
        this.comparator = comparator;
        sortItems();

        int oldMax = getMaxPage();
        updateOpenInventories(oldMax);

        for (Map.Entry<UUID, Integer> entry : playerPages.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;

            if (setToFirstPage) {
                updatePage(player, 1);
            } else {
                updatePage(player);
            }
        }
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
     * Adds a new item to this menu which defines custom behavior when it is clicked.
     * @param slot The slot
     * @param item The item to add
     */
    public void addClickableItem(int slot, ClickableItem<?> item) {
        int oldMax = getMaxPage();

        if (!Numbers.isWithinRange(slot, 0, inventorySize-1)) {
            throw new UnsupportedOperationException("Provided slot is not within the menu bounds");
        }

        extraItemsMap.put(slot, item);
        slots.remove(Integer.valueOf(slot));
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

    @Override
    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, inventorySize, inventoryName);

        for (int i = 0; i < inventorySize-9; i++) {
            inventory.setItem(i, filler);
        }

        for (int i = inventorySize-9; i < inventorySize; i++) {
            inventory.setItem(i, pageRowFiller);
        }

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

        if (e.getClickedInventory() != e.getView().getTopInventory()) return;
        Player player = (Player) e.getWhoClicked();

        ClickableItem<?> clickableItem = extraItemsMap.get(e.getSlot());
        if (clickableItem != null) {
            clickableItem.onClick(e, player);
        }
        else {
            E item = getItemBySlot(playerPages.get(player.getUniqueId()), e.getSlot());
            if (item != null) onItemClick(e, player, item);
        }
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
        updatePage(player, playerPages.get(player.getUniqueId()) - 1);
    }

    /**
     * Handles what happens when the player clicks a custom item.
     * By default, this will give the item from {@link ItemGenerator#getPlayerItem(Player)}.
     * @param e The original click event
     * @param player The player
     * @param item The clicked item
     */
    public void onItemClick(InventoryClickEvent e, Player player, E item) {
        ItemStack itemStack = item.getPlayerItem(player);
        if (itemStack != null) player.getInventory().addItem(itemStack);
    }

    /**
     * Sets the contents of this page and updates the page info item
     * @param player The player's inventory to update
     */
    private void updatePage(Player player) {
        updatePage(player, playerPages.get(player.getUniqueId()));
    }

    /**
     * Sets the contents of this page and updates the page info item
     * @param player The player's inventory to update
     * @param page The page to open
     */
    private void updatePage(Player player, int page) {
        page = Numbers.constrain(page, 1, getMaxPage());
        playerPages.put(player.getUniqueId(), page);

        Inventory inventory = player.getOpenInventory().getTopInventory();

        for (int slot : slots) {
            inventory.setItem(slot, onItemClear(slot));
        }

        for (int i = 0; i < getNumItemsOnPage(page); i++) {
            int slot = slots.get(i);

            E item = getItemBySlot(page, slot);
            if (item != null) inventory.setItem(slot, onItemAdd(item, player));
        }

        for (Map.Entry<Integer, ClickableItem<?>> entry : extraItemsMap.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getMenuItem(player));
        }
    }

    /**
     * Forces a refresh of the player's current page contents.
     * @param player The player
     */
    public void refresh(Player player) {
        updatePage(player);
    }

    /**
     * Called when an item is removed from the inventory.
     * By default, a null value or the filler is returned.
     * @param slot The item slot
     * @return The resulting ItemStack
     */
    @Nullable
    public ItemStack onItemClear(int slot) {
        return replaceEmptySlotsWithAir ? null : filler;
    }

    /**
     * Called when an item is added to the inventory.
     * By default, this will get the item from {@link ItemGenerator#getMenuItem(Player)}.
     * @param item The generic item belonging to this inventory
     * @param player The player who will view this item
     * @return The resulting ItemStack
     */
    public ItemStack onItemAdd(E item, Player player) {
        return item.getMenuItem(player);
    }

    /**
     * Called when the page info item is inserted
     * @param template The item to clone and use
     * @param page The page
     * @return The item to add to the menu
     */
    public ItemStack createPageItem(ItemStack template, int page) {
        ItemStack pageInfo = template.clone();

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
}