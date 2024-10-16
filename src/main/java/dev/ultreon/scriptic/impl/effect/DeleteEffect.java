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

public class DeleteEffect extends Effect {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();

    @RegExp
    public static final String PATTERN = "^(delete|discard|dispose|remove) (?<expr>.+)$";

    static {
        register(File::delete);
        register((Path path) -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new IllegalStateException("Could not delete " + path + ": " + e.getMessage());
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
     * @param matcher the matcher.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        expr = Registries.compileExpr(lineNr, matcher.group("expr"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(CodeContext context) throws ScriptException {
        if (expr == null) return;

        for (var entry : REGISTRY.entrySet()) {
            Object eval = expr.eval(context);
            if (entry.getKey().isInstance(eval)) {
                entry.getValue().accept(eval);
            }
        }

        throw new IllegalArgumentException("Expected to find something deletable, but got " + expr);
    }

}
