package io.github.xypercode.scriptic.lang;

import com.ultreon.libs.commons.v0.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CodeContextImpl implements CodeContext {
    private final Map<Identifier, Object> properties;

    public CodeContextImpl(Map<Identifier, Object> properties) {
        this.properties = properties instanceof HashMap<?, ?> ? (HashMap<Identifier, Object>) properties : new HashMap<>(properties);
    }

    public CodeContextImpl() {
        this(new HashMap<>());
    }

    @Override
    public void set(Identifier name, Object value) {
        properties.put(name, value);
    }

    @Override
    public Object get(Identifier name) {
        return properties.get(name);
    }

    @Override
    public <T> T get(Identifier name, Class<T> type) {
        var value = properties.get(name);
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Value is not of type " + type);
        }
        return type.cast(value);
    }
}
