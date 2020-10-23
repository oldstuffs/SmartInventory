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

import com.google.common.base.Preconditions;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class representing a pattern with arbitrary keys and values.
 *
 * @param <T> the type of the values that will be associated with the character keys.
 */
public final class Pattern<T> {

  /**
   * the mapping.
   */
  private final Map<Character, T> mapping = new HashMap<>();

  /**
   * the lines.
   */
  @NotNull
  private final String[] lines;

  /**
   * the wrap around.
   */
  private final boolean wrapAround;

  /**
   * the default value.
   */
  @Nullable
  private T defaultValue;

  /**
   * ctor.
   *
   * @param lines the lines describing the pattern.
   *
   * @throws IllegalArgumentException if the length of {@code lines} is zero.
   * @throws IllegalArgumentException if the length of a line is not equal to the length of the first line.
   * @see #Pattern(boolean, String...) to get the possibility to create a repeating pattern.
   */
  public Pattern(@NotNull final String... lines) {
    this(false, lines);
  }

  /**
   * ctor.
   *
   * @param wrapAround whether the pattern should be repeated if the.
   * @param lines the lines describing the pattern.
   *
   * @throws IllegalArgumentException if the length of {@code lines} is zero
   * @throws IllegalArgumentException if the length of a line is not equal to the length of the first line
   */
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

  /**
   * attaches an object to a character in this pattern instance.
   *
   * @param character The key character.
   * @param object The object to attach to that character.
   *
   * @return {@code this} for a builder-like usage.
   */
  @NotNull
  public Pattern<T> attach(final char character, @NotNull final T object) {
    this.mapping.put(character, object);
    return this;
  }

  /**
   * returns the object from the n-th key in this pattern.
   * if this pattern has wrapAround set to {@code true}, and the index is equals or greater than
   * the amount of individual positions in this pattern, it will continue downwards, and not wrap around sideways.
   * because of this, it could be unclear what this method does and usage is for code clarity discouraged.
   *
   * @param index The index in this pattern.
   *
   * @return The object associated with the key.
   *
   * @see #getObject(int, int) For more detailed information.
   */
  @NotNull
  public Optional<T> getObject(final int index) {
    final int count = this.getColumnCount();
    return this.getObject(index / count, index % count);
  }

  /**
   * this method is simple a shorthand to the method call {@link #getObject(int, int) getObject(slot.getRow(),
   * slot.getColumn())},
   * so all the special cases described in that method will apply to this one.
   *
   * @param slot The slot position to extract the row and column from.
   *
   * @return The object associated with the key, or the default object.
   *
   * @see #getObject(int, int) For the more detailed information.
   */
  @NotNull
  public Optional<T> getObject(@NotNull final SlotPos slot) {
    return this.getObject(slot.getRow(), slot.getColumn());
  }

  /**
   * retrieves the object associated with the key found at the row and column in this pattern, if there is no object
   * attached to that character,
   * the default object set via {@link #setDefault(Object)} is used.
   * <p>
   * if wrapAround is set to {@code true} and the row or column would be too big or small of the pattern, it will
   * wrap around and continue on from the other side, like it would be endless.
   * if not, {@link IndexOutOfBoundsException} will be thrown.
   *
   * @param row The row of the key.
   * @param column The column of the key.
   *
   * @return The object associated with the key, or the default object.
   *
   * @throws IndexOutOfBoundsException if wrapAround is {@code false} and row or column are negative or not less
   *   that the patterns dimensions.
   */
  @NotNull
  public Optional<T> getObject(final int row, final int column) {
    int rowCache = row;
    int columnCache = column;
    if (this.wrapAround) {
      rowCache %= this.getRowCount();
      if (rowCache < 0) {
        rowCache += this.getRowCount();
      }
      columnCache %= this.getColumnCount();
      if (columnCache < 0) {
        columnCache += this.getColumnCount();
      }
    } else {
      Preconditions.checkElementIndex(rowCache, this.lines.length, "The row must be between 0 and the row count");
      Preconditions.checkElementIndex(columnCache, this.lines[0].length(), "The column must be between 0 and the column size");
    }
    return Optional.ofNullable(
      this.mapping.getOrDefault(this.lines[rowCache].charAt(columnCache), this.defaultValue));
  }

  /**
   * searches through this patterns lines to find the first top-left occurrence of this key.
   * if it could not be found, the returned {@link Optional} is empty.
   *
   * @param character The character key to look for.
   *
   * @return an optional containing the slot position in this pattern, or empty if it could not be found.
   */
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

  /**
   * searches through this patterns lines to find all occurrences of this key.
   * the first position is the most top-left and the last position is the most bottom-right one.
   * <p>
   * if the key isn't contained in this pattern, the returned list will be empty.
   *
   * @param character The character key to look for.
   *
   * @return A mutable list containing all positions where that key occurs.
   */
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

  /**
   * sets a new default value, which can be null and will override the previous value if present.
   *
   * @param defaultValue The new default value.
   *
   * @return {@code this} for a builder-like usage.
   */
  @NotNull
  public Pattern<T> setDefault(@NotNull final T defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * returns the default value set via {@link #setDefault(Object)}
   *
   * @return The default value.
   */
  @NotNull
  public Optional<T> getDefaultValue() {
    return Optional.ofNullable(this.defaultValue);
  }

  /**
   * this method counts the amount of rows this pattern has based on the amount of lines provided at creation.
   *
   * @return the amount of rows.
   */
  public int getRowCount() {
    return this.lines.length;
  }

  /**
   * this method counts the amount of rows this pattern has based on the length of the lines.
   *
   * @return the amount of columns.
   */
  public int getColumnCount() {
    return this.lines[0].length();
  }

  /**
   * a simple getter for the value provided at the Patterns creation, if this pattern supports wrapAround.
   *
   * @return {@code true} if wrapAround is enabled for this instance.
   */
  public boolean isWrapAround() {
    return this.wrapAround;
  }
}
