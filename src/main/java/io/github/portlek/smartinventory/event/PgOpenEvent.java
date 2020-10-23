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

package io.github.portlek.smartinventory.event;

import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.event.abs.OpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class PgOpenEvent implements OpenEvent {

  @NotNull
  private final Plugin plugin;

  @NotNull
  private final InventoryOpenEvent event;

  @NotNull
  private final InventoryContents contents;

  public PgOpenEvent(@NotNull final Plugin plugin, @NotNull final InventoryOpenEvent event,
                     @NotNull final InventoryContents contents) {
    this.plugin = plugin;
    this.event = event;
    this.contents = contents;
  }

  @NotNull
  @Override
  public InventoryContents contents() {
    return this.contents;
  }

  @Override
  public void cancel() {
    this.event.setCancelled(true);
  }

  @Override
  public void close() {
    Bukkit.getScheduler().runTask(this.plugin, () ->
      this.contents.page().close(this.contents.player()));
  }
}
