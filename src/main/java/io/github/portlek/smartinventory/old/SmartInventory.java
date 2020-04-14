/*
 * MIT License
 *
 * Copyright (c) 2020 Hasan Demirtaş
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.portlek.smartinventory.old;

import com.google.common.base.Preconditions;
import io.github.portlek.smartinventory.old.content.InventoryContents;
import io.github.portlek.smartinventory.old.content.InventoryProvider;
import io.github.portlek.smartinventory.old.content.SlotPos;
import io.github.portlek.smartinventory.old.internal.GeneralListener;
import io.github.portlek.smartinventory.old.internal.InventoryListener;
import io.github.portlek.smartinventory.old.opener.InventoryOpener;
import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("unchecked")
public final class SmartInventory {

    /**
     * Inventory Manager. This field is the core component for managing SmartInventory inventories.
     */
    private final InventoryManager manager;

    /**
     * ID string. The field is the unique id for the custom inventory. This will help you identify the inventory
     * When creating a SmartInv you will want to add a custom 'id' to help identify which inventory you are working
     * with so that the rest of the API may uniquely recognize it.
     */
    private final String id;

    /**
     * Title String. The field is the title for the custom inventory. Every inventory needs a title, when you want
     * to make a custom title you will need to use this field.
     */
    private final String title;

    /**
     * Inventory type. This field will allow you to decide which type of inventory you want to display to the user.
     * However, only a select number of types are supported.
     * <p>
     * <b>Standard Supported Inventory Types:</b>
     *  <ul>
     *      <li>Chest</li>
     *      <li>Ender Chest</li>
     *  </ul>
     * <b>Special Supported Inventory Types:</b>
     *  <ul>
     *      <li>Furnace</li>
     *      <li>Workbench</li>
     *      <li>Dispenser</li>
     *      <li>Dropper</li>
     *      <li>Enchanting</li>
     *      <li>Brewing</li>
     *      <li>Anvil</li>
     *      <li>Beacon</li>
     *      <li>Hopper</li>
     *  </ul>
     * <b>Un-Supported Inventory Types:</b>
     *  <ul>
     *      <li>Crafting</li>
     *      <li>Player</li>
     *      <li>creative</li>
     *      <li>Merchant</li>
     *  </ul>
     */
    private final InventoryType type;

    /**
     * Rows &amp; Columns Integer. This field has default values for both rows (6) &amp; columns (9).
     * Both 'rows' &amp; 'columns' are essential for setting the size for two inventory types:
     * <ul>
     *     <li>Chest</li>
     *     <li>Ender Chest</li>
     * </ul>
     * The column count must be set to nine (9) else an error message will output. The row count must
     * be set between one (1) &amp; six (6) else an error message will output.
     */
    private final int columns, rows;

    private final long updateFrequency;

    /**
     * Provider Inventory. This field is the provider interface for getting contents inside the SmartInventory.
     */
    private final InventoryProvider provider;

    /**
     * Parent smart inventory. This field is the key to making the inventory a parent. This field can be
     * utilized making a hierarchy of custom inventories.
     */
    private final SmartInventory parent;

    /**
     * Listener array. This field being an array is the key to the inventory listeners which also extends
     * the {@link Event} class and will list all inventory listeners which have been created. You will use
     * this field to list all custom created listeners for the custom inventories.
     */
    private final List<InventoryListener<? extends Event>> listeners;

    private final List<InventoryListener<? extends Event>> bottomListeners;

    /**
     * Inventory close boolean. This field being a boolean, is the key to allowing the inventory to
     * be closed or not. Setting the closable to 'true' will allow the custom SmartInv to be closed.
     * Setting the closable to 'false' will not allow the SmartInv to be closed freely.
     */
    private boolean closeable;

    public SmartInventory(final InventoryManager manager, final String id,
                          final String title, final InventoryType type, final int rows,
                          final int columns, final boolean closeable, final long updateFrequency,
                          final InventoryProvider provider, final SmartInventory parent,
                          final List<InventoryListener<? extends Event>> listeners,
                          final List<InventoryListener<? extends Event>> bottomListeners) {
        this.manager = manager;
        this.id = id;
        this.title = title;
        this.type = type;
        this.rows = rows;
        this.columns = columns;
        this.closeable = closeable;
        this.updateFrequency = updateFrequency;
        this.provider = provider;
        this.parent = parent;
        this.listeners = listeners;
        this.bottomListeners = bottomListeners;
    }

    /**
     * Used for creating a custom SmartInventory inventory. This builder method has a predefined inventory
     * type (chest), row amount (6) and column amount (9). By default, the inventory is closeable. You
     * would use this to create a custom child inventory for your parent inventory.
     *
     * @return custom child inventory with custom data.
     */
    public static SmartInventory.Builder builder() {
        return new SmartInventory.Builder();
    }

    /**
     * Opens up the given inventory on the first page.
     *
     * @param player Gets the player opening up the SmartInventory
     * @return the opened SmartInventory inventory
     */
    public Inventory open(final Player player) {
        return this.open(player, 0, Collections.emptyMap());
    }

    public Inventory open(final Player player, final int page, final Map<String, Object> properties) {
        final Optional<SmartInventory> oldInv = this.manager.getInventory(player);
        oldInv.ifPresent(inv -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryCloseEvent.class))
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                    .accept(new InventoryCloseEvent(player.getOpenInventory())));

            this.manager.setInventory(player, null);
        });
        final InventoryContents contents = new InventoryContents.Impl(this, player);
        contents.pagination().page(page);
        properties.forEach(contents::setProperty);
        this.manager.setContents(player, contents);
        this.provider.init(player, contents);
        final InventoryOpener opener = this.manager.findOpener(this.type)
            .orElseThrow(() ->
                new IllegalStateException("No opener found for the inventory type " + this.type.name())
            );
        final Inventory handle = opener.open(this, player);
        this.manager.setInventory(player, this);
        this.manager.scheduleUpdateTask(player, this);
        return handle;
    }

    /**
     * Any custom listeners that is associated with {@link InventoryListener} will be instantiated
     * as a List. This List also extends the {@link Event} class to require the new listener return
     * a {@link org.bukkit.event.HandlerList}
     *
     * @return handlerList as listener
     */
    public List<InventoryListener<? extends Event>> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    /**
     * Opens up the given inventory with the inputted page number.
     *
     * @param player Gets the player opening up the SmartInventory
     * @param page Open inventory to inputted page number
     * @return SmartInventory inventory to the given page number
     */
    public Inventory open(final Player player, final int page) {
        return this.open(player, page, Collections.emptyMap());
    }

    public Inventory open(final Player player, final Map<String, Object> properties) {
        return this.open(player, 0, properties);
    }

    public List<InventoryListener<? extends Event>> getBottomListeners() {
        return Collections.unmodifiableList(this.bottomListeners);
    }

    /**
     * Close the SmartInventory for the given player.
     *
     * @param player Gets the player looking at the SmartInventory
     */
    public void close(final Player player) {
        this.listeners.stream()
            .filter(listener -> listener.getType().equals(InventoryCloseEvent.class))
            .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                .accept(new InventoryCloseEvent(player.getOpenInventory())));

        this.manager.setInventory(player, null);
        player.closeInventory();

        this.manager.setContents(player, null);
        this.manager.cancelUpdateTask(player);
    }

    /**
     * Checks if this inventory has a slot at the specified position.
     *
     * @param row Slot row (starts at 0)
     * @param col Slot column (starts at 0)
     * @return Returns false if row or col less than 0 or
     * row and col less than {@link #rows} and {@link #columns}
     */
    public boolean checkBounds(final int row, final int col) {
        if (row < 0 || col < 0) {
            return false;
        }
        return row < this.rows && col < this.columns;
    }

    /**
     * Gets the custom id for the given SmartInventory.
     *
     * @return custom SmartInventory id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the SmartInventory custom title.
     *
     * @return SmartInventory custom title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets the SmartInventory inventory type.
     *
     * @return SmartInventory inventory type
     */
    public InventoryType getType() {
        return this.type;
    }

    /**
     * Gets the number of rows for the given SmartInventory.
     *
     * @return number of rows
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Gets the number of columns for the given SmartInventory.
     *
     * @return number columns
     */
    public int getColumns() {
        return this.columns;
    }

    /**
     * Will return a boolean value if the given SmartInventory is closable.
     *
     * @return true if closable, false if not closable
     */
    public boolean isCloseable() {
        return this.closeable;
    }

    /**
     * Set whether the SmartInventory is closable or not. If you set the setCloseable() to true, then the SmartInventory
     * will return true for {@link #isCloseable()}. If you set the setCloseable() to false, then the SmartInventory will return
     * false for {@link #isCloseable()}.
     *
     * @param closeable Enable or Disable SmartInventory closeable feature
     */
    public void setCloseable(final boolean closeable) {
        this.closeable = closeable;
    }

    public long getUpdateFrequency() {
        return this.updateFrequency;
    }

    /**
     * Will return the custom SmartInventory with all of its contents for the given player.
     *
     * @return custom SmartInventory contents
     */
    public InventoryProvider getProvider() {
        return this.provider;
    }

    /**
     * Gets the parent of a Smart Inventory.
     *
     * @return smart inventory parent
     */
    public Optional<SmartInventory> getParent() {
        return Optional.ofNullable(this.parent);
    }

    /**
     * Get Inventory Manager and manage the operations of a SmartInventory.
     *
     * @return inventory manager property
     */
    public InventoryManager getManager() {
        return this.manager;
    }

    /**
     * A static inner class for creating a custom child inventory.
     */
    public static final class Builder {

        /**
         * Listener array. This field being an array is the key to the inventory listeners which also extends
         * the {@link Event} class and will list all inventory listeners which have been created. You will use
         * this field to list all custom created listeners for the custom inventories.
         */
        private final List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

        private final List<InventoryListener<? extends Event>> bottomListeners = new ArrayList<>();

        /**
         * ID string. This field is the inventory's unique id. You will utilize this field to create its
         * own identifying properties when search for your custom inventories.
         */
        private String id = "unknown";

        /**
         * Title string. This field is the inventory's title. If you are not wanting your child inventory
         * to have a title you may leave this field blank. This field will allow you to add a custom
         * title to your custom child inventory that will differ from your parent inventory.
         */
        private String title = "";

        /**
         * Inventory Type. This field has a default inventory type of chest. The inventory type
         * may be changed to any of the following supported types:
         * <p>
         * <b>Standard Supported Inventory Types:</b>
         *  <ul>
         *      <li>Chest</li>
         *      <li>Ender Chest</li>
         *  </ul>
         * <b>Special Supported Inventory Types:</b>
         *  <ul>
         *      <li>Furnace</li>
         *      <li>Workbench</li>
         *      <li>Dispenser</li>
         *      <li>Dropper</li>
         *      <li>Enchanting</li>
         *      <li>Brewing</li>
         *      <li>Anvil</li>
         *      <li>Beacon</li>
         *      <li>Hopper</li>
         *  </ul>
         * <b>Un-Supported Inventory Types:</b>
         *  <ul>
         *      <li>Crafting</li>
         *      <li>Player</li>
         *      <li>creative</li>
         *      <li>Merchant</li>
         *  </ul>
         */
        private InventoryType type = InventoryType.CHEST;

        /**
         * Rows &amp; Columns Integer. This field has default values for both rows (6) &amp; columns (9).
         * The columns field must have the amount nine (9) without throwing any errors. You may select
         * between one (1) &amp; six (6) without throwing any errors.
         */
        private Optional<Integer> rows, columns = Optional.empty();

        /**
         * Inventory close boolean. This field being a boolean, is the key to allowing the inventory to
         * be closed or not. By default, this field is set to true which will allow you to close your
         * child inventory. If you want to stop your custom child inventory from being closed freely
         * you will want to set this boolean to 'false'.
         */
        private boolean closeable = true;

        private long updateFrequency = 1L;

        /**
         * Inventory Manager. This field is the core component for managing SmartInventory inventories. You
         * can use this field to set your contents inside your custom child inventory as well as get all
         * the contents already set. This field will also allow you to get all the players who have opened
         * the custom child inventory. The manager field also has custom event listeners which will help
         * with managing your custom child inventory. Please review {@link GeneralListener}
         * for more information.
         */
        private InventoryManager manager;

        /**
         * Provider Inventory. This field is the provider interface for getting contents inside the SmartInventory.
         * This field will populate the custom child inventory and get the player current interacting with
         * that given custom child inventory.
         */
        private InventoryProvider provider;

        /**
         * Parent SmartInventory. This field will get the attached parent inventory that will within this
         * child's inventory hierarchy.
         */
        private SmartInventory parent;

        /**
         * The default constructor is defined for cleaner code.
         */
        private Builder() {
        }

        /**
         * Set a specific SmartInventory unique id.
         *
         * @param id set SmartInventory id
         * @return The builder chain.
         */
        public SmartInventory.Builder id(final String id) {
            this.id = id;
            return this;
        }

        /**
         * Set a specific SmartInventory title.
         *
         * @param title set SmartInventory title
         * @return The builder chain.
         */
        public SmartInventory.Builder title(final String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a specific inventory type.
         *
         * @param type set inventory type
         * @return The builder chain.
         */
        public SmartInventory.Builder type(final InventoryType type) {
            this.type = type;
            return this;
        }

        /**
         * Set the amount of rows and set the amount of columns in a SmartInventory.
         *
         * @param rows set row amount
         * @param columns set column amount
         * @return The builder chain.
         */
        public SmartInventory.Builder size(final int rows, final int columns) {
            this.rows = Optional.of(rows);
            this.columns = Optional.of(columns);
            return this;
        }

        /**
         * Set whether the inventory is closeable or not.
         *
         * @param closeable false indicates that the inventory is not closeable, true by default
         * from closeable field indicates that the inventory is closeable
         * @return The builder chain.
         */
        public SmartInventory.Builder closeable(final boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        /**
         * This method is used to configure the frequency at which the {@link InventoryProvider#update(Player, InventoryContents)}
         * method is called. Defaults to 1
         *
         * @param frequency The inventory update frequency, in ticks
         * @return The builder chain.
         * @throws IllegalArgumentException If frequency is smaller than 1.
         */
        public SmartInventory.Builder updateFrequency(final long frequency) {
            Preconditions.checkArgument(frequency > 0L, "frequency must be > 0");
            this.updateFrequency = frequency;
            return this;
        }

        /**
         * Method to get all the contents from inside the SmartInventory inventory.
         *
         * @param provider update inventory with all contents inside
         * @return The builder chain.
         */
        public SmartInventory.Builder provider(final InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        /**
         * Provides an association to the parent inventory for the children inventories. This method will
         * allow you to create a hierarchy of inventories.
         *
         * @param parent get parent inventory
         * @return The builder chain.
         */
        public SmartInventory.Builder parent(final SmartInventory parent) {
            this.parent = parent;
            return this;
        }

        /**
         * Listens for custom events. This method extends {@link Event} with the generics lower bounded wildcard.
         *
         * @param listener listen for events within SmartInventory
         * @return The builder chain.
         */
        public SmartInventory.Builder listener(final InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        /**
         * Listens for button inventory events. This method extends {@link Event} with the generics lower bounded wildcard.
         *
         * @param listener listen for events within SmartInventory
         * @return The builder chain.
         */
        public SmartInventory.Builder bottomListener(final InventoryListener<? extends Event> listener) {
            this.bottomListeners.add(listener);
            return this;
        }

        /**
         * Manages the custom inventory. This method handles all the actions that can be performs within
         * a custom inventory using SmartInventory.
         *
         * @param manager manage the custom actions of an inventory
         * @return The builder chain.
         */
        public SmartInventory.Builder manager(final InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public String getId() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public InventoryType getType() {
            return this.type;
        }

        public Optional<Integer> getRows() {
            return this.rows;
        }

        public Optional<Integer> getColumns() {
            return this.columns;
        }

        public boolean isCloseable() {
            return this.closeable;
        }

        public long getUpdateFrequency() {
            return this.updateFrequency;
        }

        public Optional<InventoryProvider> getProvider() {
            return Optional.ofNullable(this.provider);
        }

        public Optional<SmartInventory> getParent() {
            return Optional.ofNullable(this.parent);
        }

        public List<InventoryListener<? extends Event>> getListeners() {
            return Collections.unmodifiableList(this.listeners);
        }

        public List<InventoryListener<? extends Event>> getBottomListeners() {
            return Collections.unmodifiableList(this.bottomListeners);
        }

        /**
         * Builds the custom SmartInventory inventory.
         *
         * @return the custom inventory with its prerequisites
         */
        public SmartInventory build() {
            if (this.provider == null) {
                throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");
            }
            if (this.manager == null) {
                throw new IllegalStateException("The manager of the SmartInventory.Builder must be set.");
            }
            return new SmartInventory(
                this.manager,
                this.id,
                this.title,
                this.type,
                this.rows.orElseGet(() -> this.getDefaultDimensions(this.type).getRow()),
                this.columns.orElseGet(() -> this.getDefaultDimensions(this.type).getColumn()),
                this.closeable,
                this.updateFrequency,
                this.provider,
                this.parent,
                this.listeners,
                this.bottomListeners
            );
        }

        private SlotPos getDefaultDimensions(final InventoryType type) {
            return this.getManager().orElseThrow(() ->
                new IllegalStateException("Cannot find InventoryManager for type " + type)
            ).findOpener(type).orElseThrow(() ->
                new IllegalStateException("Cannot find InventoryOpener for type " + type)
            ).defaultSize(type);
        }

        public Optional<InventoryManager> getManager() {
            return Optional.ofNullable(this.manager);
        }

    }

}
