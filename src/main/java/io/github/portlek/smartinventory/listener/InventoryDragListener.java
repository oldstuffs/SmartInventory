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

package io.github.portlek.smartinventory.listener;

import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.event.IcDragEvent;
import io.github.portlek.smartinventory.util.SlotPos;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * a class that represents inventory drag listeners.
 */
public final class InventoryDragListener implements Listener {

  /**
   * listens inventory drag events.
   *
   * @param event the event to listen.
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onInventoryDrag(final InventoryDragEvent event) {
    SmartInventory.getHolder(event.getWhoClicked().getUniqueId()).ifPresent(holder -> {
      final Inventory inventory = event.getInventory();
      final InventoryContents contents = holder.getContents();
      for (final int slot : event.getRawSlots()) {
        final SlotPos pos = SlotPos.of(slot / 9, slot % 9);
        contents.get(pos).ifPresent(icon ->
          icon.accept(new IcDragEvent(holder.getPlugin(), event, contents, icon)));
        if (slot >= inventory.getSize() || contents.isEditable(pos)) {
          continue;
        }
        event.setCancelled(true);
        break;
      }
    });
  }
}
