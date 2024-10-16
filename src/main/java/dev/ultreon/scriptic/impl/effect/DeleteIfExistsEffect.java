package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.intellij.lang.annotations.RegExp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class DeleteIfExistsEffect extends Effect {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();
    @RegExp
    public static final String PATTERN = "^(delete|discard|remove) (?<expr>.+) if ((it )?exists|existent)$";

    static {
        register((File file) -> {
            if (file.exists()) file.deleteOnExit();
        });
        register((Path path) -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        });
    }

    private Expr expr;

    @SafeVarargs
    public static <T> void register(Consumer<? super T> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        expr = Registries.compileExpr(lineNr, matcher.group("expr"));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        if (expr == null) return;
        expr.eval(context);

        for (var entry : REGISTRY.entrySet()) {
            if (entry.getKey().isInstance(expr.eval(context))) {
                entry.getValue().accept(expr.eval(context));
            }
        }

        throw new IllegalArgumentException("Expected to find something deletable, but got " + expr);
    }
}
