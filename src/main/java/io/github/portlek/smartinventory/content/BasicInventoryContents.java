/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
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

import io.github.portlek.smartinventory.Icon;
import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.Pagination;
import io.github.portlek.smartinventory.SlotIterator;
import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.util.SlotPos;
import io.github.portlek.smartinventory.util.TitleUpdater;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an implementation for {@link InventoryContents}.
 */
public final class BasicInventoryContents implements InventoryContents {

  /**
   * the contents.
   */
  @Nullable
  private final Icon[][] contents;

  /**
   * the editable slots.
   */
  private final Set<SlotPos> editableSlots = new HashSet<>();

  /**
   * the iterators.
   */
  private final Map<String, SlotIterator> iterators = new HashMap<>();

  /**
   * the page.
   */
  @NotNull
  private final Page page;

  /**
   * the pagination.
   */
  private final Pagination pagination = new BasicPagination();

  /**
   * the player.
   */
  @NotNull
  private final Player player;

  /**
   * the properties.
   */
  private final Map<String, Object> properties = new HashMap<>();

  /**
   * ctpr.
   *
   * @param page the page.
   * @param player the player.
   * @param contents the contents.
   */
  public BasicInventoryContents(@NotNull final Page page, @NotNull final Player player,
                                @NotNull final Icon[][] contents) {
    this.page = page;
    this.player = player;
    this.contents = contents.clone();
  }

  /**
   * ctor.
   *
   * @param page the page.
   * @param player the player
   */
  public BasicInventoryContents(@NotNull final Page page, @NotNull final Player player) {
    this(page, player, new Icon[page.row()][page.column()]);
  }

  @NotNull
  @Override
  public Icon[][] all() {
    return this.contents.clone();
  }

  @NotNull
  @Override
  public Map<String, Object> getProperties() {
    return Collections.unmodifiableMap(this.properties);
  }

  @Override
  public boolean isEditable(@NotNull final SlotPos slot) {
    return this.editableSlots.contains(slot);
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
    final SlotIterator iterator = this.newIterator(type, startRow, startColumn);
    this.iterators.put(id, iterator);
    return iterator;
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
  public Player player() {
    return this.player;
  }

  @NotNull
  @Override
  public InventoryContents set(final int row, final int column, @Nullable final Icon item) {
    if (row < 0 || row >= this.contents.length) {
      return this;
    }
    if (column < 0 || column >= this.contents[row].length) {
      return this;
    }
    this.contents[row][column] = item;
    if (item == null) {
      this.update(row, column, null);
    } else {
      this.update(row, column, item.calculateItem(this));
    }
    return this;
  }

  @NotNull
  @Override
  public InventoryContents setEditable(@NotNull final SlotPos slot, final boolean editable) {
    if (editable) {
      this.editableSlots.add(slot);
    } else {
      this.editableSlots.remove(slot);
    }
    return this;
  }

  @NotNull
  @Override
  public InventoryContents setProperty(@NotNull final String name, @NotNull final Object value) {
    this.properties.put(name, value);
    return this;
  }

  @Override
  public void updateTitle(@NotNull final String newTitle) {
    TitleUpdater.updateInventory(this.player, newTitle);
  }

  /**
   * updates row and column of the inventory to the given item.
   *
   * @param row the row to update.
   * @param column the column to update.
   * @param item the item to update.
   */
  private void update(final int row, final int column, @Nullable final ItemStack item) {
    if (SmartInventory.getOpenedPlayers(this.page).contains(this.player())) {
      this.getTopInventory().setItem(this.page.column() * row + column, item);
    }
  }
}
