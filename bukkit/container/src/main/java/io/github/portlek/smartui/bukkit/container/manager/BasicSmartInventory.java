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

package io.github.portlek.smartui.bukkit.container.manager;

import io.github.portlek.smartui.bukkit.container.*;
import io.github.portlek.smartui.bukkit.container.event.PgTickEvent;
import io.github.portlek.smartui.bukkit.container.listener.*;
import io.github.portlek.smartui.bukkit.container.opener.ChestInventoryOpener;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class BasicSmartInventory implements SmartInventory {

    private final Map<Player, Page> lastpages = new HashMap<>();

    private final Map<Player, Page> pages = new HashMap<>();

    private final Map<Player, InventoryContents> contents = new HashMap<>();

    private final Map<Inventory, InventoryContents> contentsByInventory = new HashMap<>();

    private final Map<Player, BukkitRunnable> tasks = new HashMap<>();

    private final List<InventoryOpener> defaulters = Collections.singletonList(
        new ChestInventoryOpener()
    );

    private final Collection<InventoryOpener> openers = new ArrayList<>();

    @NotNull
    @Getter
    private final Plugin plugin;

    @NotNull
    @Override
    public void init() {
        Arrays.asList(
            new InventoryClickListener(this),
            new InventoryOpenListener(this),
            new InventoryCloseListener(this),
            new PlayerQuitListener(this),
            new PluginDisableListener(this),
            new InventoryDragListener(this)
        ).forEach(listener ->
            Bukkit.getPluginManager().registerEvents(listener, this.plugin));
    }

    @NotNull
    @Override
    public Optional<InventoryOpener> findOpener(@NotNull final InventoryType type) {
        return Stream.of(this.openers, this.defaulters)
            .flatMap(Collection::stream)
            .filter(opener -> opener.supports(type))
            .findAny();
    }

    @Override
    public void registerOpeners(@NotNull final InventoryOpener... openers) {
        this.openers.addAll(Arrays.asList(openers));
    }

    @NotNull
    @Override
    public List<Player> getOpenedPlayers(@NotNull final Page inv) {
        final List<Player> list = new ArrayList<>();
        this.pages.forEach((player, playerInv) -> {
            if (inv.equals(playerInv)) {
                list.add(player);
            }
        });
        return list;
    }

    @NotNull
    @Override
    public Optional<Page> getPage(@NotNull final Player player) {
        return Optional.ofNullable(this.pages.get(player));
    }

    @NotNull
    @Override
    public Optional<Page> getLastPage(@NotNull final Player player) {
        return Optional.ofNullable(this.lastpages.get(player));
    }

    @Override
    public void notifyUpdate(@NotNull final Player player) {
        this.getContents(player).ifPresent(InventoryContents::notifyUpdate);
    }

    @Override
    public <T extends InventoryProvided> void notifyUpdateForAll(@NotNull final Class<T> provider) {
        this.contents.values().stream()
            .filter(contents -> provider.equals(contents.page().provider().getClass()))
            .forEach(InventoryContents::notifyUpdate);
    }

    @Override
    public <T extends InventoryProvided> void notifyUpdateForAllById(@NotNull final String id) {
        this.pages.values().stream()
            .filter(page -> page.id().equals(id))
            .forEach(Page::notifyUpdateForAll);
    }

    @NotNull
    @Override
    public Optional<InventoryContents> getContents(@NotNull final Player player) {
        return Optional.ofNullable(this.contents.get(player));
    }

    @NotNull
    @Override
    public Optional<InventoryContents> getContentsByInventory(@NotNull final Inventory inventory) {
        return Optional.ofNullable(this.contentsByInventory.get(inventory));
    }

    @NotNull
    @Override
    public Map<Player, Page> getPages() {
        return Collections.unmodifiableMap(this.pages);
    }

    @NotNull
    @Override
    public Map<Player, InventoryContents> getContents() {
        return Collections.unmodifiableMap(this.contents);
    }

    @NotNull
    @Override
    public Map<Inventory, InventoryContents> getContentsByInventory() {
        return Collections.unmodifiableMap(this.contentsByInventory);
    }

    @Override
    public void removePage(@NotNull final Player player) {
        this.pages.remove(player);
    }

    @Override
    public void removeLastPage(@NotNull final Player player) {
        this.lastpages.remove(player);
    }

    @Override
    public void removeContent(@NotNull final Player player) {
        this.contents.remove(player);
    }

    @Override
    public void removeContentByInventory(@NotNull final Inventory inventory) {
        this.contentsByInventory.remove(inventory);
    }

    @Override
    public void clearPages(@NotNull final Predicate<InventoryContents> predicate) {
        new HashMap<>(this.pages).keySet().forEach(player ->
            Optional.ofNullable(this.contents.get(player))
                .filter(predicate)
                .ifPresent(contents ->
                    this.pages.remove(player)));
    }

    @Override
    public void clearPages() {
        this.pages.clear();
    }

    @Override
    public void clearLastPages(@NotNull final Predicate<Player> predicate) {
        final Map<Player, Page> temp = new HashMap<>(this.lastpages);
        temp.forEach((player, page) -> {
            if (predicate.test(player)) {
                this.pages.remove(player);
            }
        });
    }

    @Override
    public void clearLastPages() {
        this.lastpages.clear();
    }

    @Override
    public void clearContents() {
        this.contents.clear();
    }

    @Override
    public void clearContentsByInventory() {
        this.contentsByInventory.clear();
    }

    @Override
    public void stopTick(final Player player) {
        Optional.ofNullable(this.tasks.get(player)).ifPresent(runnable -> {
            Bukkit.getScheduler().cancelTask(runnable.getTaskId());
            this.tasks.remove(player);
        });
    }

    @Override
    public void setPage(@NotNull final Player player, @NotNull final Page page) {
        this.pages.put(player, page);
        this.lastpages.put(player, page);
    }

    @Override
    public void setContents(@NotNull final Player player, @NotNull final InventoryContents contest) {
        this.contents.put(player, contest);
    }

    @Override
    public void setContentsByInventory(@NotNull final Inventory inventory, @NotNull final InventoryContents contest) {
        this.contentsByInventory.put(inventory, contest);
    }

    @Override
    public void tick(@NotNull final Player player, @NotNull final Page page) {
        final BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Optional.ofNullable(BasicSmartInventory.this.contents.get(player)).ifPresent(contents -> {
                    page.accept(new PgTickEvent(contents));
                    page.provider().tick(contents);
                });
            }
        };
        if (page.async()) {
            task.runTaskTimerAsynchronously(this.plugin, page.startDelay(), page.tick());
        } else {
            task.runTaskTimer(this.plugin, page.startDelay(), page.tick());
        }
        this.tasks.put(player, task);
    }

}
