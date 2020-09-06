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

package io.github.portlek.smartui.bukkit.container.util;

import com.google.common.base.Preconditions;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Pattern<T> {

    private final Map<Character, T> mapping = new HashMap<>();

    @NotNull
    private final String[] lines;

    private final boolean wrapAround;

    @Nullable
    private T defaultValue;

    public Pattern(@NotNull final String... lines) {
        this(false, lines);
    }

    public Pattern(final boolean wrapAround, @NotNull final String... lines) {
        Preconditions.checkArgument(lines.length > 0, "The given pattern lines must not be empty.");
        final int count = lines[0].length();
        this.lines = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];
            Preconditions.checkNotNull(line, "The given pattern line %s cannot be null.", i);
            Preconditions.checkArgument(line.length() == count,
                "The given pattern line %s does not match the first line character count.", i);
            this.lines[i] = lines[i];
        }
        this.wrapAround = wrapAround;
    }

    @NotNull
    public Optional<T> getDefaultValue() {
        return Optional.ofNullable(this.defaultValue);
    }

    @NotNull
    public Pattern<T> attach(final char character, @NotNull final T object) {
        this.mapping.put(character, object);
        return this;
    }

    @NotNull
    public Optional<T> getObject(final int index) {
        final int count = this.getColumnCount();
        return this.getObject(index / count, index % count);
    }

    public int getColumnCount() {
        return this.lines[0].length();
    }

    @NotNull
    public Optional<T> getObject(final int row, final int column) {
        int rowclone = row;
        int columnclone = column;
        if (this.wrapAround) {
            rowclone %= this.getRowCount();
            if (rowclone < 0) {
                rowclone += this.getRowCount();
            }
            columnclone %= this.getColumnCount();
            if (columnclone < 0) {
                columnclone += this.getColumnCount();
            }
        } else {
            Preconditions.checkElementIndex(rowclone, this.lines.length, "The row must be between 0 and the row count");
            Preconditions.checkElementIndex(columnclone, this.lines[0].length(), "The column must be between 0 and the column size");
        }
        return Optional.ofNullable(
            this.mapping.getOrDefault(this.lines[rowclone].charAt(columnclone), this.defaultValue));
    }

    public int getRowCount() {
        return this.lines.length;
    }

    @NotNull
    public Optional<T> getObject(@NotNull final SlotPos slot) {
        return this.getObject(slot.getRow(), slot.getColumn());
    }

    @NotNull
    public Optional<SlotPos> findKey(final char character) {
        for (int row = 0; row < this.getRowCount(); row++) {
            for (int column = 0; column < this.getColumnCount(); column++) {
                if (this.lines[row].charAt(column) == character) {
                    return Optional.of(SlotPos.of(row, column));
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    public List<SlotPos> findAllKeys(final char character) {
        final List<SlotPos> positions = new ArrayList<>();
        for (int row = 0; row < this.getRowCount(); row++) {
            for (int column = 0; column < this.getColumnCount(); column++) {
                if (this.lines[row].charAt(column) == character) {
                    positions.add(SlotPos.of(row, column));
                }
            }
        }
        return positions;
    }

    @NotNull
    public Pattern<T> setDefault(@NotNull final T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public boolean isWrapAround() {
        return this.wrapAround;
    }

}
