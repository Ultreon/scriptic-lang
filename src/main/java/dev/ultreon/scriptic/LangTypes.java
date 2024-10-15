package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Either;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.tuple.Pair;
import dev.ultreon.scriptic.lang.Type;

import java.io.File;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

public class LangTypes {
    public static final Type VOID = register(ScripticLang.id("void"), new Type(void.class, "actual void"));
    public static final Type NUMBER = register(ScripticLang.id("number"), new Type(Number.class, "number|int|float|num|decimal|dec|floating point( number|)"));
    public static final Type TEXT = register(ScripticLang.id("text"), new Type(Number.class, "(text|string|str)"));
    public static final Type BOOLEAN = register(ScripticLang.id("boolean"), new Type(Number.class, "(boolean|bool|yes( ?/ ?| or )no( value|))"));
    public static final Type BIT_SET = register(ScripticLang.id("bit_set"), new Type(BitSet.class, "bits|bit set|bitmask"));
    public static final Type LIST = register(ScripticLang.id("list"), new Type(List.class, "list|array|sequence"));
    public static final Type SET = register(ScripticLang.id("list"), new Type(List.class, "set|unique list"));
    public static final Type MAP = register(ScripticLang.id("map"), new Type(List.class, "map|dictionary|key-value pair|associative array|mapping table"));
    public static final Type COLLECTION = register(ScripticLang.id("collection"), new Type(List.class, "collection"));

    public static final Type IDENTIFIER = register(ScripticLang.id("identifier"), new Type(Identifier.class, "scriptic id(entifier|)"));

    public static final Type FILE = register(ScripticLang.id("file"), new Type(File.class, "file|java file"));
    public static final Type PATH = register(ScripticLang.id("path"), new Type(Path.class, "(file[- ]?)?(path|location)"));

    public static final Type OPTIONAL = register(ScripticLang.id("optional"), new Type(Optional.class, "optional value"));
    public static final Type EITHER = register(ScripticLang.id("either"), new Type(Either.class, "either value"));
    public static final Type PAIR = register(ScripticLang.id("pair"), new Type(Pair.class, "pair"));

    private static Type register(Identifier name, Type type) {
        Registries.TYPES.register(name, type);
        return type;
    }

    public static void init() {

    }
}
