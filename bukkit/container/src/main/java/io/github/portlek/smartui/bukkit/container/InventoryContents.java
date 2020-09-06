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

package io.github.portlek.smartui.bukkit.container;

import io.github.portlek.smartui.bukkit.container.util.Pattern;
import io.github.portlek.smartui.bukkit.container.util.SlotPos;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InventoryContents {

    @NotNull
    Page page();

    @NotNull
    Pagination pagination();

    @NotNull
    Optional<SlotIterator> iterator(@NotNull String id);

    @NotNull
    SlotIterator newIterator(@NotNull String id, @NotNull SlotIterator.Type type, int startRow, int startColumn);

    @NotNull
    SlotIterator newIterator(@NotNull SlotIterator.Type type, int startRow, int startColumn);

    @NotNull
    SlotIterator newIterator(@NotNull String id, @NotNull SlotIterator.Type type, @NotNull SlotPos startPos);

    @NotNull
    SlotIterator newIterator(@NotNull SlotIterator.Type type, @NotNull SlotPos startPos);

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
    Optional<Icon> get(@NotNull SlotPos slotPos);

    @NotNull
    InventoryContents set(int index, @Nullable Icon item);

    @NotNull
    InventoryContents set(int row, int column, @Nullable Icon item);

    @NotNull
    InventoryContents set(@NotNull SlotPos slotPos, @Nullable Icon item);

    @NotNull
    InventoryContents add(Icon item);

    @NotNull
    Optional<SlotPos> findItem(@NotNull ItemStack item);

    @NotNull
    Optional<SlotPos> findItem(@NotNull Icon item);

    void removeFirst(@NotNull ItemStack item);

    void removeFirst(@NotNull Icon item);

    void removeAmount(@NotNull ItemStack item, int amount);

    void removeAmount(@NotNull Icon item, int amount);

    void removeAll(@NotNull ItemStack item);

    void removeAll(@NotNull Icon item);

    @NotNull
    InventoryContents fill(@NotNull Icon item);

    @NotNull
    InventoryContents fillEmpties(@NotNull Icon item);

    @NotNull
    InventoryContents fillRow(int row, @NotNull Icon item);

    @NotNull
    InventoryContents fillColumn(int column, @NotNull Icon item);

    @NotNull
    InventoryContents fillBorders(@NotNull Icon item);

    @NotNull
    InventoryContents fillRect(int fromIndex, int toIndex, @NotNull Icon item);

    @NotNull
    InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, @NotNull Icon item);

    @NotNull
    InventoryContents fillRect(@NotNull SlotPos fromPos, @NotNull SlotPos toPos, @NotNull Icon item);

    @NotNull
    InventoryContents fillSquare(int fromIndex, int toIndex, @NotNull Icon item);

    @NotNull
    InventoryContents fillSquare(int fromRow, int fromColumn, int toRow, int toColumn, @NotNull Icon item);

    @NotNull
    InventoryContents fillSquare(@NotNull SlotPos fromPos, @NotNull SlotPos toPos, @NotNull Icon item);

    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<Icon> pattern);

    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<Icon> pattern, int startIndex);

    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<Icon> pattern, int startRow, int startColumn);

    @NotNull
    InventoryContents fillPattern(@NotNull Pattern<Icon> pattern, @NotNull SlotPos startPos);

    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<Icon> pattern);

    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<Icon> pattern, int startIndex, int endIndex);

    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<Icon> pattern, int startRow, int startColumn,
                                           int endRow, int endColumn);

    @NotNull
    InventoryContents fillPatternRepeating(@NotNull Pattern<Icon> pattern, @NotNull SlotPos startPos,
                                           @NotNull SlotPos endPos);

    @NotNull
    InventoryContents applyRect(int fromRow, int fromColumn, int toRow, int toColumn,
                                @NotNull BiConsumer<Integer, Integer> apply);

    @NotNull
    InventoryContents applyRect(int fromRow, int fromColumn, int toRow, int toColumn, @NotNull Consumer<Icon> apply);

    @Nullable <T> T getProperty(@NotNull String name);

    @NotNull <T> T getPropertyOrDefault(@NotNull String name, @NotNull T def);

    @NotNull
    InventoryContents setProperty(@NotNull String name, @NotNull Object value);

    default InventoryContents setEditable(@NotNull final SlotPos slot) {
        return this.setEditable(slot, true);
    }

    InventoryContents setEditable(@NotNull SlotPos slot, boolean editable);

    boolean isEditable(@NotNull SlotPos slot);

    @NotNull
    Player player();

    @NotNull
    Inventory getBottomInventory();

    @NotNull
    Inventory getTopInventory();

    void notifyUpdate();

    void notifyUpdateForAll();

    void notifyUpdateForAllById();

}
