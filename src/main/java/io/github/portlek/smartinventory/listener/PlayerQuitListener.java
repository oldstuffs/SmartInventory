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
import io.github.portlek.smartinventory.event.PlyrQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerQuitListener implements Listener {

  @NotNull
  private final SmartInventory inventory;

  public PlayerQuitListener(@NotNull final SmartInventory inventory) {
    this.inventory = inventory;
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    this.inventory.getPage(player).ifPresent(page ->
      this.inventory.getContents(player).ifPresent(contents ->
        page.accept(new PlyrQuitEvent(contents))));
    this.inventory.stopTick(player);
    this.inventory.removePage(player);
    this.inventory.removeContent(player);
    this.inventory.removeContentByInventory(player.getOpenInventory().getTopInventory());
  }
}
