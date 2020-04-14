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

import io.github.portlek.smartinventory.old.ClickableItem;
import java.util.Arrays;

/**
 * <p>
 * Pagination system which lets you switch pages;
 * easily get items in the given page,
 * easily manipulate the pages and
 * check if a page is the first or the last one
 * ({@link Pagination#isFirst()} / {@link Pagination#isLast()}).
 * </p>
 *
 * <p>
 * You must start by setting the <b>items</b> and the <b>itemsPerPage</b>,
 * then you can manipulate the pages by using the
 * {@link Pagination#page(int)} /
 * {@link Pagination#first()} /
 * {@link Pagination#previous()} /
 * {@link Pagination#next()} /
 * {@link Pagination#last()}
 * methods.
 * </p>
 *
 * <p>
 * Then, when you need to get all the items of the current page,
 * either use the {@link Pagination#getPageItems()} method, or directly
 * add the items to your inventory with a SlotIterator and the
 * method {@link Pagination#addToIterator(SlotIterator)}
 * </p>
 */
public interface Pagination {

    /**
     * Gets the items of the current page.
     * <br>
     * This returns an array of the size of the items per page.
     *
     * @return the current page items
     */
    ClickableItem[] getPageItems();

    /**
     * Gets the current page.
     *
     * @return the current page
     */
    int getPage();

    /**
     * Sets the current page.
     *
     * @param page the current page
     * @return {@code this}, for chained calls
     */
    Pagination page(int page);

    /**
     * Checks if the current page is the first page.
     * <br>
     * This is equivalent to: {@code page == 0}
     *
     * @return {@code true} if this page is the first page
     */
    boolean isFirst();

    /**
     * Checks if the current page is the last page.
     * <br>
     * This is equivalent to: {@code page == itemsCount / itemsPerPage}
     *
     * @return {@code true} if this page is the last page
     */
    boolean isLast();

    /**
     * Sets the current page to the first page.
     * <br>
     * This is equivalent to: {@code page(0)}
     *
     * @return {@code this}, for chained calls
     */
    Pagination first();

    /**
     * Sets the current page to the previous page,
     * if the current page is already the first page, this do nothing.
     *
     * @return {@code this}, for chained calls
     */
    Pagination previous();

    /**
     * Sets the current page to the next page,
     * if the current page is already the last page, this do nothing.
     *
     * @return {@code this}, for chained calls
     */
    Pagination next();

    /**
     * Sets the current page to the last page.
     * <br>
     * This is equivalent to: {@code page(itemsCount / itemsPerPage)}
     *
     * @return {@code this}, for chained calls
     */
    Pagination last();

    /**
     * Adds all the current page items to the given
     * iterator.
     *
     * @param iterator the iterator
     * @return {@code this}, for chained calls
     */
    Pagination addToIterator(SlotIterator iterator);

    /**
     * Sets all the items for this Pagination.
     *
     * @param items the items
     * @return {@code this}, for chained calls
     */
    Pagination setItems(ClickableItem... items);

    /**
     * Sets the maximum amount of items per page.
     *
     * @param itemsPerPage the maximum amount of items per page
     * @return {@code this}, for chained calls
     */
    Pagination setItemsPerPage(int itemsPerPage);

    final class Impl implements Pagination {

        private int currentPage;

        private ClickableItem[] items = new ClickableItem[0];

        private int itemsPerPage = 5;

        @Override
        public ClickableItem[] getPageItems() {
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
        public Pagination addToIterator(final SlotIterator iterator) {
            for (final ClickableItem item : this.getPageItems()) {
                iterator.next().set(item);
                if (iterator.ended()) {
                    break;
                }
            }
            return this;
        }

        @Override
        public Pagination setItems(final ClickableItem... items) {
            this.items = items;
            return this;
        }

        @Override
        public Pagination setItemsPerPage(final int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
            return this;
        }

    }

}