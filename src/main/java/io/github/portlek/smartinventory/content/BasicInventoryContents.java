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

package io.github.portlek.smartinventory.content;

import com.google.common.base.Preconditions;
import io.github.portlek.smartinventory.*;
import io.github.portlek.smartinventory.util.Pattern;
import io.github.portlek.smartinventory.util.SlotPos;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public final class BasicInventoryContents implements InventoryContents {

    private final Pagination pagination = new BasicPagination();

    private final Map<String, SlotIterator> iterators = new HashMap<>();

    private final Map<String, Object> properties = new HashMap<>();

    private final Set<SlotPos> editableSlots = new HashSet<>();

    @NotNull
    private final Page page;

    @NotNull
    private final Player player;

    @NotNull
    private final Icon[][] contents;

    public BasicInventoryContents(@NotNull final Page page, @NotNull final Player player) {
        this(page, player, new Icon[page.row()][page.column()]);
    }

    @NotNull
    @Override
    public Page page() {
        return this.page;
    }

    @NotNull
    @Override
    public Pagination pagination() {
        return this.pagination;
    }

    @NotNull
    @Override
    public Optional<SlotIterator> iterator(@NotNull final String id) {
        return Optional.ofNullable(this.iterators.get(id));
    }

    @NotNull
    @Override
    public SlotIterator newIterator(@NotNull final String id, @NotNull final SlotIterator.Type type,
                                    final int startRow, final int startColumn) {
        final SlotIterator iterator = new BasicSlotIterator(this, type, startRow, startColumn);
        this.iterators.put(id, iterator);
        return iterator;
    }

    @NotNull
    @Override
    public SlotIterator newIterator(@NotNull final SlotIterator.Type type, final int startRow,
                                    final int startColumn) {
        return new BasicSlotIterator(this, type, startRow, startColumn);
    }

    @NotNull
    @Override
    public SlotIterator newIterator(@NotNull final String id, @NotNull final SlotIterator.Type type,
                                    @NotNull final SlotPos startPos) {
        return this.newIterator(id, type, startPos.getRow(), startPos.getColumn());
    }

    @NotNull
    @Override
    public SlotIterator newIterator(@NotNull final SlotIterator.Type type, @NotNull final SlotPos startPos) {
        return this.newIterator(type, startPos.getRow(), startPos.getColumn());
    }

    @NotNull
    @Override
    public Icon[][] all() {
        return this.contents.clone();
    }

    @NotNull
    @Override
    public List<SlotPos> slots() {
        final List<SlotPos> position = new ArrayList<>();
        for (int row = 0; row < this.contents.length; row++) {
            for (int column = 0; column < this.contents[0].length; column++) {
                position.add(SlotPos.of(row, column));
            }
        }
        return position;
    }

    @NotNull
    @Override
    public Optional<SlotPos> firstEmpty() {
        for (int row = 0; row < this.contents.length; row++) {
            for (int column = 0; column < this.contents[0].length; column++) {
                if (!this.get(row, column).isPresent()) {
                    return Optional.of(new SlotPos(row, column));
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<Icon> get(final int index) {
        final int count = this.page.column();
        return this.get(index / count, index % count);
    }

    @NotNull
    @Override
    public Optional<Icon> get(final int row, final int column) {
        if (row < 0 || row >= this.contents.length) {
            return Optional.empty();
        }
        if (column < 0 || column >= this.contents[row].length) {
            return Optional.empty();
        }
        return Optional.of(this.contents[row][column]);
    }

    @NotNull
    @Override
    public Optional<Icon> get(@NotNull final SlotPos pos) {
        return this.get(pos.getRow(), pos.getColumn());
    }

    @NotNull
    @Override
    public InventoryContents set(final int index, @NotNull final Icon item) {
        final int columnCount = this.page.column();
        return this.set(index / columnCount, index % columnCount, item);
    }

    @NotNull
    @Override
    public InventoryContents set(final int row, final int column, @NotNull final Icon item) {
        if (row < 0 || row >= this.contents.length) {
            return this;
        }
        if (column < 0 || column >= this.contents[row].length) {
            return this;
        }
        this.contents[row][column] = item;
        this.update(row, column, item.calculateItem(this));
        return this;
    }

    @NotNull
    @Override
    public InventoryContents set(@NotNull final SlotPos slotPos, @NotNull final Icon item) {
        return this.set(slotPos.getRow(), slotPos.getColumn(), item);
    }

    @NotNull
    @Override
    public InventoryContents remove(final int index) {
        final int columnCount = this.page.column();
        return this.remove(index / columnCount, index % columnCount);
    }

    @NotNull
    @Override
    public InventoryContents remove(final int row, final int column) {
        if (row < 0 || row >= this.contents.length) {
            return this;
        }
        if (column < 0 || column >= this.contents[row].length) {
            return this;
        }
        this.contents[row][column] = Icon.EMPTY;
        this.removeAndUpdate(row, column);
        return this;
    }

    @NotNull
    @Override
    public InventoryContents remove(@NotNull final SlotPos slotPos) {
        return this.remove(slotPos.getRow(), slotPos.getColumn());
    }

    @NotNull
    @Override
    public InventoryContents add(@NotNull final Icon item) {
        for (int row = 0; row < this.contents.length; row++) {
            for (int column = 0; column < this.contents[0].length; column++) {
                if (Icon.EMPTY.equals(this.contents[row][column])) {
                    this.set(row, column, item);
                    return this;
                }
            }
        }
        return this;
    }

    @NotNull
    @Override
    public Optional<SlotPos> findItem(@NotNull final ItemStack itemStack) {
        for (int row = 0; row < this.contents.length; row++) {
            for (int column = 0; column < this.contents[0].length; column++) {
                final Icon item = this.contents[row][column];
                if (itemStack.isSimilar(item.calculateItem(this))) {
                    return Optional.of(SlotPos.of(row, column));
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<SlotPos> findItem(@NotNull final Icon item) {
        return this.findItem(item.calculateItem(this));
    }

    @Override
    public void removeFirst(@NotNull final ItemStack item) {
        this.findItem(item).ifPresent(this::remove);
    }

    @Override
    public void removeFirst(@NotNull final Icon item) {
        this.removeFirst(item.calculateItem());
    }

    @Override
    public void removeAmount(@NotNull final ItemStack item, int amount) {
        for (int row = 0; row < this.contents.length; row++) {
            for (int column = 0; column < this.contents[row].length; column++) {
                final Icon icon = this.contents[row][column];
                if (!item.isSimilar(icon.calculateItem())) {
                    continue;
                }
                final ItemStack foundStack = icon.calculateItem();
                if (foundStack.getAmount() <= amount) {
                    amount -= foundStack.getAmount();
                    this.remove(row, column);
                    if (amount == 0) {
                        return;
                    }
                } else if (foundStack.getAmount() > amount) {
                    final ItemStack clonedStack = foundStack.clone();
                    clonedStack.setAmount(clonedStack.getAmount() - amount);
                    final Icon clonedIcon = icon.clone(clonedStack);
                    this.set(row, column, clonedIcon);
                    return;
                }
            }
        }
    }

    @Override
    public void removeAmount(@NotNull final Icon item, final int amount) {
        Preconditions.checkNotNull(item, "The clickableitem to remove cannot be null");
        this.removeAmount(item.calculateItem(), amount);
    }

    @Override
    public void removeAll(@NotNull final ItemStack item) {
        Preconditions.checkNotNull(item, "The itemstack to remove cannot be null");
        for (int row = 0; row < this.contents.length; row++) {
            for (int column = 0; column < this.contents[row].length; column++) {
                if (this.contents[row][column] != null &&
                    item.isSimilar(this.contents[row][column].calculateItem())) {
                    this.set(row, column, null);
                }
            }
        }
    }

    @Override
    public void removeAll(@NotNull final Icon item) {
        Preconditions.checkNotNull(item, "The clickableitem to remove cannot be null");
        this.removeAll(item.calculateItem());
    }

    @Override
    public InventoryContents fill(@NotNull final Icon item) {
        for (int row = 0; row < this.contents.length; row++) {
            for (int column = 0; column < this.contents[row].length; column++) {
                this.set(row, column, item);
            }
        }
        return this;
    }

    @Override
    public InventoryContents fillRow(final int row, @NotNull final Icon item) {
        if (row < 0 || row >= this.contents.length) {
            return this;
        }
        for (int column = 0; column < this.contents[row].length; column++) {
            this.set(row, column, item);
        }
        return this;
    }

    @Override
    public InventoryContents fillColumn(final int column, @NotNull final Icon item) {
        if (column < 0 || column >= this.contents[0].length) {
            return this;
        }
        for (int row = 0; row < this.contents.length; row++) {
            this.set(row, column, item);
        }
        return this;
    }

    @Override
    public InventoryContents fillBorders(@NotNull final Icon item) {
        this.fillRect(0, 0, this.page.row() - 1, this.page.column() - 1, item);
        return this;
    }

    @Override
    public InventoryContents fillRect(final int fromIndex, final int toIndex, @NotNull final Icon item) {
        final int count = this.page.column();
        return this.fillRect(
            fromIndex / count, fromIndex % count,
            toIndex / count, toIndex % count,
            item
        );
    }

    @Override
    public InventoryContents fillRect(final int fromRow, final int fromColumn, final int toRow, final int toColumn,
                                      @NotNull final Icon item) {
        for (int row = fromRow; row <= toRow; row++) {
            for (int column = fromColumn; column <= toColumn; column++) {
                if (row != fromRow &&
                    row != toRow &&
                    column != fromColumn &&
                    column != toColumn) {
                    continue;
                }
                this.set(row, column, item);
            }
        }
        return this;
    }

    @Override
    public InventoryContents fillRect(@NotNull final SlotPos fromPos, @NotNull final SlotPos toPos,
                                      @NotNull final Icon item) {
        return this.fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
    }

    @Override
    public InventoryContents fillSquare(final int fromIndex, final int toIndex, @NotNull final Icon item) {
        final int count = this.page.column();
        return this.fillSquare(
            fromIndex / count, fromIndex % count,
            toIndex / count, toIndex % count,
            item
        );
    }

    @Override
    public InventoryContents fillSquare(final int fromRow, final int fromColumn, final int toRow,
                                        final int toColumn, @NotNull final Icon item) {
        Preconditions.checkArgument(fromRow < toRow, "The start row needs to be lower than the end row");
        Preconditions.checkArgument(fromColumn < toColumn,
            "The start column needs to be lower than the end column");
        for (int row = fromRow; row <= toRow; row++) {
            for (int column = fromColumn; column <= toColumn; column++) {
                this.set(row, column, item);
            }
        }
        return this;
    }

    @Override
    public InventoryContents fillSquare(@NotNull final SlotPos fromPos, @NotNull final SlotPos toPos,
                                        @NotNull final Icon item) {
        return this.fillSquare(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
    }

    @Override
    public InventoryContents fillPattern(@NotNull final Pattern<Icon> pattern) {
        return this.fillPattern(pattern, 0, 0);
    }

    @Override
    public InventoryContents fillPattern(@NotNull final Pattern<Icon> pattern, final int startIndex) {
        final int count = this.page.column();
        return this.fillPattern(pattern, startIndex / count, startIndex % count);
    }

    @Override
    public InventoryContents fillPattern(@NotNull final Pattern<Icon> pattern, final int startRow,
                                         final int startColumn) {
        for (int row = 0; row < pattern.getRowCount(); row++) {
            for (int column = 0; column < pattern.getColumnCount(); column++) {
                final int finalRow = startRow + row;
                final int finalColumn = startColumn + column;
                pattern.getObject(row, column).ifPresent(icon ->
                    this.set(finalRow, finalColumn, icon));
            }
        }
        return this;
    }

    @Override
    public InventoryContents fillPattern(@NotNull final Pattern<Icon> pattern, @NotNull final SlotPos startPos) {
        return this.fillPattern(pattern, startPos.getRow(), startPos.getColumn());
    }

    @Override
    public InventoryContents fillPatternRepeating(@NotNull final Pattern<Icon> pattern) {
        return this.fillPatternRepeating(pattern, 0, 0, -1, -1);
    }

    @Override
    public InventoryContents fillPatternRepeating(@NotNull final Pattern<Icon> pattern,
                                                  final int startIndex, final int endIndex) {
        final int columnCount = this.page.column();
        final boolean maxSize = endIndex < 0;

        if (maxSize) {
            return this.fillPatternRepeating(pattern, startIndex / columnCount, startIndex % columnCount, -1, -1);
        }
        return this.fillPatternRepeating(pattern, startIndex / columnCount, startIndex % columnCount,
            endIndex / columnCount, endIndex % columnCount);
    }

    @Override
    public InventoryContents fillPatternRepeating(@NotNull final Pattern<Icon> pattern, final int startRow,
                                                  final int startColumn, int endRow, int endColumn) {
        Preconditions.checkArgument(pattern.isWrapAround(),
            "To fill in a repeating pattern wrapAround needs to be enabled for the pattern to work!");
        if (endRow < 0) {
            endRow = this.page.row();
        }
        if (endColumn < 0) {
            endColumn = this.page.column();
        }
        Preconditions.checkArgument(startRow < endRow, "The start row needs to be lower than the end row");
        Preconditions.checkArgument(startColumn < endColumn, "The start column needs to be lower than the end column");
        final int rowDelta = endRow - startRow;
        final int columnDelta = endColumn - startColumn;
        for (int row = 0; row <= rowDelta; row++) {
            for (int column = 0; column <= columnDelta; column++) {
                final int finalRow = startRow + row;
                final int finalColumn = startColumn + column;
                pattern.getObject(row, column).ifPresent(icon ->
                    this.set(finalRow, finalColumn, icon));
            }
        }
        return this;
    }

    @Override
    public InventoryContents fillPatternRepeating(@NotNull final Pattern<Icon> pattern,
                                                  @NotNull final SlotPos startPos, @NotNull final SlotPos endPos) {
        return this.fillPatternRepeating(pattern, startPos.getRow(), startPos.getColumn(), endPos.getRow(),
            endPos.getColumn());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T property(@NotNull final String name) {
        return (T) this.properties.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T propertyOrDefault(@NotNull final String name, @NotNull final T def) {
        return (T) this.properties.getOrDefault(name, def);
    }

    @Override
    public InventoryContents property(@NotNull final String name, @NotNull final Object value) {
        this.properties.put(name, value);
        return this;
    }

    @Override
    public InventoryContents setEditable(@NotNull final SlotPos slot, final boolean editable) {
        if (editable) {
            this.editableSlots.add(slot);
        } else {
            this.editableSlots.remove(slot);
        }
        return this;
    }

    @Override
    public boolean isEditable(@NotNull final SlotPos slot) {
        return this.editableSlots.contains(slot);
    }

    @NotNull
    @Override
    public Player player() {
        return this.player;
    }

    @Override
    @NotNull
    public Inventory getBottomInventory() {
        return this.player.getOpenInventory().getBottomInventory();
    }

    @Override
    @NotNull
    public Inventory getTopInventory() {
        return this.player.getOpenInventory().getTopInventory();
    }

    @Override
    public void notifyUpdate() {
        this.page.notifyUpdate(this);
    }

    private void update(final int row, final int column, @NotNull final ItemStack item) {
        if (this.page.inventory().getOpenedPlayers(this.page).contains(this.player)) {
            final Inventory inv = this.getTopInventory();
            inv.setItem(this.page.column() * row + column, item);
        }
    }

    private void removeAndUpdate(final int row, final int column) {
        if (this.page.inventory().getOpenedPlayers(this.page).contains(this.player)) {
            final Inventory inv = this.getTopInventory();
            inv.setItem(this.page.column() * row + column, null);
        }
    }

}
