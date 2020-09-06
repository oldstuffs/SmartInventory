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

package io.github.portlek.smartui.bukkit.container.listener;

import io.github.portlek.smartui.bukkit.container.SmartInventory;
import io.github.portlek.smartui.bukkit.container.event.PlgnDisableEvent;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class PluginDisableListener implements Listener {

    static {
        PlgnDisableEvent.class.getSimpleName();
    }

    @NotNull
    private final SmartInventory inventory;

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        new HashMap<>(this.inventory.getPages()).forEach((player, page) -> {
            this.inventory.getContents(player).ifPresent(contents ->
                page.accept(new PlgnDisableEvent(contents)));
            page.close(player);
        });
        this.inventory.clearPages();
        this.inventory.clearContents();
        this.inventory.clearContentsByInventory();
    }

}
