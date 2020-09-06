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

package io.github.portlek.smartui.bukkit.container.content;

import io.github.portlek.smartui.bukkit.container.Icon;
import io.github.portlek.smartui.bukkit.container.Pagination;
import io.github.portlek.smartui.bukkit.container.SlotIterator;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public final class BasicPagination implements Pagination {

    private int currentPage;

    private Icon[] items = new Icon[0];

    private int itemsPerPage = 5;

    @Override
    public Icon[] getPageItems() {
        return Arrays.copyOfRange(this.items,
            this.currentPage * this.itemsPerPage,
            (this.currentPage + 1) * this.itemsPerPage);
    }

    @Override
    public int getPage() {
        return this.currentPage;
    }

    @Override
    public Pagination page(final int page) {
        this.currentPage = page;
        return this;
    }

    @Override
    public boolean isFirst() {
        return this.currentPage == 0;
    }

    @Override
    public boolean isLast() {
        final int pageCount = (int) Math.ceil((double) this.items.length / (double) this.itemsPerPage);
        return this.currentPage >= pageCount - 1;
    }

    @Override
    public Pagination first() {
        this.currentPage = 0;
        return this;
    }

    @Override
    public Pagination previous() {
        if (!this.isFirst()) {
            this.currentPage--;
        }
        return this;
    }

    @Override
    public Pagination next() {
        if (!this.isLast()) {
            this.currentPage++;
        }
        return this;
    }

    @Override
    public Pagination last() {
        this.currentPage = this.items.length / this.itemsPerPage;
        return this;
    }

    @Override
    public Pagination addToIterator(@NotNull final SlotIterator iterator) {
        for (final Icon item : this.getPageItems()) {
            iterator.next().set(item);
            if (iterator.ended()) {
                break;
            }
        }
        return this;
    }

    @Override
    public Pagination setItems(@NotNull final Icon... items) {
        this.items = items;
        return this;
    }

    @Override
    public Pagination setItemsPerPage(final int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        return this;
    }

}