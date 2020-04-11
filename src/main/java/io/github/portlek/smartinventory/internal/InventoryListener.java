/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
