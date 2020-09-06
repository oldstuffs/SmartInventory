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

package io.github.portlek.smartui.bukkit.container.event;

import io.github.portlek.smartui.bukkit.container.Icon;
import io.github.portlek.smartui.bukkit.container.InventoryContents;
import io.github.portlek.smartui.bukkit.container.event.abs.DragEvent;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class IcDragEvent implements DragEvent {

    @NotNull
    private final Plugin plugin;

    @NotNull
    private final InventoryDragEvent event;

    @NotNull
    private final InventoryContents contents;

    @NotNull
    private final Icon icon;

    @NotNull
    @Override
    public DragType drag() {
        return this.event.getType();
    }

    @NotNull
    @Override
    public Map<Integer, ItemStack> added() {
        return this.event.getNewItems();
    }

    @NotNull
    @Override
    public Set<Integer> slots() {
        return this.event.getInventorySlots();
    }

    @NotNull
    @Override
    public Optional<ItemStack> newcursor() {
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
        Bukkit.getScheduler().runTask(this.plugin, () ->
            this.contents.page().close(this.contents.player()));
    }

}
