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
import io.github.portlek.smartinventory.old.ClickableItem;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public final class PaginationTest {

    @Test
    public void test() {
        final Pagination pagination = new Pagination.Impl();

        final ClickableItem[] items = new ClickableItem[64];

        for (int i = 0; i < items.length; i++) {
            items[i] = ClickableItem.empty(new ItemStack(Material.STONE, i));
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(30);

        assertEquals(pagination.getPage(), 0);
        assertArrayEquals(Arrays.copyOfRange(items, 0, 30), pagination.getPageItems());

        pagination.previous();

        assertEquals(pagination.getPage(), 0);

        pagination.next();

        assertEquals(pagination.getPage(), 1);
        assertArrayEquals(Arrays.copyOfRange(items, 30, 60), pagination.getPageItems());

        pagination.next();

        assertEquals(pagination.getPage(), 2);
        assertArrayEquals(Arrays.copyOfRange(items, 60, 90), pagination.getPageItems());

        pagination.next();

        assertEquals(pagination.getPage(), 2);

        pagination.first();
        assertTrue(pagination.isFirst());
        assertFalse(pagination.isLast());
        assertEquals(pagination.getPage(), 0);

        pagination.last();

        assertFalse(pagination.isFirst());
        assertTrue(pagination.isLast());
        assertEquals(pagination.getPage(), 2);
    }

}
