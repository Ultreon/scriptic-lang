package dev.ultreon.scriptic;

import dev.ultreon.scriptic.lang.LangObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistryEntry<T extends LangObject<T>> {
    private final Pattern pattern;
    private final Supplier<T> factory;
    private final Class<T> type;

    public RegistryEntry(Pattern pattern, Supplier<T> factory, Class<T> type) {
        this.pattern = pattern;
        this.factory = factory;
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Supplier<T> getFactory() {
        return factory;
    }

    public @Nullable Matcher matcher(String code) {
        Matcher matcher = pattern.matcher(code);
        if (matcher.matches()) {
            return matcher;
        }

        return null;
    }

    public T create(int lineNr, Matcher matcher) throws CompileException {
        T entryObject = factory.get();
        entryObject.load(lineNr, matcher);

        return entryObject;
    }
}