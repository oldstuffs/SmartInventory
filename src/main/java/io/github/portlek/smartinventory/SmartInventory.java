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

import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public final class SmartInventory {

    private static final Listener[] LISTENERS = {

    };

    private static final Queue<Plugin> PLUGIN_QUEUE = new ConcurrentLinkedQueue<>();

    private static void registerListeners(@NotNull final Plugin plugin) {
        final PluginManager manager = Bukkit.getPluginManager();
        Arrays.stream(SmartInventory.LISTENERS).forEach(listener ->
            manager.registerEvents(listener, plugin));
    }

    public void init(@NotNull final Plugin plugin) {
        if (SmartInventory.PLUGIN_QUEUE.isEmpty()) {
            SmartInventory.registerListeners(plugin);
        }
        synchronized (this) {
            SmartInventory.PLUGIN_QUEUE.add(plugin);
        }
    }

    public void onPluginDisable(@NotNull final PluginDisableEvent event) {
        if (!SmartInventory.PLUGIN_QUEUE.peek().equals(event.getPlugin())) {
            synchronized (this) {
                SmartInventory.PLUGIN_QUEUE.remove(event.getPlugin());
                return;
            }
        }
        synchronized (this) {
            SmartInventory.PLUGIN_QUEUE.poll();
        }
        Optional.ofNullable(SmartInventory.PLUGIN_QUEUE.peek())
            .filter(Plugin::isEnabled)
            .ifPresent(SmartInventory::registerListeners);
    }

}
