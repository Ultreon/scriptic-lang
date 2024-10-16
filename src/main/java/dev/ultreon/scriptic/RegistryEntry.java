package dev.ultreon.scriptic;

import dev.ultreon.scriptic.lang.LangObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.regex.Matcher;

public record RegistryEntry<T extends LangObject<T>>(java.util.regex.Pattern pattern, Supplier<T> factory, Class<T> type) {
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
