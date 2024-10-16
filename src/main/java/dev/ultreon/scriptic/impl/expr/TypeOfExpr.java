package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class TypeOfExpr extends Expr<Object> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();
    public static final String PATTERN = "^(the |)type of (?<expr>.+)$";
    private @UnknownNullability Expr<Object> expr;

    public TypeOfExpr() {
        super(Object.class);
    }

    @SafeVarargs
    public static <T> void register(Consumer<? super T> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        final var exprCode = matcher.group("expr");
        expr = Registries.compileExpr(lineNr, new Parser(exprCode));
    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        expr.eval(context);

        for (var entry : REGISTRY.entrySet()) {
            if (entry.getKey().isInstance(expr.eval(context))) {
                entry.getValue().accept(expr.eval(context));
            }
        }

        throw new IllegalArgumentException("Can't find the type of that object, got expression: " + expr);
    }
}
