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

package io.github.portlek.smartinventory.old.util;

import static org.junit.Assert.assertEquals;
import io.github.portlek.smartinventory.old.content.SlotPos;
import org.junit.Test;

public final class PatternTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPattern() {
        new Pattern<>();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnequalColumnsPattern() {
        new Pattern<>(
            "X   X",
            "X    X",
            "X   X"
        );
    }

    @Test
    public void testRowColumnCountPattern() {
        final Pattern<String> pattern = new Pattern<>(
            "XOOOX",
            "XOXOX",
            "XOOOX"
        );

        assertEquals(3, pattern.getRowCount());
        assertEquals(5, pattern.getColumnCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeGetPattern() {
        final Pattern<String> pattern = new Pattern<>(
            "XOOX",
            "XOOX"
        );

        pattern.getObject(-1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testOversizeGetPattern() {
        final Pattern<String> pattern = new Pattern<>(
            "XOOX",
            "XOOX"
        );

        pattern.getObject(0, 4);
    }

    @Test
    public void testAttachPattern() {
        final Pattern<String> pattern = new Pattern<>(
            "XXXXXXX",
            "XOOOOOX",
            "XOOOOOX",
            "XOOOOOX",
            "XOOOOOX",
            "XXXXXXX"
        );

        pattern.setDefault("Empty");
        pattern.attach('X', "Full");

        for (int row = 0; row < pattern.getRowCount(); row++) {
            for (int column = 0; column < pattern.getColumnCount(); column++) {
                final String expected;

                if (row == 0 || row == pattern.getRowCount() - 1
                    || column == 0 || column == pattern.getColumnCount() - 1) {

                    expected = "Full";
                } else {
                    expected = "Empty";
                }

                assertEquals(expected, pattern.getObject(row, column));
                assertEquals(expected, pattern.getObject(SlotPos.of(row, column)));
            }
        }
    }

}
