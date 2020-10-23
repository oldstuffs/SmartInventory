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
import io.github.portlek.smartinventory.event.IcClickEvent;
import io.github.portlek.smartinventory.event.PgBottomClickEvent;
import io.github.portlek.smartinventory.event.PgClickEvent;
import io.github.portlek.smartinventory.event.PgOutsideClickEvent;
import io.github.portlek.smartinventory.util.SlotPos;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class InventoryClickListener implements Listener {

  @NotNull
  private final SmartInventory inventory;

  public InventoryClickListener(@NotNull final SmartInventory inventory) {
    this.inventory = inventory;
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final Player player = (Player) event.getWhoClicked();
    final Optional<Page> optional = this.inventory.getPage(player);
    if (!optional.isPresent()) {
      return;
    }
    final Page page = optional.get();
    final Optional<InventoryContents> contentsoptional = this.inventory.getContents(player);
    if (!contentsoptional.isPresent()) {
      return;
    }
    final InventoryContents contents = contentsoptional.get();
    if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
      event.setCancelled(true);
      return;
    }
    final Inventory clicked = event.getClickedInventory();
    if (clicked == null) {
      page.accept(new PgOutsideClickEvent(this.inventory.getPlugin(), event, contents));
      return;
    }
    if (clicked.equals(player.getOpenInventory().getBottomInventory())) {
      page.accept(new PgBottomClickEvent(this.inventory.getPlugin(), event, contents));
      return;
    }
    final ItemStack current = event.getCurrentItem();
    if (current == null || current.getType() == Material.AIR) {
      page.accept(new PgClickEvent(this.inventory.getPlugin(), event, contents));
      return;
    }
    final int row = event.getSlot() / 9;
    final int column = event.getSlot() % 9;
    if (!page.checkBounds(row, column)) {
      return;
    }
    final SlotPos slot = SlotPos.of(row, column);
    if (!contents.isEditable(slot)) {
      event.setCancelled(true);
    }
    contents.get(slot).ifPresent(item ->
      item.accept(new IcClickEvent(this.inventory.getPlugin(), event, contents, item)));
    if (!contents.isEditable(slot)) {
      player.updateInventory();
    }
  }
}
