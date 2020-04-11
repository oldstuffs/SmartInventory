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

package io.github.portlek.smartinventory.internal;

import java.util.function.Consumer;

/**
 * An event listener for clickable items inside a SmartInvs that returns a generic type. By
 * utilizing generics type, we do not need to do type-casting which in return will remove
 * ClassCastException at runtime. If there is no type provided at the time of creation, the
 * compiler will produce a warning that "GenericsType is a raw type".
 * <p>
 * If you want to suppress these warnings you can use the @SuppressWarnings("rawtypes")
 * annotation to suppress the compiler warning.
 *
 * @param <T> Generic type which should be parameterized. If not type is provided, the type
 * becomes Object.
 */
public final class InventoryListener<T> {

    private final Class<T> type;

    private final Consumer<T> consumer;

    /**
     * This constructor is used to explicitly declare the class generic type  and consumer
     * generics type,
     *
     * @param type generic type
     * @param consumer generic consumer
     */
    public InventoryListener(final Class<T> type, final Consumer<T> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    /**
     * Will insert type-casting if necessary and will provide type-checking at compile time. This
     * will ensure that no new classes are created for parameterized types. This feature is called
     *
     * @param handle Replaces the bounded type parameter T with hte first bound interface.
     * @see "<a href="https://docs.oracle.com/javase/tutorial/java/generics/erasure.html" target="_top">Type Erasure</a>".
     */
    public void accept(final T handle) {
        this.consumer.accept(handle);
    }

    /**
     * Apply generics type for return and get the type for the inventory.
     *
     * @return the clicked inventory type
     */
    public Class<T> getType() {
        return this.type;
    }

}
