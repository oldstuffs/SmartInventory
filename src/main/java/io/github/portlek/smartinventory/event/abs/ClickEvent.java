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

package io.github.portlek.smartinventory.event.abs;

import java.util.Optional;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine click events.
 */
public interface ClickEvent extends IconEvent {

  /**
   * obtains the action.
   *
   * @return action.
   */
  @NotNull
  InventoryAction action();

  /**
   * obtains the click.
   *
   * @return click.
   */
  @NotNull
  ClickType click();

  /**
   * obtains the column.
   *
   * @return column.
   */
  int column();

  /**
   * obtains the current.
   *
   * @return the current.
   */
  @NotNull
  Optional<ItemStack> current();

  /**
   * obtains the cursor.
   *
   * @return cursor.
   */
  @NotNull
  Optional<ItemStack> cursor();

  /**
   * obtains the event.
   *
   * @return event.
   */
  @NotNull
  InventoryClickEvent getEvent();

  /**
   * obtains the row.
   *
   * @return row.
   */
  int row();

  /**
   * obtains the slot.
   *
   * @return slot.
   */
  @NotNull
  InventoryType.SlotType slot();
}
