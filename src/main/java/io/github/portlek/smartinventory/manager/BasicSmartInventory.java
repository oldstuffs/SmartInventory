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

import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.InventoryOpener;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartInventory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * an implementation for {@link SmartInventory}.
 */
public final class BasicSmartInventory implements SmartInventory {

  /**
   * the latest opened pages.
   */
  private final Map<Player, Page> lastPages = new ConcurrentHashMap<>();

  /**
   * the pages.
   */
  private final Map<Player, Page> pages = new ConcurrentHashMap<>();

  /**
   * the contents.
   */
  private final Map<Player, InventoryContents> contents = new ConcurrentHashMap<>();

  /**
   * the contents by inventory.
   */
  private final Map<Inventory, InventoryContents> contentsByInventory = new ConcurrentHashMap<>();

  /**
   * the tasks.
   */
  private final Map<Player, BukkitRunnable> tasks = new ConcurrentHashMap<>();

  /**
   * the openers.
   */
  private final Collection<InventoryOpener> openers = new ArrayList<>();

  @NotNull
  private final Plugin plugin;

  public BasicSmartInventory(@NotNull final Plugin plugin) {
    this.plugin = plugin;
  }

  @NotNull
  @Override
  public Plugin getPlugin() {
    return this.plugin;
  }

  @NotNull
  @Override
  public Collection<InventoryOpener> getOpeners() {
    return Collections.unmodifiableCollection(this.openers);
  }

  @NotNull
  @Override
  public Map<Player, Page> getLastPages() {
    return Collections.unmodifiableMap(this.lastPages);
  }

  @NotNull
  @Override
  public Map<Player, Page> getPages() {
    return Collections.unmodifiableMap(this.pages);
  }

  @NotNull
  @Override
  public Map<Player, InventoryContents> getContents() {
    return Collections.unmodifiableMap(this.contents);
  }

  @NotNull
  @Override
  public Map<Inventory, InventoryContents> getContentsByInventory() {
    return Collections.unmodifiableMap(this.contentsByInventory);
  }

  @NotNull
  @Override
  public Optional<Page> getPage(@NotNull final Player player) {
    return Optional.ofNullable(this.pages.get(player));
  }

  @NotNull
  @Override
  public Optional<Page> getLastPage(@NotNull final Player player) {
    return Optional.ofNullable(this.lastPages.get(player));
  }

  @NotNull
  @Override
  public Optional<InventoryContents> getContents(@NotNull final Player player) {
    return Optional.ofNullable(this.contents.get(player));
  }

  @NotNull
  @Override
  public Optional<InventoryContents> getContentsByInventory(@NotNull final Inventory inventory) {
    return Optional.ofNullable(this.contentsByInventory.get(inventory));
  }

  @NotNull
  @Override
  public Optional<BukkitRunnable> getTask(@NotNull final Player player) {
    return Optional.ofNullable(this.tasks.get(player));
  }

  @Override
  public void setPage(@NotNull final Player player, @NotNull final Page page) {
    this.pages.put(player, page);
    this.lastPages.put(player, page);
  }

  @Override
  public void setContents(@NotNull final Player player, @NotNull final InventoryContents contest) {
    this.contents.put(player, contest);
  }

  @Override
  public void setContentsByInventory(@NotNull final Inventory inventory, @NotNull final InventoryContents contest) {
    this.contentsByInventory.put(inventory, contest);
  }

  @Override
  public void setTask(@NotNull final Player player, @NotNull final BukkitRunnable task) {
    this.tasks.put(player, task);
  }

  @Override
  public void removePage(@NotNull final Player player) {
    this.pages.remove(player);
  }

  @Override
  public void removeLastPage(@NotNull final Player player) {
    this.lastPages.remove(player);
  }

  @Override
  public void removeContent(@NotNull final Player player) {
    this.contents.remove(player);
  }

  @Override
  public void removeContentByInventory(@NotNull final Inventory inventory) {
    this.contentsByInventory.remove(inventory);
  }

  @Override
  public void removeTask(@NotNull final Player player) {
    this.tasks.remove(player);
  }

  @Override
  public void clearPages() {
    this.pages.clear();
  }

  @Override
  public void clearLastPages() {
    this.lastPages.clear();
  }

  @Override
  public void clearContents() {
    this.contents.clear();
  }

  @Override
  public void clearContentsByInventory() {
    this.contentsByInventory.clear();
  }

  @Override
  public void clearTask() {
    this.tasks.clear();
  }

  @Override
  public void registerOpeners(@NotNull final InventoryOpener... openers) {
    this.openers.addAll(Arrays.asList(openers));
  }
}
