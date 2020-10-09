package io.github.boogiemonster1o1.legacyfabricbot.util;

import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nonnull;

public class IteratorIterable<T> implements Iterable<T> {
    @Nonnull
    private final Iterator<T> iterator;

    private IteratorIterable(Iterator<T> iterator) {
        this.iterator = Objects.requireNonNull(iterator);
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return this.iterator;
    }

    public static <T> IteratorIterable<T> of(Iterator<T> iterator) {
        return new IteratorIterable<>(iterator);
    }
}
