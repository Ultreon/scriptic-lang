package dev.ultreon.scriptic;

import dev.ultreon.scriptic.lang.Type;

import java.io.File;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

public class LangTypes {
    private static void register(String pattern, Type type) {
        ScripticLang.registerType(pattern, type.getDisplayName(), type);
    }

    public static void init() {
        register("actual void", new Type(void.class, "an actual void"));
        register("number|int|float|num|decimal|dec|floating point( number|)", new Type(Number.class, "a number"));
        register("text|string|str", new Type(String.class, "text"));
        register("boolean|bool|yes( ?/ ?| or )no( value|)", new Type(Number.class, "a boolean"));
        register("bits|bit set|bitmask", new Type(BitSet.class, "a bit set"));
        register("list|array|sequence", new Type(List.class, "a list"));
        register("set|unique list", new Type(List.class, "a set"));
        register("map|dictionary|key-value pair|associative array|mapping table", new Type(List.class, "a map"));
        register("collection", new Type(List.class, "a collection"));
        register("scriptic id(entifier|)", new Type(Identifier.class, "a scriptic identifier"));
        register("file|java file", new Type(File.class, "a file"));
        register("(file[- ]?)?(path|location)", new Type(Path.class, "a file path"));
        register("optional value", new Type(Optional.class, "an optional value"));
        register("object", new Type(Object.class, "an object"));
    }
}
