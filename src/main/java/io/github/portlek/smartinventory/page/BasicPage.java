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

package io.github.portlek.smartinventory.page;

import io.github.portlek.smartinventory.InventoryProvided;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.Target;
import io.github.portlek.smartinventory.event.CloseEvent;
import io.github.portlek.smartinventory.event.OpenEvent;
import io.github.portlek.smartinventory.event.PageEvent;
import io.github.portlek.smartinventory.old.content.InventoryContents;
import io.github.portlek.smartinventory.old.opener.InventoryOpener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public final class BasicPage implements Page {

    private final Collection<Target<? extends PageEvent>> targets = new ArrayList<>();

    private final Predicate<PageEvent> control = event ->
        event instanceof CloseEvent && this.canclose.test((CloseEvent) event) ||
            event instanceof OpenEvent && this.canopen.test((OpenEvent) event);

    @NotNull
    private final SmartInventory inventory;

    @NotNull
    private final InventoryProvided provided;

    @NotNull
    private final InventoryType type = InventoryType.CHEST;

    @NotNull
    private String title = "Smart Inventory";

    private int row = 1;

    private int column = 9;

    private long tick = 1L;

    private boolean async = false;

    @NotNull
    private Predicate<OpenEvent> canopen = event -> true;

    @NotNull
    private Predicate<CloseEvent> canclose = event -> true;

    public BasicPage(@NotNull final SmartInventory inventory, @NotNull final InventoryProvided provided) {
        this.inventory = inventory;
        this.provided = provided;
    }

    @Override
    public <T extends PageEvent> void accept(@NotNull final T event) {
        if (this.control.test(event)) {
            this.targets.stream()
                .filter(target -> target.getType().isAssignableFrom(event.getClass()))
                .map(target -> (Target<T>) target)
                .forEach(target -> target.accept(event));
        }
    }

    @NotNull
    @Override
    public InventoryProvided provider() {
        return this.provided;
    }

    @NotNull
    @Override
    public SmartInventory inventory() {
        return this.inventory;
    }

    @Override
    public long tick() {
        return this.tick;
    }

    @NotNull
    @Override
    public Page tick(final long tick) {
        this.tick = tick;
        return this;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    @NotNull
    @Override
    public Page async(final boolean async) {
        this.async = async;
        return this;
    }

    @Override
    public int row() {
        return this.row;
    }

    @NotNull
    @Override
    public Page row(final int row) {
        this.row = row;
        return this;
    }

    @Override
    public int column() {
        return this.column;
    }

    @NotNull
    @Override
    public Page column(final int column) {
        this.column = column;
        return this;
    }

    @NotNull
    @Override
    public String title() {
        return this.title;
    }

    @NotNull
    @Override
    public Page title(@NotNull final String title) {
        this.title = title;
        return this;
    }

    @NotNull
    @Override
    public <T extends PageEvent> Page target(@NotNull final Target<T> target) {
        this.targets.add(target);
        return this;
    }

    @NotNull
    @Override
    public Page canOpen(@NotNull final Predicate<OpenEvent> predicate) {
        this.canopen = predicate;
        return this;
    }

    @NotNull
    @Override
    public Page canClose(@NotNull final Predicate<CloseEvent> predicate) {
        this.canclose = predicate;
        return this;
    }

    @NotNull
    @Override
    public Inventory open(@NotNull final Player player, final int page, @NotNull final Map<String, Object> properties) {
        this.close(player);
        final InventoryContents contents = new InventoryContents.Impl(this, player);
        contents.pagination().page(page);
        properties.forEach(contents::setProperty);
        this.inventory.setContents(player, contents);
        this.provider().init(contents);
        final InventoryOpener opener = this.inventory.findOpener(this.type)
            .orElseThrow(() ->
                new IllegalStateException("No opener found for the inventory type " + this.type.name())
            );
        final Inventory handle = opener.open(this, player);
        this.inventory.setPage(player, this);
        this.inventory.tick(player, this);
        return handle;
    }

    @Override
    public void close(@NotNull final Player player) {
        player.closeInventory();
        this.inventory.removePage(player);
        this.inventory.removeContent(player);
        this.inventory.stopTick(player);
    }

}
