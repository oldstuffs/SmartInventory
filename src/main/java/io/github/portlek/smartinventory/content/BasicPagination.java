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

package io.github.portlek.smartinventory.content;

import io.github.portlek.smartinventory.Icon;
import io.github.portlek.smartinventory.Pagination;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

/**
 * an implementation for {@link Pagination}.
 */
public final class BasicPagination implements Pagination {

  /**
   * the current page.
   */
  private int currentPage;

  /**
   * the icons.
   */
  @NotNull
  private Icon[] icons = new Icon[0];

  /**
   * the icons per page.
   */
  private int iconsPerPage = 5;

  @NotNull
  @Override
  public Pagination first() {
    this.currentPage = 0;
    return this;
  }

  @Override
  public int getPage() {
    return this.currentPage;
  }

  @NotNull
  @Override
  public Icon[] getPageIcons() {
    return Arrays.copyOfRange(this.icons,
      this.currentPage * this.iconsPerPage,
      (this.currentPage + 1) * this.iconsPerPage);
  }

  @Override
  public boolean isFirst() {
    return this.currentPage == 0;
  }

  @Override
  public boolean isLast() {
    return this.currentPage >= (int) Math.ceil((double) this.icons.length / (double) this.iconsPerPage) - 1;
  }

  @NotNull
  @Override
  public Pagination last() {
    return this.page(this.getPageIcons().length / this.iconsPerPage);
  }

  @NotNull
  @Override
  public Pagination next() {
    if (!this.isLast()) {
      this.currentPage++;
    }
    return this;
  }

  @NotNull
  @Override
  public Pagination page(final int page) {
    this.currentPage = page;
    return this;
  }

  @NotNull
  @Override
  public Pagination previous() {
    if (!this.isFirst()) {
      this.currentPage--;
    }
    return this;
  }

  @NotNull
  @Override
  public Pagination setIcons(@NotNull final Icon... icons) {
    this.icons = icons.clone();
    return this;
  }

  @NotNull
  @Override
  public Pagination setIconsPerPage(final int iconsPerPage) {
    this.iconsPerPage = iconsPerPage;
    return this;
  }
}
