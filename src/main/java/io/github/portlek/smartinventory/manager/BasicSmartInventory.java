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

package io.github.portlek.smartinventory.manager;

import io.github.portlek.smartinventory.InventoryOpener;
import io.github.portlek.smartinventory.SmartInventory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * an implementation for {@link SmartInventory}.
 */
public final class BasicSmartInventory implements SmartInventory {

  /**
   * the openers.
   */
  private final Collection<InventoryOpener> openers = new ArrayList<>();

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  /**
   * the tasks.
   */
  private final Map<UUID, BukkitRunnable> tasks = new ConcurrentHashMap<>();

  static {
    try {
      Class.forName("io.github.portlek.smartinventory.event.PlgnDisableEvent");
    } catch (final ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * ctor.
   *
   * @param plugin the plugin.
   */
  public BasicSmartInventory(@NotNull final Plugin plugin) {
    this.plugin = plugin;
  }

  @NotNull
  @Override
  public Collection<InventoryOpener> getOpeners() {
    return Collections.unmodifiableCollection(this.openers);
  }

  @NotNull
  @Override
  public Plugin getPlugin() {
    return this.plugin;
  }

  @NotNull
  @Override
  public Map<UUID, BukkitRunnable> getTasks() {
    return Collections.unmodifiableMap(this.tasks);
  }

  @Override
  public void registerOpeners(@NotNull final InventoryOpener... openers) {
    this.openers.addAll(Arrays.asList(openers));
  }

  @Override
  public void removeTask(@NotNull final UUID uniqueId) {
    this.tasks.remove(uniqueId);
  }

  @Override
  public void setTask(@NotNull final UUID uniqueId, @NotNull final BukkitRunnable task) {
    this.tasks.put(uniqueId, task);
  }

  @Override
  public void unregisterOpeners(@NotNull final InventoryOpener... openers) {
    this.openers.removeAll(Arrays.asList(openers));
  }
}
