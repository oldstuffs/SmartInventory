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
import io.github.portlek.smartinventory.old.InventoryManager;
import io.github.portlek.smartinventory.old.SmartInventory;
import org.junit.Test;

public final class SlotIteratorTest {

    // TODO: Improve the SlotIterator tests and add some more tests

    @Test
    public void testPreviousNext() {
        final SlotIterator iterator = this.createIterator(5, 5);

        assertEquals(0, iterator.row());
        assertEquals(0, iterator.column());

        iterator.previous();

        assertEquals(0, iterator.row());
        assertEquals(0, iterator.column());

        iterator.next();

        assertEquals(0, iterator.row());
        assertEquals(1, iterator.column());

        for (int i = 0; i < 4; i++) {
            iterator.next();
        }

        assertEquals(1, iterator.row());
        assertEquals(0, iterator.column());

        for (int i = 0; i < 4 * 5 - 1; i++) {
            iterator.next();
        }

        assertEquals(4, iterator.row());
        assertEquals(4, iterator.column());

        iterator.previous();

        assertEquals(4, iterator.row());
        assertEquals(3, iterator.column());
    }

    private SlotIterator createIterator(final int rows, final int columns) {
        final InventoryManager manager = mock(InventoryManager.class);

        final SmartInventory inv = mock(SmartInventory.class);
        when(inv.getRows()).thenReturn(rows);
        when(inv.getColumns()).thenReturn(columns);
        when(inv.getManager()).thenReturn(manager);

        final InventoryContents contents = new InventoryContents.Impl(inv, null);
        return contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
    }

    @Test
    public void testStartedEnded() {
        final SlotIterator iterator = this.createIterator(3, 9);

        assertFalse("The started() method returns true before the start", iterator.started());
        assertFalse("The ended() method returns true before the end", iterator.ended());

        iterator.previous();

        assertTrue("The started() method returns false after previous()", iterator.started());
        assertFalse("The ended() method returns true before the end", iterator.ended());

        iterator.next();

        assertTrue("The started() method returns false after next()", iterator.started());
        assertFalse("The ended() method returns true before the end", iterator.ended());

        for (int i = 0; i < 3 * 9 - 1; i++) {
            iterator.next();
        }

        assertTrue("The started() method returns false after multiple next()", iterator.started());
        assertTrue("The ended() method returns false at the end of the inventory", iterator.ended());

        iterator.next();

        assertTrue("The started() method returns false after the end of the inventory", iterator.started());
        assertTrue("The ended() method returns false after the end of the inventory", iterator.ended());

        iterator.previous();

        assertTrue("The started() method returns false after previous()", iterator.started());
        assertFalse("The ended() method returns true after previous()", iterator.ended());
    }

}
