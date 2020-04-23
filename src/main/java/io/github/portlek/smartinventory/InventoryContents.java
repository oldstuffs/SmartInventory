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

package io.github.portlek.smartinventory;

import io.github.portlek.smartinventory.util.Pattern;
import io.github.portlek.smartinventory.util.SlotPos;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface InventoryContents {

    @NotNull
    Page page();

    @NotNull
    Pagination pagination();

    @NotNull
    Optional<SlotIterator> iterator(@NotNull String id);

    @NotNull
    SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn);

    @NotNull
    SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn);

    @NotNull
    SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos);

    @NotNull
    SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos);

    @NotNull
    Icon[][] all();

    @NotNull
    List<SlotPos> slots();

    @NotNull
    Optional<SlotPos> firstEmpty();

    @NotNull
    Optional<Icon> get(int index);

    @NotNull
    Optional<Icon> get(int row, int column);

    @NotNull
    Optional<Icon> get(SlotPos slotPos);

    @NotNull
    InventoryContents set(int index, Icon item);

    @NotNull
    InventoryContents set(int row, int column, Icon item);

    @NotNull
    InventoryContents set(SlotPos slotPos, Icon item);

    @NotNull
    InventoryContents add(Icon item);

    @NotNull
    Optional<SlotPos> findItem(ItemStack item);

    @NotNull
    Optional<SlotPos> findItem(Icon item);

    void removeFirst(ItemStack item);

    void removeFirst(Icon item);

    void removeAmount(ItemStack item, int amount);

    void removeAmount(Icon item, int amount);

    void removeAll(ItemStack item);

    void removeAll(Icon item);

    @NotNull
    InventoryContents fill(Icon item);

    @NotNull
    InventoryContents fillRow(int row, Icon item);

    @NotNull
    InventoryContents fillColumn(int column, Icon item);

    @NotNull
    InventoryContents fillBorders(Icon item);

    @NotNull
    InventoryContents fillRect(int fromIndex, int toIndex, Icon item);

    @NotNull
    InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, Icon item);

    @NotNull
    InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, Icon item);

    @NotNull
    InventoryContents fillSquare(int fromIndex, int toIndex, Icon item);

    @NotNull
    InventoryContents fillSquare(int fromRow, int fromColumn, int toRow, int toColumn, Icon item);

    @NotNull
    InventoryContents fillSquare(SlotPos fromPos, SlotPos toPos, Icon item);

    @NotNull
    InventoryContents fillPattern(Pattern<Icon> pattern);

    @NotNull
    InventoryContents fillPattern(Pattern<Icon> pattern, int startIndex);

    @NotNull
    InventoryContents fillPattern(Pattern<Icon> pattern, int startRow, int startColumn);

    @NotNull
    InventoryContents fillPattern(Pattern<Icon> pattern, SlotPos startPos);

    @NotNull
    InventoryContents fillPatternRepeating(Pattern<Icon> pattern);

    @NotNull
    InventoryContents fillPatternRepeating(Pattern<Icon> pattern, int startIndex, int endIndex);

    @NotNull
    InventoryContents fillPatternRepeating(Pattern<Icon> pattern, int startRow, int startColumn,
                                           int endRow, int endColumn);

    @NotNull
    InventoryContents fillPatternRepeating(Pattern<Icon> pattern, SlotPos startPos,
                                           SlotPos endPos);

    @NotNull <T> T property(String name);

    @NotNull <T> T property(String name, T def);

    @NotNull
    InventoryContents setProperty(String name, Object value);

    void setEditable(SlotPos slot, boolean editable);

    boolean isEditable(SlotPos slot);

    @NotNull
    Player player();

    @NotNull
    Inventory getBottomInventory();

    @NotNull
    Inventory getTopInventory();

    void notifyUpdate();

}
