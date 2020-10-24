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

package io.github.portlek.smartinventory.util;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * represents the position (row + column) of a slot in an inventory.
 */
public final class SlotPos {

  /**
   * the row.
   */
  private final int row;

  /**
   * the column.
   */
  private final int column;

  /**
   * ctor.
   *
   * @param row the row.
   * @param column the column.
   */
  public SlotPos(final int row, final int column) {
    this.row = row;
    this.column = column;
  }

  /**
   * creats a simple slot position instance.
   *
   * @param row the row to create.
   * @param column the column to create.
   *
   * @return a simple slot position instance.
   */
  @NotNull
  public static SlotPos of(final int row, final int column) {
    return new SlotPos(row, column);
  }

  /**
   * obtains the row row of {@code this}
   *
   * @return the row.
   */
  public int getRow() {
    return this.row;
  }

  /**
   * obtains the column row of {@code this}
   *
   * @return the column.
   */
  public int getColumn() {
    return this.column;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.row, this.column);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final SlotPos slotPos = (SlotPos) o;
    return this.row == slotPos.row && this.column == slotPos.column;
  }

  @Override
  public String toString() {
    return "SlotPos{" +
      "row=" + this.row +
      ", column=" + this.column +
      '}';
  }
}
