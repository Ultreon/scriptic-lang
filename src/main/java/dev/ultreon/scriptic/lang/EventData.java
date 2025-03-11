package dev.ultreon.scriptic.lang;


import dev.ultreon.scriptic.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EventData {
    private static final Map<Identifier, Object> properties = new HashMap<>();

    public <T> T get(Identifier name, Class<T> type) {
        var value = properties.get(name);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new ClassCastException("Cannot cast " + value.getClass() + " to " + type);
    }

    public <T> EventData set(Identifier name, T value) {
        properties.put(name, value);
        return this;
    }
}
