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

import io.github.portlek.smartinventory.event.abs.*;
import io.github.portlek.smartinventory.page.BasicPage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine {@link Inventory}.
 */
public interface Page {

  /**
   * creates a simple page instance from the given parameters.
   *
   * @param inventory the inventory to create.
   * @param provider the provider to create.
   *
   * @return a simple page instance.
   */
  static Page build(@NotNull final SmartInventory inventory, @NotNull final InventoryProvider provider) {
    return new BasicPage(inventory, provider);
  }

  /**
   * creates a simple page instance from the given inventory.
   *
   * @param inventory the inventory to create.
   *
   * @return a simple page instnce.
   */
  static Page build(@NotNull final SmartInventory inventory) {
    return new BasicPage(inventory);
  }

  /**
   * adds the given consumer as a close event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenClose(@NotNull final Consumer<CloseEvent> consumer) {
    return this.whenClose(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a close event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenClose(@NotNull final Consumer<CloseEvent> consumer,
                         @NotNull final List<Predicate<CloseEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer as a open event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenOpen(@NotNull final Consumer<OpenEvent> consumer) {
    return this.whenOpen(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a open event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenOpen(@NotNull final Consumer<OpenEvent> consumer,
                        @NotNull final List<Predicate<OpenEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer as a init event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenInit(@NotNull final Consumer<InitEvent> consumer) {
    return this.whenInit(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a init event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenInit(@NotNull final Consumer<InitEvent> consumer,
                        @NotNull final List<Predicate<InitEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer as a update event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenUpdate(@NotNull final Consumer<UpdateEvent> consumer) {
    return this.whenUpdate(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a update event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenUpdate(@NotNull final Consumer<UpdateEvent> consumer,
                          @NotNull final List<Predicate<UpdateEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer as a tick event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenTick(@NotNull final Consumer<TickEvent> consumer) {
    return this.whenTick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a tick event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenTick(@NotNull final Consumer<TickEvent> consumer,
                        @NotNull final List<Predicate<TickEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer as a bottom inventory click event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenBottomClick(@NotNull final Consumer<BottomClickEvent> consumer) {
    return this.whenBottomClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a bottom inventory click event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenBottomClick(@NotNull final Consumer<BottomClickEvent> consumer,
                               @NotNull final List<Predicate<BottomClickEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer as a outside inventory click event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenOutsideClick(@NotNull final Consumer<OutsideClickEvent> consumer) {
    return this.whenOutsideClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a outside inventory click event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenOutsideClick(@NotNull final Consumer<OutsideClickEvent> consumer,
                                @NotNull final List<Predicate<OutsideClickEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer as a empty slot click event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenEmptyClick(@NotNull final Consumer<PageClickEvent> consumer) {
    return this.whenEmptyClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a empty slot click event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page whenEmptyClick(@NotNull final Consumer<PageClickEvent> consumer,
                              @NotNull final List<Predicate<PageClickEvent>> requirements) {
    return this.handle(consumer, requirements);
  }

  /**
   * adds the given consumer.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default <T extends PageEvent> Page handle(@NotNull final Consumer<T> consumer,
                                            @NotNull final List<Predicate<T>> requirements) {
    return this.handle(Handle.from(consumer, requirements));
  }

  /**
   * adds the given handle.
   *
   * @param handle the handle to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull <T extends PageEvent> Page handle(@NotNull Handle<T> handle);

  /**
   * sets the can close to the given boolean.
   *
   * @param canClose the can close to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Page canClose(final boolean canClose) {
    return this.canClose(event -> canClose);
  }

  /**
   * opens the page to the given player.
   *
   * @param player the player to open.
   */
  default void open(@NotNull final Player player) {
    this.open(player, 0);
  }

  /**
   * opens the page to the given player.
   *
   * @param player the player to open.
   * @param page the page to open.
   */
  default void open(@NotNull final Player player, final int page) {
    this.open(player, page, Collections.emptyMap());
  }

  /**
   * opens the page to the given player.
   *
   * @param player the player to open.
   * @param properties the properties to open.
   */
  default void open(@NotNull final Player player, @NotNull final Map<String, Object> properties) {
    this.open(player, 0, properties);
  }

  /**
   * checks the bounds.
   *
   * @param row the row to check.
   * @param column the column to check.
   *
   * @return {@code true} if the given row and column are correct for the page size.
   */
  default boolean checkBounds(final int row, final int column) {
    if (row >= 0) {
      return column >= 0;
    }
    if (row < this.row()) {
      return column < this.column();
    }
    return false;
  }

  /**
   * runs the {@link InventoryProvider#update(InventoryContents)} methods for all players who opened {@code this}.
   */
  default void notifyUpdateForAll() {
    this.inventory().notifyUpdateForAll(this.provider().getClass());
  }

  /**
   * runs the {@link InventoryProvider#update(InventoryContents)} methods for all players who opened {@code this}.
   */
  default void notifyUpdateForAllById() {
    this.inventory().notifyUpdateForAllById(this.id());
  }

  /**
   * runs the {@link this#provider()}'s {@link InventoryProvider#update(InventoryContents)} methods with the given
   * contents.
   *
   * @param contents the contents to update.
   */
  void notifyUpdate(@NotNull InventoryContents contents);

  /**
   * accepts the upcoming event.
   *
   * @param event the event to accept.
   * @param <T> type of the event.
   */
  <T extends PageEvent> void accept(@NotNull T event);

  /**
   * obtains the inventory provider.
   *
   * @return an inventory provider instance.
   */
  @NotNull
  InventoryProvider provider();

  /**
   * sets the provider to the given provider
   *
   * @param provider the provider to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Page provider(@NotNull InventoryProvider provider);

  /**
   * obtains the page's inventory manager.
   *
   * @return an inventory manager instance.
   */
  @NotNull
  SmartInventory inventory();

  /**
   * obtains the tick amount.
   *
   * @return the tick amount.
   */
  long tick();

  /**
   * sets the tick.
   *
   * @param tick the tick to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Page tick(long tick);

  /**
   * obtains the start delay for the task.
   *
   * @return the start delay for the task.
   */
  long startDelay();

  /**
   * sets the start delay of the task.
   *
   * @param startDelay the start delay to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Page startDelay(long startDelay);

  /**
   * obtains status of the task in terms of async.
   *
   * @return status of the task in terms of async.
   */
  boolean async();

  /**
   * sets the task's async status.
   *
   * @param async the async to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Page async(boolean async);

  /**
   * obtains the task enable.
   *
   * @return {@code true} if the task enable.
   */
  boolean tickEnable();

  @NotNull
  Page tickEnable(boolean tickEnable);

  int row();

  @NotNull
  Page row(int row);

  int column();

  @NotNull
  Page column(int column);

  @NotNull
  String title();

  @NotNull
  Page title(@NotNull String title);

  @NotNull
  Page parent(@NotNull Page parent);

  @NotNull
  Optional<Page> parent();

  @NotNull
  Page id(@NotNull String id);

  @NotNull
  String id();

  @NotNull
  Page canClose(@NotNull Predicate<CloseEvent> predicate);

  boolean canClose(@NotNull CloseEvent predicate);

  @NotNull
  Inventory open(@NotNull Player player, int page, @NotNull Map<String, Object> properties);

  void close(@NotNull Player player);
}
