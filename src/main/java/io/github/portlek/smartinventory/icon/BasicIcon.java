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
import io.github.portlek.smartinventory.Requirement;
import io.github.portlek.smartinventory.Target;
import io.github.portlek.smartinventory.old.content.InventoryContents;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class BasicIcon implements Icon {

    private final Collection<Target<? extends InventoryInteractEvent>> targets = new ArrayList<>();

    private final Collection<Requirement<? extends InventoryInteractEvent>> requirements = new ArrayList<>();

    @NotNull
    private final ItemStack item;

    @NotNull
    private Predicate<InventoryContents> cansee = contents -> true;

    @NotNull
    private Predicate<InventoryContents> canclick = contents -> true;

    @NotNull
    private ItemStack fallback = new ItemStack(Material.AIR);

    public BasicIcon(@NotNull final ItemStack item) {
        this.item = item;
    }

    @NotNull
    @Override
    public ItemStack calculateItem(@NotNull final InventoryContents contents) {
        if (this.cansee.test(contents)) {
            return this.item;
        }
        return this.fallback;
    }

    @Override
    public void accept(@NotNull final InventoryInteractEvent event) {

    }

    @SafeVarargs
    @NotNull
    @Override
    public final Icon target(@NotNull final Target<InventoryInteractEvent>... targets) {
        this.targets.addAll(Arrays.asList(targets));
        return this;
    }

    @SafeVarargs
    @NotNull
    @Override
    public final Icon requirement(@NotNull final Requirement<InventoryInteractEvent>... requirements) {
        this.requirements.addAll(Arrays.asList(requirements));
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
    public Icon canClick(@NotNull final Predicate<InventoryContents> predicate) {
        this.canclick = predicate;
        return this;
    }

    @NotNull
    @Override
    public Icon fallback(@NotNull final ItemStack fallback) {
        this.fallback = fallback;
        return this;
    }

    @NotNull
    @Override
    public boolean canSee(@NotNull final InventoryContents contents) {
        return this.cansee.test(contents);
    }

    @NotNull
    @Override
    public boolean canClick(@NotNull final InventoryContents contents) {
        return this.canclick.test(contents);
    }

}
