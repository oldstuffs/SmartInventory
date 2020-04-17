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

import io.github.portlek.smartinventory.event.ClickEvent;
import io.github.portlek.smartinventory.event.DragEvent;
import io.github.portlek.smartinventory.event.IconEvent;
import io.github.portlek.smartinventory.icon.BasicIcon;
import io.github.portlek.smartinventory.old.content.InventoryContents;
import io.github.portlek.smartinventory.target.BasicTarget;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Icon {

    @SafeVarargs
    static <T extends DragEvent> Icon draggable(@NotNull final ItemStack item,
                                                @NotNull final Consumer<T> event,
                                                @NotNull final Predicate<T>... requirements) {
        return new BasicIcon(item).target(new BasicTarget<>(event, requirements));
    }

    @SafeVarargs
    static <T extends ClickEvent> Icon clickable(@NotNull final ItemStack item,
                                                 @NotNull final Consumer<T> event,
                                                 @NotNull final Predicate<T>... requirements) {
        return new BasicIcon(item).target(new BasicTarget<>(event, requirements));
    }

    static Icon cancel(@NotNull final ItemStack item) {
        return new BasicIcon(item)
            .canClick(contents -> false);
    }

    static Icon empty() {
        return new BasicIcon(new ItemStack(Material.AIR));
    }

    @NotNull
    ItemStack calculateItem(@NotNull InventoryContents contents);

    void accept(@NotNull IconEvent event);

    @NotNull <T extends IconEvent> Icon target(@NotNull Target<T>... targets);

    @NotNull <T extends IconEvent> Icon requirement(@NotNull Predicate<T>... requirements);

    @NotNull
    Icon canSee(@NotNull Predicate<InventoryContents> predicate);

    @NotNull
    Icon canClick(@NotNull Predicate<InventoryContents> predicate);

    @NotNull
    Icon fallback(@NotNull ItemStack fallback);

    @NotNull
    boolean canSee(@NotNull InventoryContents contents);

    @NotNull
    boolean canClick(@NotNull InventoryContents contents);

}
