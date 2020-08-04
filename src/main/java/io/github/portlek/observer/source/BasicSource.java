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

package io.github.portlek.observer.source;

import io.github.portlek.observer.Source;
import io.github.portlek.observer.Target;
import java.util.Collection;
import java.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class BasicSource<T> implements Source<T> {

    private final Collection<Target<T>> targets = new Vector<>();

    @Override
    public void subscribe(@NotNull final Target<T> target) {
        if (!this.targets.contains(target)) {
            this.targets.add(target);
        }
    }

    @Override
    public void unsubscribe(@NotNull final Target<T> target) {
        this.targets.remove(target);
    }

    @Override
    public void notifyTargets(@NotNull final T argument) {
        this.targets.forEach(target -> target.update(argument));
    }

}
