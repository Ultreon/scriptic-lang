package io.github.xypercode.scriptic.lang;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.ScriptException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CodeContext {

    static CodeContext of(Map<Identifier, Object> properties) {
        return new CodeContextImpl(properties);
    }

    void set(Identifier name, Object value);

    @Nullable
    Object get(Identifier name);

    @Nullable
    <T> T get(Identifier name, Class<T> type);

    @NotNull
    default <T> T require(Identifier name, Class<T> type) throws ScriptException {
        var value = get(name, type);

        if (value == null) {
            throw new ScriptException("The required '" + name + "' is not available.", 0);
        }

        return value;
    }
}
