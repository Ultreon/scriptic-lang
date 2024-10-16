package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.lang.parser.Parser;

@FunctionalInterface
public interface Parsable<T> {
    boolean parse(T instance, Parser input);
}
