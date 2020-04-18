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

import io.github.portlek.smartinventory.listener.*;
import io.github.portlek.smartinventory.content.InventoryContents;
import io.github.portlek.smartinventory.opener.ChestInventoryOpener;
import io.github.portlek.smartinventory.opener.InventoryOpener;
import java.util.*;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public final class SmartInventory {

    @NotNull
    private final Plugin plugin;

    private final Map<Player, Page> pages = new HashMap<>();

    private final Map<Player, InventoryContents> contents = new HashMap<>();

    private final Map<Player, BukkitRunnable> tasks = new HashMap<>();

    private final List<InventoryOpener> defaulters = Collections.singletonList(
        new ChestInventoryOpener()
    );

    private final Collection<InventoryOpener> openers = new ArrayList<>();

    public SmartInventory(@NotNull final Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public void init() {
        Arrays.asList(
            new InventoryClickListener(this),
            new InventoryOpenListener(this),
            new InventoryCloseListener(this),
            new PlayerQuitListener(this),
            new PluginDisableListener(this),
            new InventoryDragListener(this)
        ).forEach(listener ->
            Bukkit.getPluginManager().registerEvents(listener, this.plugin()));
    }

    @NotNull
    public Plugin plugin() {
        return this.plugin;
    }

    @NotNull
    public Optional<InventoryOpener> findOpener(@NotNull final InventoryType type) {
        return Stream.of(this.openers, this.defaulters)
            .flatMap(Collection::stream)
            .filter(opener -> opener.supports(type))
            .findAny();
    }

    public void registerOpeners(@NotNull final InventoryOpener... openers) {
        this.openers.addAll(Arrays.asList(openers));
    }

    @NotNull
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
    public Optional<Page> getPage(@NotNull final Player player) {
        return Optional.ofNullable(this.pages.get(player));
    }

    @NotNull
    public Optional<InventoryContents> getContents(@NotNull final Player player) {
        return Optional.ofNullable(this.contents.get(player));
    }

    @NotNull
    public Map<Player, Page> getPages() {
        return Collections.unmodifiableMap(this.pages);
    }

    @NotNull
    public Map<Player, InventoryContents> getContents() {
        return Collections.unmodifiableMap(this.contents);
    }

    public void removePage(@NotNull final Player player) {
        this.pages.remove(player);
    }

    public void removeContent(@NotNull final Player player) {
        this.contents.remove(player);
    }

    public void clearPages() {
        this.pages.clear();
    }

    public void clearContents() {
        this.contents.clear();
    }

    public void stopTick(final Player player) {
        Optional.ofNullable(this.tasks.get(player)).ifPresent(runnable -> {
            Bukkit.getScheduler().cancelTask(runnable.getTaskId());
            this.tasks.remove(player);
        });
    }

    public void setPage(@NotNull final Player player, @NotNull final Page page) {
        this.pages.put(player, page);
    }

    public void setContents(@NotNull final Player player, @NotNull final InventoryContents contest) {
        this.contents.put(player, contest);
    }

    public void tick(@NotNull final Player player, @NotNull final Page page) {
        final BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                page.provider().tick(SmartInventory.this.contents.get(player));
            }
        };
        if (page.async()) {
            task.runTaskTimer(this.plugin, 1L, page.tick());
        } else {
            task.runTaskTimerAsynchronously(this.plugin, 1L, page.tick());
        }
        this.tasks.put(player, task);
    }

}
