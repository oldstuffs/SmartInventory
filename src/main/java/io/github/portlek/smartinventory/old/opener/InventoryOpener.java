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

package io.github.portlek.smartinventory.old.opener;

import io.github.portlek.smartinventory.old.ClickableItem;
import io.github.portlek.smartinventory.old.SmartInventory;
import io.github.portlek.smartinventory.old.content.InventoryContents;
import io.github.portlek.smartinventory.old.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public interface InventoryOpener {

    Inventory open(SmartInventory inv, Player player);

    boolean supports(InventoryType type);

    default void fill(final Inventory handle, final InventoryContents contents,
                      final Player player) {
        final ClickableItem[][] items = contents.all();
        for (int row = 0; row < items.length; row++) {
            for (int column = 0; column < items[row].length; column++) {
                if (items[row][column] != null) {
                    handle.setItem(9 * row + column, items[row][column].getItem(player));
                }
            }
        }
    }

    /**
     * This method is used to configure the default inventory size(s)
     * for inventories supported by this opener. These values will only
     * be applied if the size is not set explicitly. (See {@link SmartInventory.Builder#size(int, int)}).
     * <p>
     * This method must return a non-null value for all supported inventory types.
     *
     * @param type inventory type
     * @return The desired default dimensions, this default implementation returns
     * (3x9) for type (ender)chest, (3x3) for dispenser and dropper and
     * (1x_sizeOfInventoryType_) for everything else.
     */
    default SlotPos defaultSize(final InventoryType type) {
        switch (type) {
            case CHEST:
            case ENDER_CHEST:
                return SlotPos.of(3, 9);
            case DISPENSER:
            case DROPPER:
                return SlotPos.of(3, 3);
            default:
                return SlotPos.of(1, type.getDefaultSize());
        }
    }

}
