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

package io.github.portlek.smartinventory.target;

import io.github.portlek.smartinventory.Target;
import io.github.portlek.smartinventory.event.abs.SmartEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class BasicTarget<T extends SmartEvent> implements Target<T> {

    @NotNull
    private final Class<T> clazz;

    @NotNull
    private final Consumer<T> consumer;

    @NotNull
    private final List<Predicate<T>> requirements;

    @SafeVarargs
    public BasicTarget(@NotNull final Class<T> clazz, @NotNull final Consumer<T> consumer,
                       @NotNull final Predicate<T>... requirements) {
        this(clazz, consumer, Arrays.asList(requirements));
    }

    @Override
    public void accept(@NotNull final T event) {
        if (this.requirements.stream().allMatch(req -> req.test(event))) {
            this.consumer.accept(event);
        }
    }

    @NotNull
    @Override
    public Class<T> getType() {
        return this.clazz;
    }

}
