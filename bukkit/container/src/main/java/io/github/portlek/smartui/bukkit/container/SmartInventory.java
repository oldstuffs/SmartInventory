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

package io.github.portlek.smartui.bukkit.container;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface SmartInventory {

    @NotNull
    void init();

    @NotNull
    Plugin getPlugin();

    @NotNull
    Optional<InventoryOpener> findOpener(@NotNull InventoryType type);

    void registerOpeners(@NotNull InventoryOpener... openers);

    @NotNull
    List<Player> getOpenedPlayers(@NotNull Page inv);

    @NotNull
    Optional<Page> getPage(@NotNull Player player);

    @NotNull
    Optional<Page> getLastPage(@NotNull Player player);

    void notifyUpdate(@NotNull Player player);

    <T extends InventoryProvided> void notifyUpdateForAll(@NotNull Class<T> provider);

    <T extends InventoryProvided> void notifyUpdateForAllById(@NotNull String id);

    @NotNull
    Optional<InventoryContents> getContents(@NotNull Player player);

    @NotNull
    Optional<InventoryContents> getContentsByInventory(@NotNull Inventory inventory);

    @NotNull
    Map<Player, Page> getPages();

    @NotNull
    Map<Player, InventoryContents> getContents();

    @NotNull
    Map<Inventory, InventoryContents> getContentsByInventory();

    void removePage(@NotNull Player player);

    void removeLastPage(@NotNull Player player);

    void removeContent(@NotNull Player player);

    void removeContentByInventory(@NotNull Inventory inventory);

    void clearPages(@NotNull Predicate<InventoryContents> predicate);

    void clearPages();

    void clearLastPages(@NotNull Predicate<Player> predicate);

    void clearLastPages();

    void clearContents();

    void clearContentsByInventory();

    void stopTick(Player player);

    void setPage(@NotNull Player player, @NotNull Page page);

    void setContents(@NotNull Player player, @NotNull InventoryContents contest);

    void setContentsByInventory(@NotNull Inventory inventory, @NotNull InventoryContents contest);

    void tick(@NotNull Player player, @NotNull Page page);

}
