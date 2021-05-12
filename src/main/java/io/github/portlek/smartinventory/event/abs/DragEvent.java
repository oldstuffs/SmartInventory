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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine drag events.
 */
public interface DragEvent extends IconEvent {

  /**
   * obtains the added.
   *
   * @return added.
   */
  @NotNull
  Map<Integer, ItemStack> added();

  /**
   * obtains the drag.
   *
   * @return drag.
   */
  @NotNull
  DragType drag();

  /**
   * obtains the event.
   *
   * @return event.
   */
  @NotNull
  InventoryDragEvent getEvent();

  /**
   * obtains the new cursor.
   *
   * @return new cursor.
   */
  @NotNull
  Optional<ItemStack> newCursor();

  /**
   * obtains the slots.
   *
   * @return slots.
   */
  @NotNull
  Set<Integer> slots();
}
