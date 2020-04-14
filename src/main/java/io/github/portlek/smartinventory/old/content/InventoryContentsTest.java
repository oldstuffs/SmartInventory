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

package io.github.portlek.smartinventory.old.content;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.github.portlek.smartinventory.old.ClickableItem;
import io.github.portlek.smartinventory.old.InventoryManager;
import io.github.portlek.smartinventory.old.SmartInventory;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public final class InventoryContentsTest {

    private static final ItemStack TEST_ITEM = new ItemStack(Material.DIRT);

    private static final ClickableItem TEST_CLICKABLE = ClickableItem.empty(InventoryContentsTest.TEST_ITEM);

    @Test
    public void testAddItem() {
        final SmartInventory inv = this.mockInventory(6, 9);
        final InventoryContents contents = new InventoryContents.Impl(inv, null);

        contents.add(InventoryContentsTest.TEST_CLICKABLE);

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                final Optional<ClickableItem> result = contents.get(SlotPos.of(row, column));

                if (row == 0 && column == 0) {
                    assertTrue(result.isPresent());
                    assertEquals(result.get(), InventoryContentsTest.TEST_CLICKABLE);
                } else {
                    assertFalse(result.isPresent());
                }
            }
        }
    }

    private SmartInventory mockInventory(final int rows, final int columns) {
        final InventoryManager manager = mock(InventoryManager.class);

        final SmartInventory inv = mock(SmartInventory.class);
        when(inv.getRows()).thenReturn(rows);
        when(inv.getColumns()).thenReturn(columns);
        when(inv.getManager()).thenReturn(manager);

        return inv;
    }

    @Test
    public void testAddNull() {
        final SmartInventory inv = this.mockInventory(6, 9);
        final InventoryContents contents = new InventoryContents.Impl(inv, null);

        contents.add(null);

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                final Optional<ClickableItem> result = contents.get(SlotPos.of(row, column));

                assertFalse(result.isPresent());
            }
        }
    }

    @Test
    public void test() {
        final SmartInventory inv = this.mockInventory(6, 9);
        final InventoryContents contents = new InventoryContents.Impl(inv, null);

        for (int row = 0; row < inv.getRows(); row++) {
            for (int column = 0; column < inv.getColumns(); column++) {
                assertFalse(contents.get(row, column).isPresent());
                assertFalse(contents.get(SlotPos.of(row, column)).isPresent());
            }
        }

        contents.setProperty("test", "Test String");

        assertEquals(contents.property("test"), "Test String");
        assertNull(contents.property("unknownProperty"));

        contents.add(ClickableItem.empty(InventoryContentsTest.TEST_ITEM));

        assertTrue(contents.get(0, 0).isPresent());
        assertTrue(contents.get(SlotPos.of(0, 0)).isPresent());

        assertSame(contents.get(0, 0).get().getItem(), InventoryContentsTest.TEST_ITEM);
        assertSame(contents.get(SlotPos.of(0, 0)).get().getItem(), InventoryContentsTest.TEST_ITEM);

        assertTrue(contents.firstEmpty().isPresent());
        assertEquals(contents.firstEmpty().get(), SlotPos.of(0, 1));
    }

}
