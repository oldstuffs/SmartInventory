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

package io.github.portlek.smartui.bukkit.container;

import io.github.portlek.smartui.bukkit.container.util.Pattern;
import io.github.portlek.smartui.bukkit.container.util.SlotPos;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface SlotIterator {

    @NotNull
    Optional<Icon> get();

    @NotNull
    SlotIterator set(@NotNull Icon item);

    @NotNull
    SlotIterator previous();

    @NotNull
    SlotIterator next();

    @NotNull
    SlotIterator blacklist(int index);

    @NotNull
    SlotIterator blacklist(int row, int column);

    @NotNull
    SlotIterator blacklist(@NotNull SlotPos slotPos);

    int row();

    @NotNull
    SlotIterator row(int row);

    int column();

    @NotNull
    SlotIterator column(int column);

    @NotNull
    SlotIterator reset();

    boolean started();

    boolean ended();

    @NotNull
    SlotIterator endPosition(int row, int column);

    @NotNull
    SlotIterator endPosition(@NotNull SlotPos endPosition);

    boolean doesAllowOverride();

    @NotNull
    SlotIterator allowOverride(boolean override);

    @NotNull
    SlotIterator withPattern(@NotNull Pattern<Boolean> pattern);

    @NotNull
    SlotIterator withPattern(@NotNull Pattern<Boolean> pattern, int rowOffset, int columnOffset);

    @NotNull
    SlotIterator blacklistPattern(@NotNull Pattern<Boolean> pattern);

    @NotNull
    SlotIterator blacklistPattern(@NotNull Pattern<Boolean> pattern, int rowOffset, int columnOffset);

    enum Type {

        HORIZONTAL,
        VERTICAL

    }

}
