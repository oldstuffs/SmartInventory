/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
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

import io.github.portlek.smartinventory.event.abs.ClickEvent;
import io.github.portlek.smartinventory.event.abs.DragEvent;
import io.github.portlek.smartinventory.event.abs.IconEvent;
import io.github.portlek.smartinventory.event.abs.SmartEvent;
import io.github.portlek.smartinventory.icon.BasicIcon;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * a class that holds the {@link ItemStack} to put the given inventory.
 */
public interface Icon {

  /**
   * an empty {@link Icon} instance.
   */
  Icon EMPTY = Icon.from(new ItemStack(Material.AIR));

  /**
   * creates a simple icon from the given {@link ItemStack} with {@link SmartEvent#cancel()} interaction.
   *
   * @param item the item to create.
   *
   * @return a simple icon instance.
   */
  @NotNull
  static Icon cancel(@NotNull final ItemStack item) {
    return Icon.from(item)
      .whenInteract(SmartEvent::cancel);
  }

  /**
   * creates a simple icon from the given {@link ItemStack} with a {@link ClickEvent}.
   *
   * @param item the item to create.
   * @param consumer the consumer to run.
   * @param requirements the requirements to check.
   *
   * @return a simple icon instance.
   */
  @SafeVarargs
  @NotNull
  static Icon click(@NotNull final ItemStack item, @NotNull final Consumer<ClickEvent> consumer,
                    @NotNull final Predicate<ClickEvent>... requirements) {
    return Icon.from(item)
      .whenClick(consumer, Arrays.asList(requirements));
  }

  /**
   * creates a simple icon from the given {@link ItemStack} with a {@link DragEvent}.
   *
   * @param item the item to create.
   * @param consumer the consumer to run.
   * @param requirements the requirements to check.
   *
   * @return a simple icon instance.
   */
  @SafeVarargs
  @NotNull
  static Icon drag(@NotNull final ItemStack item, @NotNull final Consumer<DragEvent> consumer,
                   @NotNull final Predicate<DragEvent>... requirements) {
    return Icon.from(item)
      .whenDrag(consumer, Arrays.asList(requirements));
  }

  /**
   * creates a simple icon from the given {@link ItemStack}.
   *
   * @param item the item to create.
   *
   * @return a simple icon instance.
   */
  @NotNull
  static Icon from(@NotNull final ItemStack item) {
    return new BasicIcon(item);
  }

  /**
   * accepts the upcoming event for all of the handles.
   *
   * @param event the event to accept.
   * @param <T> type of the event.
   */
  <T extends IconEvent> void accept(@NotNull T event);

  /**
   * calculates and returns the item of the icon.
   * tests the {@code canSee} with the given contents, and if it returns {@code true},
   * returns {@link #getItem()} else, returns the fallback.
   *
   * @param contents the contents to calculate.
   *
   * @return the calculated item.
   */
  @NotNull
  ItemStack calculateItem(@NotNull InventoryContents contents);

  /**
   * sets the canSee value of the icon to the given predicate.
   *
   * @param predicate the predicate to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Icon canSee(@NotNull Predicate<InventoryContents> predicate);

  /**
   * sets the canUse value of the icon to the given predicate.
   *
   * @param predicate the predicate to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Icon canUse(@NotNull Predicate<InventoryContents> predicate);

  /**
   * sets the fallback item of the icon to the given item.
   *
   * @param fallback the fallback to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Icon fallback(@NotNull ItemStack fallback);

  /**
   * obtains the icon's {@link ItemStack}.
   *
   * @return the icon's item.
   */
  @NotNull
  ItemStack getItem();

  /**
   * add the given event and requirements to the icon's handles.
   *
   * @param clazz the class to determine the type of the event.
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   * @param <T> type of the event.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default <T extends IconEvent> Icon handle(@NotNull final Class<T> clazz, @NotNull final Consumer<T> consumer,
                                            @NotNull final List<Predicate<T>> requirements) {
    return this.handle(Handle.from(clazz, consumer, requirements));
  }

  /**
   * adds the given handle into the icon's handle list.
   *
   * @param handle the handle to add.
   * @param <T> type of the event.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull <T extends IconEvent> Icon handle(@NotNull Handle<T> handle);

  /**
   * adds all the given handles into the icon's handle list.
   *
   * @param handles the handles to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Icon handles(@NotNull Collection<Handle<? extends IconEvent>> handles);

  /**
   * sets the item of the icon to the given item.
   *
   * @param item the item to set.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  Icon item(@NotNull ItemStack item);

  /**
   * adds the given {@link ClickEvent} to the icon's handles.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Icon whenClick(@NotNull final Consumer<ClickEvent> consumer) {
    return this.whenClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given {@link ClickEvent} with the requirements to the icon's handles.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Icon whenClick(@NotNull final Consumer<ClickEvent> consumer,
                         @NotNull final List<Predicate<ClickEvent>> requirements) {
    return this.handle(ClickEvent.class, consumer, requirements);
  }

  /**
   * adds the given {@link DragEvent} to the icon's handles.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Icon whenDrag(@NotNull final Consumer<DragEvent> consumer) {
    return this.whenDrag(consumer, Collections.emptyList());
  }

  /**
   * adds the given {@link DragEvent} with the requirements to the icon's handles.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Icon whenDrag(@NotNull final Consumer<DragEvent> consumer,
                        @NotNull final List<Predicate<DragEvent>> requirements) {
    return this.handle(DragEvent.class, consumer, requirements);
  }

  /**
   * adds the given {@link IconEvent} to the icon's handles.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Icon whenInteract(@NotNull final Consumer<IconEvent> consumer) {
    return this.whenInteract(consumer, Collections.emptyList());
  }

  /**
   * adds the given {@link IconEvent} with the requirements to the icon's handles.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  default Icon whenInteract(@NotNull final Consumer<IconEvent> consumer,
                            @NotNull final List<Predicate<IconEvent>> requirements) {
    return this.handle(IconEvent.class, consumer, requirements);
  }
}
