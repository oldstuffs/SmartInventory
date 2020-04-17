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

package io.github.portlek.smartinventory.old;

import io.github.portlek.smartinventory.old.content.InventoryContents;
import io.github.portlek.smartinventory.old.internal.GeneralListener;
import io.github.portlek.smartinventory.old.internal.PlayerInvTask;
import io.github.portlek.smartinventory.old.opener.ChestInventoryOpener;
import io.github.portlek.smartinventory.old.opener.InventoryOpener;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class InventoryManager {

    private final Plugin plugin;

    private final Map<Player, SmartInventory> inventories = new HashMap<>();

    private final Map<Player, InventoryContents> contents = new HashMap<>();

    private final Map<Player, BukkitRunnable> tasks = new HashMap<>();

    private final List<InventoryOpener> defaulters = Arrays.asList(
        new ChestInventoryOpener(), new SpecialInventoryOpener()
    );

    private final Collection<InventoryOpener> openers = new ArrayList<>();

    public InventoryManager(final Plugin plgn) {
        this.plugin = plgn;
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(new GeneralListener(this), this.plugin);
    }

    public Optional<InventoryOpener> findOpener(final InventoryType type) {
        final Optional<InventoryOpener> optional = this.openers.stream()
            .filter(opener -> opener.supports(type))
            .findAny();
        if (optional.isPresent()) {
            return optional;
        }
        return this.defaulters.stream().filter(opener -> opener.supports(type)).findAny();
    }

    public void registerOpeners(final InventoryOpener... opnrs) {
        this.openers.addAll(Arrays.asList(opnrs));
    }

    public List<Player> getOpenedPlayers(final SmartInventory inv) {
        final List<Player> list = new ArrayList<>();
        this.inventories.forEach((player, playerInv) -> {
            if (inv.equals(playerInv)) {
                list.add(player);
            }
        });
        return list;
    }

    public Optional<SmartInventory> getInventory(final Player player) {
        return Optional.ofNullable(this.inventories.get(player));
    }

    public Optional<InventoryContents> getContents(final Player player) {
        return Optional.ofNullable(this.contents.get(player));
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Map<Player, SmartInventory> getInventories() {
        return Collections.unmodifiableMap(this.inventories);
    }

    public void removeInventory(final Player player) {
        this.inventories.remove(player);
    }

    public void removeContent(final Player player) {
        this.contents.remove(player);
    }

    public void clearInventories() {
        this.inventories.clear();
    }

    public void clearContents() {
        this.contents.clear();
    }

    public void cancelUpdateTask(final Player player) {
        if (this.tasks.containsKey(player)) {
            final int id = this.tasks.get(player).getTaskId();
            Bukkit.getScheduler().cancelTask(id);
            this.tasks.remove(player);
        }
    }

    void setInventory(final Player player, final SmartInventory inv) {
        if (inv == null) {
            this.inventories.remove(player);
        } else {
            this.inventories.put(player, inv);
        }
    }

    void setContents(final Player player, final InventoryContents contest) {
        if (contest == null) {
            this.contents.remove(player);
        } else {
            this.contents.put(player, contest);
        }
    }

    void scheduleUpdateTask(final Player player, final SmartInventory inv) {
        final PlayerInvTask task =
            new PlayerInvTask(player, inv.getProvider(), this.contents.get(player));
        task.runTaskTimer(this.plugin, 1L, inv.getUpdateFrequency());
        this.tasks.put(player, task);
    }

}
