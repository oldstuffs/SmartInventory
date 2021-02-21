/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
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

package io.github.portlek.smartinventory;

import io.github.portlek.smartinventory.event.abs.SmartEvent;
import io.github.portlek.smartinventory.handle.BasicHandle;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

/**
 * a class that handles and runs the given consumer after checking the requirements.
 */
public interface Handle<T extends SmartEvent> extends Consumer<T>, Type<T> {

  /**
   * creates a simple handler.
   *
   * @param clazz the class to determine the type of the event.
   * @param consumer the consumer to run.
   * @param requirements the requirements to check.
   * @param <T> type of the {@link SmartEvent}.
   *
   * @return a simple handler instance.
   */
  @NotNull
  static <T extends SmartEvent> Handle<T> from(@NotNull final Class<T> clazz, @NotNull final Consumer<T> consumer,
                                               @NotNull final List<Predicate<T>> requirements) {
    return new BasicHandle<>(clazz, consumer, requirements);
  }

  /**
   * creates a simple handler.
   *
   * @param clazz the class to determine the type of the event.
   * @param consumer the consumer to run.
   * @param requirements the requirements to check.
   * @param <T> type of the {@link SmartEvent}.
   *
   * @return a simple handler instance.
   */
  @SafeVarargs
  @NotNull
  static <T extends SmartEvent> Handle<T> from(@NotNull final Class<T> clazz, @NotNull final Consumer<T> consumer,
                                               @NotNull final Predicate<T>... requirements) {
    return Handle.from(clazz, consumer, Arrays.asList(requirements));
  }
}
