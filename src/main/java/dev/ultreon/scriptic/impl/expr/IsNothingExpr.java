package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class IsNothingExpr extends Expr<Boolean> {
    public static final String PATTERN = "^(?<expr1>.+) is( nothing| none| null| nil|(n't| not) any(thing|one)?| something)$";
    private @UnknownNullability Expr<Object> expr1;

    private static final Map<Class<?>, Predicate> REGISTRY = new HashMap<>();

    public IsNothingExpr() {
        super(Boolean.class);
    }

    @SafeVarargs
    public static <T> void register(Predicate<T> predicate, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), predicate);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        final var exprCode1 = matcher.group("expr1");
        expr1 = Registries.compileExpr(lineNr, new Parser(exprCode1));
    }

    @Override
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        var valA = expr1.eval(context);

        return isNothing(valA);
    }

    static @NotNull Boolean isNothing(Object valA) {
        var type = valA.getClass();
        if (valA == Null.NULL) return true;
        if (valA instanceof String) return valA.equals("");
        if (valA instanceof Number) return valA.equals(0);
        if (valA instanceof Collection<?>) return ((Collection<?>) valA).isEmpty();
        if (valA instanceof Iterable) return !((Iterable<?>) valA).iterator().hasNext();
        if (type.isArray()) return Array.getLength(valA) == 0;

        if (REGISTRY.containsKey(type)) return REGISTRY.get(type).test(valA);

        return false;
    }
}
