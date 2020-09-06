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

package io.github.portlek.smartui.bukkit.container;

import io.github.portlek.smartui.bukkit.container.event.abs.*;
import io.github.portlek.smartui.bukkit.container.page.BasicPage;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface Page {

    static Page build(@NotNull final SmartInventory inventory, @NotNull final InventoryProvided provided) {
        return new BasicPage(inventory, provided);
    }

    static Page build(@NotNull final SmartInventory inventory) {
        return new BasicPage(inventory);
    }

    void notifyUpdate(@NotNull InventoryContents contents);

    void notifyUpdateForAll();

    void notifyUpdateForAllById();

    <T extends PageEvent> void accept(@NotNull T event);

    @NotNull
    InventoryProvided provider();

    @NotNull
    Page provider(@NotNull InventoryProvided provided);

    @NotNull
    SmartInventory inventory();

    long tick();

    @NotNull
    Page tick(long tick);

    long startDelay();

    @NotNull
    Page startDelay(long startDelay);

    boolean async();

    @NotNull
    Page async(boolean async);

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
    default Page whenClose(@NotNull final Consumer<CloseEvent> consumer) {
        return this.whenClose(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenClose(@NotNull final Consumer<CloseEvent> consumer,
                           @NotNull final Predicate<CloseEvent>... requirements) {
        return this.target(CloseEvent.class, consumer, requirements);
    }

    @NotNull
    default Page whenOpen(@NotNull final Consumer<OpenEvent> consumer) {
        return this.whenOpen(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenOpen(@NotNull final Consumer<OpenEvent> consumer,
                          @NotNull final Predicate<OpenEvent>... requirements) {
        return this.target(OpenEvent.class, consumer, requirements);
    }

    @NotNull
    default Page whenInit(@NotNull final Consumer<InitEvent> consumer) {
        return this.whenInit(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenInit(@NotNull final Consumer<InitEvent> consumer,
                          @NotNull final Predicate<InitEvent>... requirements) {
        return this.target(InitEvent.class, consumer, requirements);
    }

    @NotNull
    default Page whenUpdate(@NotNull final Consumer<UpdateEvent> consumer) {
        return this.whenUpdate(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenUpdate(@NotNull final Consumer<UpdateEvent> consumer,
                            @NotNull final Predicate<UpdateEvent>... requirements) {
        return this.target(UpdateEvent.class, consumer, requirements);
    }

    @NotNull
    default Page whenTick(@NotNull final Consumer<TickEvent> consumer) {
        return this.whenTick(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenTick(@NotNull final Consumer<TickEvent> consumer,
                          @NotNull final Predicate<TickEvent>... requirements) {
        return this.target(TickEvent.class, consumer, requirements);
    }

    @NotNull
    default Page whenBottomClick(@NotNull final Consumer<BottomClickEvent> consumer) {
        return this.whenBottomClick(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenBottomClick(@NotNull final Consumer<BottomClickEvent> consumer,
                                 @NotNull final Predicate<BottomClickEvent>... requirements) {
        return this.target(BottomClickEvent.class, consumer, requirements);
    }

    @NotNull
    default Page whenOutsideClick(@NotNull final Consumer<OutsideClickEvent> consumer) {
        return this.whenOutsideClick(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenOutsideClick(@NotNull final Consumer<OutsideClickEvent> consumer,
                                  @NotNull final Predicate<OutsideClickEvent>... requirements) {
        return this.target(OutsideClickEvent.class, consumer, requirements);
    }

    @NotNull
    default Page whenEmptyClick(@NotNull final Consumer<PageClickEvent> consumer) {
        return this.whenEmptyClick(consumer, new Predicate[0]);
    }

    @NotNull
    default Page whenEmptyClick(@NotNull final Consumer<PageClickEvent> consumer,
                                @NotNull final Predicate<PageClickEvent>... requirements) {
        return this.target(PageClickEvent.class, consumer, requirements);
    }

    @NotNull
    default <T extends PageEvent> Page target(@NotNull final Class<T> clazz, @NotNull final Consumer<T> consumer,
                                              @NotNull final Predicate<T>... requirements) {
        return this.target(Target.from(clazz, consumer, requirements));
    }

    @NotNull <T extends PageEvent> Page target(@NotNull Target<T> target);

    @NotNull
    default Page canClose(final boolean canClose) {
        return this.canClose(event -> canClose);
    }

    @NotNull
    Page canClose(@NotNull Predicate<CloseEvent> predicate);

    boolean canClose(@NotNull CloseEvent predicate);

    boolean checkBounds(int row, int column);

    @NotNull
    default void open(@NotNull final Player player) {
        this.open(player, 0);
    }

    default void open(@NotNull final Player player, final int page) {
        this.open(player, page, Collections.emptyMap());
    }

    default void open(@NotNull final Player player, @NotNull final Map<String, Object> properties) {
        this.open(player, 0, properties);
    }

    @NotNull
    Inventory open(@NotNull Player player, int page, @NotNull Map<String, Object> properties);

    void close(@NotNull Player player);

}
