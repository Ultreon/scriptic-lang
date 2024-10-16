package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.intellij.lang.annotations.RegExp;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class DeleteOnExitEffect extends Effect {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();
    @RegExp
    public static final String PATTERN = "^(delete|discard|remove) on (exit|quit|shutdown) (?<expr>.+)$";

    static {
        register(File::deleteOnExit);
        register((Path path) -> path.toFile().deleteOnExit());
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
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        expr = Registries.compileExpr(lineNr, matcher.group("expr"));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        if (expr == null) return;
        var eval = expr.eval(context);

        for (var entry : REGISTRY.entrySet()) {
            if (entry.getKey().isInstance(eval)) {
                entry.getValue().accept(eval);
            }
        }

        throw new IllegalArgumentException("Expected to find something deletable, but got `" + expr);
    }
}
