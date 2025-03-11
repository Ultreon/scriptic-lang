package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;

@SuppressWarnings("rawtypes")
public class IsEmptyExpr extends Expr<Boolean> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Predicate> REGISTRY = new HashMap<>();
    public static final String PATTERN = "^(?<expr1>.+) is(?<inverter> not) empty$";

    static {
        IsEmptyExpr.register(String::isEmpty);
        IsEmptyExpr.register((boolean[] arr) -> arr.length == 0);
        IsEmptyExpr.register((byte[] arr) -> arr.length == 0);
        IsEmptyExpr.register((char[] arr) -> arr.length == 0);
        IsEmptyExpr.register((short[] arr) -> arr.length == 0);
        IsEmptyExpr.register((int[] arr) -> arr.length == 0);
        IsEmptyExpr.register((long[] arr) -> arr.length == 0);
        IsEmptyExpr.register((float[] arr) -> arr.length == 0);
        IsEmptyExpr.register((double[] arr) -> arr.length == 0);
        IsEmptyExpr.register((Object[] arr) -> arr.length == 0);
        IsEmptyExpr.register((String[] arr) -> arr.length == 0);
        IsEmptyExpr.register(BitSet::isEmpty);
        IsEmptyExpr.register((Optional t) -> t.isEmpty());
        IsEmptyExpr.<Collection>register(Collection::isEmpty);
    }

    private @UnknownNullability Expr<Object> expr1;
    private String inverter;

    public IsEmptyExpr() {
        super(Boolean.class);
    }

    @SafeVarargs
    private static <T> void register(Predicate<T> predicate, T... typeGetter) {
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

        inverter = matcher.group("inverter");
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        var eval1 = expr1.eval(context);

        var inverted = inverter != null && inverter.equals(" not");
        for (var p : REGISTRY.entrySet()) {
            if (p.getKey().isInstance(eval1)) {
                return p.getValue().test(eval1);
            }
        }

        return false;
    }
}
