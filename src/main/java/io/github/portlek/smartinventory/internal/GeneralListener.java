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

package io.github.portlek.smartinventory.internal;

import io.github.portlek.smartinventory.InventoryManager;
import io.github.portlek.smartinventory.ItemClickData;
import io.github.portlek.smartinventory.content.SlotPos;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

@SuppressWarnings("unchecked")
public final class GeneralListener implements Listener {

    private final InventoryManager manager;

    public GeneralListener(final InventoryManager mngr) {
        this.manager = mngr;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        this.manager.getInventory(player).ifPresent(inv -> {
            if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                event.getAction() == InventoryAction.NOTHING) {
                event.setCancelled(true);
                return;
            }
            if (!event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
                inv.getBottomListeners().stream()
                    .filter(listener -> listener.getType().equals(InventoryClickEvent.class))
                    .map(listener -> (InventoryListener<InventoryClickEvent>) listener)
                    .forEach(listener -> listener.accept(event));
                return;
            }
            final int row = event.getSlot() / 9;
            final int column = event.getSlot() % 9;
            if (!inv.checkBounds(row, column)) {
                return;
            }
            this.manager.getContents(player).ifPresent(contents -> {
                final SlotPos slot = SlotPos.of(row, column);
                if (!contents.isEditable(slot)) {
                    event.setCancelled(true);
                }
                inv.getListeners().stream()
                    .filter(listener -> listener.getType().equals(InventoryClickEvent.class))
                    // FIXME: 23.02.2020 We should not use casting.
                    .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(event));
                contents.get(slot).ifPresent(item ->
                    item.run(new ItemClickData(event, player, event.getCurrentItem(), slot))
                );
                // Don't update if the clicked slot is editable - prevent item glitching
                if (!contents.isEditable(slot)) {
                    player.updateInventory();
                }
            });
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryDrag(final InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        this.manager.getInventory(player).ifPresent(inv -> {
            final Optional<InventoryContents> optional = inv.getManager().getContents(player);
            if (!optional.isPresend()) {
                return;
            }
            final InventoryContents contents = optional.get();
            for (final int slot : event.getRawSlots()) {
                if (slot >= player.getOpenInventory().getTopInventory().getSize() || 
                        contents.isEditable(SlotPos.of(slot / 9, slot % 9))) {
                    continue;
                }
                event.setCancelled(true);
                break;
            }
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryDragEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(event));
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final Player player = (Player) event.getPlayer();
        this.manager.getInventory(player).ifPresent(inv -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryOpenEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(event));
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        this.manager.getInventory(player).ifPresent(inv -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(InventoryCloseEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(event));
            if (!inv.isCloseable()) {
                Bukkit.getScheduler().runTask(this.manager.getPlugin(), () ->
                    player.openInventory(event.getInventory())
                );
                return;
            }
            event.getInventory().clear();
            this.manager.cancelUpdateTask(player);
            this.manager.removeInventory(player);
            this.manager.removeContent(player);
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.manager.getInventory(player).ifPresent(inv -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(PlayerQuitEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(event));
            this.manager.removeInventory(player);
            this.manager.removeContent(player);
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginDisable(final PluginDisableEvent event) {
        new HashMap<>(this.manager.getInventories()).forEach((player, inv) -> {
            inv.getListeners().stream()
                .filter(listener -> listener.getType().equals(PluginDisableEvent.class))
                // FIXME: 23.02.2020 We should not use casting.
                .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(event));
            inv.close(player);
        });
        this.manager.clearInventories();
        this.manager.clearContents();
    }

}
