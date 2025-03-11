package dev.ultreon.scriptic.impl.expr;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface IntIterable extends Iterable<Integer> {
    @Override
    @NotNull
    IntIterator iterator();

    interface IntIterator extends Iterator<Integer> {
        @Override
        boolean hasNext();

        @Override
        @Deprecated
        default Integer next() {
            return nextInt();
        }

        int nextInt();
    }
}
