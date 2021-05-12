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

package io.github.portlek.smartinventory;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * a class that opens {@link Inventory}s from the given {@link InventoryType}s.
 */
public interface InventoryOpener {

  /**
   * fills the given contents to the given inventory.
   *
   * @param inventory the inventory to fill.
   * @param contents the contents to fill.
   */
  default void fill(@NotNull final Inventory inventory, @NotNull final InventoryContents contents) {
    final var items = contents.all();
    for (var row = 0; row < items.length; row++) {
      for (var column = 0; column < items[row].length; column++) {
        if (items[row][column] != null) {
          inventory.setItem(9 * row + column, items[row][column].calculateItem(contents));
        }
      }
    }
  }

  /**
   * opens the page for the given player.
   *
   * @param contents the contents to open.
   *
   * @return opened inventory itself.
   */
  @NotNull
  Inventory open(@NotNull InventoryContents contents);

  /**
   * checks if the inventory type is supporting for {@code this}.
   *
   * @param type the type to check.
   *
   * @return {@code true} if the type supports the type..
   */
  boolean supports(@NotNull InventoryType type);
}
