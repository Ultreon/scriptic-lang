package dev.ultreon.scriptic.lang;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.Registries;

import javax.annotation.RegEx;
import java.util.regex.Pattern;

public class Type {
    private final Class<?> type;
    private final Pattern name;

    public Type(Class<?> type, @RegEx String name) {
        this.type = type;
        this.name = Pattern.compile(name);
    }

    public Pattern getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isValid(String code) {
        return name.matcher(code).find();
    }


    private Identifier getRegistryName() {
        return Registries.TYPES.getKey(this);
    }
}
