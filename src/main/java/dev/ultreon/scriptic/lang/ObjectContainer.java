package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.lang.obj.Struct;

import java.util.HashMap;
import java.util.Map;

public abstract class ObjectContainer<T extends LangObject<T>> {
    private final Map<Struct<T>, Parsable<T>> structures = new HashMap<>();

    public abstract boolean load(Struct<T> eventStruct, Parsable<T> parse);
}
