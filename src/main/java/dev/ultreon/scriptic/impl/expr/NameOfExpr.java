package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;

public class NameOfExpr extends Expr<String> {
    @RegExp
    private static final String PATTERN = "^(the |)name of (?<expr>.+)$";
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Function> REGISTRY = new HashMap<>();
    public static final String PATTERN1 = "^(the |)name of (?<expr>.+)$";

    static {
        register(File::getName);
        register((Path path) -> path.getFileName().toString());
        register((Locale locale) -> locale.getDisplayName());
    }

    private @UnknownNullability Expr<Object> expr;

    public NameOfExpr() {
        super(String.class);
    }

    @SafeVarargs
    public static <T> void register(Function<? super T, String> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {        final var exprCode = matcher.group("expr");
        expr = Registries.compileExpr(lineNr, new Parser(exprCode));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull String eval(CodeContext context) throws ScriptException {
        var value = expr.eval(context);
        for (var entry : REGISTRY.entrySet()) {
            if (entry.getKey().isInstance(value)) {
                return (String) entry.getValue().apply(value);
            }
        }

        throw new IllegalArgumentException("Expected to have something that has a name, got expression: " + expr);
    }
}
