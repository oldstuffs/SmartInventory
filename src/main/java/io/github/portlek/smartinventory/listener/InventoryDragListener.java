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

import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.event.IcDragEvent;
import io.github.portlek.smartinventory.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

public final class InventoryDragListener implements Listener {

    @NotNull
    private final SmartInventory inventory;

    public InventoryDragListener(@NotNull final SmartInventory inventory) {
        this.inventory = inventory;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryDrag(final InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        this.inventory.getContents(player).ifPresent(contents -> {
            for (final int slot : event.getRawSlots()) {
                final SlotPos pos = SlotPos.of(slot / 9, slot % 9);
                contents.get(pos).ifPresent(icon ->
                    icon.accept(new IcDragEvent(this.inventory.plugin(), event, contents, icon)));
                if (slot >= player.getOpenInventory().getTopInventory().getSize() ||
                    contents.isEditable(pos)) {
                    continue;
                }
                event.setCancelled(true);
                break;
            }
        });
    }

}
