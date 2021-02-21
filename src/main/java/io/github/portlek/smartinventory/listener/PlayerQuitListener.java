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
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents player quit listeners.
 */
public final class PlayerQuitListener implements Listener {

  /**
   * the stop tick function.
   */
  @NotNull
  private final Consumer<UUID> stopTickFunction;

  /**
   * ctor.
   *
   * @param stopTickFunction the stop tick function.
   */
  public PlayerQuitListener(@NotNull final Consumer<UUID> stopTickFunction) {
    this.stopTickFunction = stopTickFunction;
  }

  /**
   * listens the player quit event.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    SmartInventory.getHolder(event.getPlayer()).ifPresent(holder -> {
      holder.getPage().accept(new PlyrQuitEvent(holder.getContents()));
      this.stopTickFunction.accept(event.getPlayer().getUniqueId());
    });
  }
}
