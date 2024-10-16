package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;

import java.util.regex.Matcher;

public class Type extends LangObject<Type> {
    private final Class<?> type;
    private final String name;

    public Type(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getRegistryName() {
        return Registries.TYPES.getKey(this);
    }

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {

    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        context.setLastType(this);
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isValid(String code) {
        return getPattern().matcher(code).find();
    }

    private String getName() {
        return Registries.TYPES.getKey(this);
    }

    public boolean isInstance(Object valA) {
        return type.isInstance(valA);
    }

    public String getDisplayName() {
        return name;
    }
}
