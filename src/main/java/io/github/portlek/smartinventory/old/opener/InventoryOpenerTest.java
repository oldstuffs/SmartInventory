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

import static org.junit.Assert.assertEquals;
import io.github.portlek.smartinventory.old.content.SlotPos;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.inventory.InventoryType;
import org.junit.Test;

public final class InventoryOpenerTest {

    @Test
    public void testDefaultSize() {
        final Map<InventoryType, SlotPos> expectedSizes = new HashMap<>();

        expectedSizes.put(InventoryType.CHEST, SlotPos.of(3, 9));
        expectedSizes.put(InventoryType.ENDER_CHEST, SlotPos.of(3, 9));
        expectedSizes.put(InventoryType.DISPENSER, SlotPos.of(3, 3));
        expectedSizes.put(InventoryType.DROPPER, SlotPos.of(3, 3));
        expectedSizes.put(InventoryType.ENCHANTING, SlotPos.of(1, 2));
        expectedSizes.put(InventoryType.BREWING, SlotPos.of(1, 4));       // this line needs to be updated to SlotPos(1, 5) in the future (new brewing stand slot)
        expectedSizes.put(InventoryType.ANVIL, SlotPos.of(1, 3));
        expectedSizes.put(InventoryType.BEACON, SlotPos.of(1, 1));
        expectedSizes.put(InventoryType.HOPPER, SlotPos.of(1, 5));
        expectedSizes.put(InventoryType.FURNACE, SlotPos.of(1, 3));
        expectedSizes.put(InventoryType.WORKBENCH, SlotPos.of(1, 10));

        final SpecialInventoryOpener opener = new SpecialInventoryOpener();

        expectedSizes.forEach((type, expectedSize) -> {
            assertEquals(expectedSize, opener.defaultSize(type));
        });
    }

}
