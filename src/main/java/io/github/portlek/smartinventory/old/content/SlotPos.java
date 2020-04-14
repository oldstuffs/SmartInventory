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

/**
 * Represents the position (row + column) of a slot
 * in an inventory.
 */
public final class SlotPos {

    private final int row;

    private final int column;

    public SlotPos(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

    public static SlotPos of(final int row, final int column) {
        return new SlotPos(row, column);
    }

    @Override
    public int hashCode() {
        int result = this.row;
        result = 31 * result + this.column;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final SlotPos slotPos = (SlotPos) obj;
        return this.row == slotPos.row && this.column == slotPos.column;
    }

    @Override
    public String toString() {
        return "SlotPos{" +
            "row=" + this.row +
            ", column=" + this.column +
            '}';
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

}
