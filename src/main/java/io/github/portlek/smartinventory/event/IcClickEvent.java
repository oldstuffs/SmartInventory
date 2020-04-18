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

package io.github.portlek.smartinventory.event;

import io.github.portlek.smartinventory.Icon;
import io.github.portlek.smartinventory.content.InventoryContents;
import io.github.portlek.smartinventory.event.abs.ClickEvent;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class IcClickEvent implements ClickEvent {

    @NotNull
    private final Plugin plugin;

    @NotNull
    private final InventoryClickEvent event;

    @NotNull
    private final InventoryContents contents;

    @NotNull
    private final Icon icon;

    public IcClickEvent(@NotNull final Plugin plugin, @NotNull final InventoryClickEvent event,
                        @NotNull final InventoryContents contents, @NotNull final Icon icon) {
        this.plugin = plugin;
        this.event = event;
        this.contents = contents;
        this.icon = icon;
    }

    @Override
    public int row() {
        return this.event.getSlot() / 9;
    }

    @Override
    public int column() {
        return this.event.getSlot() % 9;
    }

    @NotNull
    @Override
    public ClickType click() {
        return this.event.getClick();
    }

    @NotNull
    @Override
    public InventoryAction action() {
        return this.event.getAction();
    }

    @NotNull
    @Override
    public InventoryType.SlotType slot() {
        return this.event.getSlotType();
    }

    @NotNull
    @Override
    public Optional<ItemStack> cursor() {
        return Optional.ofNullable(this.event.getCursor());
    }

    @NotNull
    @Override
    public Icon icon() {
        return this.icon;
    }

    @NotNull
    @Override
    public InventoryContents contents() {
        return this.contents;
    }

    @Override
    public void cancel() {
        this.event.setCancelled(true);
    }

    @Override
    public void close() {
        Bukkit.getScheduler().runTaskLater(this.plugin, () ->
            this.contents.player().closeInventory(), 1L);
    }

}
