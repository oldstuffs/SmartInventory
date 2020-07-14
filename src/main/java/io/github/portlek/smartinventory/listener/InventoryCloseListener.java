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
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.event.PgCloseEvent;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class InventoryCloseListener implements Listener {

    @NotNull
    private final SmartInventory inventory;

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        final HumanEntity human = event.getPlayer();
        if (!(human instanceof Player)) {
            return;
        }
        final Player player = (Player) human;
        final Optional<InventoryContents> contentsoptional = this.inventory.getContents(player);
        if (!contentsoptional.isPresent()) {
            return;
        }
        final InventoryContents contents = contentsoptional.get();
        final Page page = contents.page();
        final PgCloseEvent close = new PgCloseEvent(contents);
        page.accept(close);
        if (!page.canClose(close)) {
            Bukkit.getScheduler().runTask(this.inventory.getPlugin(), () ->
                player.openInventory(event.getInventory()));
            return;
        }
        event.getInventory().clear();
        this.inventory.stopTick(player);
        this.inventory.removePage(player);
        this.inventory.removeContent(player);
    }

}
