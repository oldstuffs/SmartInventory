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

package io.github.portlek.smartinventory;

import io.github.portlek.smartinventory.event.PgTickEvent;
import io.github.portlek.smartinventory.listener.*;
import io.github.portlek.smartinventory.opener.ChestInventoryOpener;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * a class that manages all smart inventories.
 */
public interface SmartInventory {

  /**
   * all listener to register.
   */
  Function<SmartInventory, List<Listener>> LISTENERS = inventory -> Arrays.asList(
    new InventoryClickListener(inventory),
    new InventoryOpenListener(inventory),
    new InventoryCloseListener(inventory),
    new PlayerQuitListener(inventory),
    new PluginDisableListener(inventory),
    new InventoryDragListener(inventory));

  /**
   * default inventory openers.
   */
  List<InventoryOpener> DEFAULT_OPENERS = Collections.singletonList(
    new ChestInventoryOpener());

  /**
   * initiates the manager.
   */
  default void init() {
    SmartInventory.LISTENERS.apply(this).forEach(listener ->
      Bukkit.getPluginManager().registerEvents(listener, this.getPlugin()));
  }

  /**
   * obtains the players that see the given page.
   *
   * @param page the page to obtain.
   *
   * @return a player list.
   */
  @NotNull
  default List<Player> getOpenedPlayers(@NotNull final Page page) {
    final List<Player> list = new ArrayList<>();
    this.getPages().forEach((player, playerInv) -> {
      if (page.equals(playerInv)) {
        list.add(player);
      }
    });
    return list;
  }

  /**
   * runs {@link InventoryProvider#update(InventoryContents)} method of the player's page.
   *
   * @param player the player to notify.
   */
  default void notifyUpdate(@NotNull final Player player) {
    this.getContents(player).ifPresent(InventoryContents::notifyUpdate);
  }

  /**
   * runs {@link InventoryProvider#update(InventoryContents)} method of the given provider's class.
   *
   * @param provider the provider to notify.
   * @param <T> type of the class.
   */
  default <T extends InventoryProvider> void notifyUpdateForAll(@NotNull final Class<T> provider) {
    this.getContents().values().stream()
      .filter(inventoryContents -> provider.equals(inventoryContents.page().provider().getClass()))
      .forEach(InventoryContents::notifyUpdate);
  }

  /**
   * runs {@link InventoryProvider#update(InventoryContents)} method of the page called the given id.
   *
   * @param id the id to find and run the update method.
   */
  default void notifyUpdateForAllById(@NotNull final String id) {
    this.getPages().values().stream()
      .filter(page -> page.id().equals(id))
      .forEach(Page::notifyUpdateForAll);
  }

  /**
   * stops the ticking of the given player.
   *
   * @param player the player to stop.
   */
  default void stopTick(@NotNull final Player player) {
    this.getTask(player).ifPresent(runnable -> {
      Bukkit.getScheduler().cancelTask(runnable.getTaskId());
      this.removeTask(player);
    });
  }

  /**
   * starts the ticking of the given player with the given page.
   *
   * @param player the player to start.
   * @param page the page to start.
   */
  default void tick(@NotNull final Player player, @NotNull final Page page) {
    final BukkitRunnable task = new BukkitRunnable() {
      @Override
      public void run() {
        SmartInventory.this.getContents(player).ifPresent(inventoryContents -> {
          page.accept(new PgTickEvent(inventoryContents));
          page.provider().tick(inventoryContents);
        });
      }
    };
    if (page.async()) {
      task.runTaskTimerAsynchronously(this.getPlugin(), page.startDelay(), page.tick());
    } else {
      task.runTaskTimer(this.getPlugin(), page.startDelay(), page.tick());
    }
    this.setTask(player, task);
  }

  /**
   * finds a {@link InventoryOpener} from the given {@link InventoryType}.
   *
   * @param type the type to find.
   *
   * @return the inventory opener from the given type.
   */
  @NotNull
  default Optional<InventoryOpener> findOpener(@NotNull final InventoryType type) {
    return Stream.of(this.getOpeners(), SmartInventory.DEFAULT_OPENERS)
      .flatMap(Collection::stream)
      .filter(opener -> opener.supports(type))
      .findAny();
  }

  /**
   * clears {@link Page}s if the given predicate returns {@code true}.
   *
   * @param predicate the predicate to check.
   */
  default void clearPages(@NotNull final Predicate<InventoryContents> predicate) {
    new HashMap<>(this.getPages()).keySet().forEach(player ->
      this.getContents(player)
        .filter(predicate)
        .ifPresent(inventoryContents ->
          this.removePage(player)));
  }

  /**
   * clears all the latest opened pages if the predicate returns {@code true}.
   *
   * @param predicate the predicate to check.
   */
  default void clearLastPages(@NotNull final Predicate<Player> predicate) {
    new HashMap<>(this.getLastPages()).forEach((player, page) -> {
      if (predicate.test(player)) {
        this.removePage(player);
      }
    });
  }

  /**
   * obtains the plugin.
   *
   * @return the plugin.
   */
  @NotNull
  Plugin getPlugin();

  /**
   * obtains inventory openers.
   *
   * @return inventory openers.
   */
  @NotNull
  Collection<InventoryOpener> getOpeners();

  /**
   * obtains the latest opened pages.
   *
   * @return the latest opened pages.
   */
  @NotNull
  Map<Player, Page> getLastPages();

  /**
   * obtains all opened {@link Page}.
   *
   * @return all the pages.
   */
  @NotNull
  Map<Player, Page> getPages();

  /**
   * obtains all {@link InventoryContents}.
   *
   * @return all the inventory contents.
   */
  @NotNull
  Map<Player, InventoryContents> getContents();

  /**
   * obtains a map that contains {@link Inventory} and {@link InventoryContents} as key and value.
   *
   * @return a map that contains inventory and contents as key and value.
   */
  @NotNull
  Map<Inventory, InventoryContents> getContentsByInventory();

  /**
   * obtains the page that seeing by the given player.
   *
   * @param player the player to obtain.
   *
   * @return a {@link Page} instance.
   */
  @NotNull
  Optional<Page> getPage(@NotNull Player player);

  /**
   * obtains the latest opened page of the given player.
   *
   * @param player the player to obtain.
   *
   * @return a {@link Page} instance.
   */
  @NotNull
  Optional<Page> getLastPage(@NotNull Player player);

  /**
   * obtains {@link InventoryContents} of the given player.
   *
   * @param player the player to obtain.
   *
   * @return an {@link InventoryContents} instance.
   */
  @NotNull
  Optional<InventoryContents> getContents(@NotNull Player player);

  /**
   * obtains {@link InventoryContents} from the given inventory.
   *
   * @param inventory the inventory to obtain.
   *
   * @return an {@link InventoryContents} instance.
   */
  @NotNull
  Optional<InventoryContents> getContentsByInventory(@NotNull Inventory inventory);

  /**
   * obtains the given player's task.
   *
   * @param player the player to obtain.
   *
   * @return a {@link BukkitRunnable} instance.
   */
  @NotNull
  Optional<BukkitRunnable> getTask(@NotNull Player player);

  /**
   * sets the given player of the page to the given page.
   *
   * @param player the player to set.
   * @param page the page to set.
   */
  void setPage(@NotNull Player player, @NotNull Page page);

  /**
   * sets the given player of the contents to the given contents.
   *
   * @param player the player to set.
   * @param contest the contest to set.
   */
  void setContents(@NotNull Player player, @NotNull InventoryContents contest);

  /**
   * sets the given inventory of the contents to the given contents.
   *
   * @param inventory the inventory to set.
   * @param contest the contest to set.
   */
  void setContentsByInventory(@NotNull Inventory inventory, @NotNull InventoryContents contest);

  /**
   * sets the given player of the ticking task to the given task.
   *
   * @param player the player to set.
   * @param task the task to set.
   */
  void setTask(@NotNull Player player, @NotNull BukkitRunnable task);

  /**
   * removes the given player's {@link Page}.
   *
   * @param player the player to remove.
   */
  void removePage(@NotNull Player player);

  /**
   * removes the given player's latest opened {@link Page}.
   *
   * @param player the player to remove.
   */
  void removeLastPage(@NotNull Player player);

  /**
   * removes the given player's {@link InventoryContents}.
   *
   * @param player the player to remove.
   */
  void removeContent(@NotNull Player player);

  /**
   * removes the given inventory's {@link InventoryContents}.
   *
   * @param inventory the inventory to remove.
   */
  void removeContentByInventory(@NotNull Inventory inventory);

  /**
   * removes given player of the ticking task.
   *
   * @param player the player to set.
   */
  void removeTask(@NotNull Player player);

  /**
   * clears all pages.
   */
  void clearPages();

  /**
   * clears all the latest opened pages.
   */
  void clearLastPages();

  /**
   * clears all contents.
   */
  void clearContents();

  /**
   * clears all contents by inventory
   */
  void clearContentsByInventory();

  /**
   * clears all tasks.
   */
  void clearTask();

  /**
   * registers the given inventory openers.
   *
   * @param openers the openers to register.
   */
  void registerOpeners(@NotNull InventoryOpener... openers);
}
