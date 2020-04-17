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

package io.github.portlek.smartinventory.icon;

import io.github.portlek.smartinventory.Icon;
import io.github.portlek.smartinventory.Target;
import io.github.portlek.smartinventory.event.IconEvent;
import io.github.portlek.smartinventory.old.content.InventoryContents;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class BasicIcon implements Icon {

    private final Collection<Target<? extends IconEvent>> targets = new ArrayList<>();

    @NotNull
    private final ItemStack item;

    @NotNull
    private Predicate<InventoryContents> cansee = contents -> true;

    @NotNull
    private Predicate<InventoryContents> canuse = contents -> true;

    @NotNull
    private ItemStack fallback = new ItemStack(Material.AIR);

    public BasicIcon(@NotNull final ItemStack item) {
        this.item = item;
    }

    @NotNull
    @Override
    public ItemStack calculateItem(@NotNull final InventoryContents contents) {
        final ItemStack calculated;
        if (this.cansee.test(contents)) {
            calculated = this.item;
        } else {
            calculated = this.fallback;
        }
        return calculated;
    }

    @Override
    public <T extends IconEvent> void accept(@NotNull final T event) {
        final InventoryContents contents = event.contents();
        if (this.cansee.test(contents) &&
            this.canuse.test(contents)) {
            this.targets.stream()
                .filter(target -> target.getType().isAssignableFrom(event.getClass()))
                .map(target -> (Target<T>) target)
                .forEach(target -> target.accept(event));
        }
    }

    @NotNull
    @Override
    public <T extends IconEvent> Icon target(@NotNull final Target<T> target) {
        this.targets.add(target);
        return this;
    }

    @NotNull
    @Override
    public Icon canSee(@NotNull final Predicate<InventoryContents> predicate) {
        this.cansee = predicate;
        return this;
    }

    @NotNull
    @Override
    public Icon canUse(@NotNull final Predicate<InventoryContents> predicate) {
        this.canuse = predicate;
        return this;
    }

    @NotNull
    @Override
    public Icon fallback(@NotNull final ItemStack fallback) {
        this.fallback = fallback;
        return this;
    }

}
