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

import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartHolder;
import io.github.portlek.smartinventory.event.PlgnDisableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * a class that represents plugin disable events.
 */
public final class PluginDisableListener implements Listener {

  /**
   * listens the plugin disable events.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onPluginDisable(final PluginDisableEvent event) {
    Bukkit.getOnlinePlayers().forEach(player -> {
      final InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
      if (!(holder instanceof SmartHolder)) {
        return;
      }
      final SmartHolder smartHolder = (SmartHolder) holder;
      final Page page = smartHolder.getPage();
      page.accept(new PlgnDisableEvent(smartHolder.getContents()));
      page.close(player);
    });
  }
}
