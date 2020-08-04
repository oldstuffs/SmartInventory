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

import io.github.portlek.smartinventory.event.abs.CloseEvent;
import io.github.portlek.smartinventory.event.abs.OpenEvent;
import io.github.portlek.smartinventory.event.abs.PageEvent;
import io.github.portlek.smartinventory.page.BasicPage;
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

    void notifyUpdate(@NotNull InventoryContents contents);

    <T extends PageEvent> void accept(@NotNull T event);

    @NotNull
    InventoryProvided provider();

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

    void open(@NotNull Player player, int page, @NotNull Map<String, Object> properties);

    void close(@NotNull Player player);

}
