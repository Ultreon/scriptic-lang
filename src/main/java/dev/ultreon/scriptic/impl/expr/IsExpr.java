package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class IsExpr extends Expr<Boolean> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();
    public static final String PATTERN = "^(?<expr1>.+) is(?<inverter> not)? (?<expr2>.+)$";
    private @UnknownNullability Expr<Object> leftExpr;
    private @UnknownNullability Expr<Object> rightExpr;
    private String inverter;

    public IsExpr() {
        super(Boolean.class);
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
        final var exprCode1 = matcher.group("expr1");
        leftExpr = Registries.compileExpr(lineNr, new Parser(exprCode1));

        inverter = matcher.group("inverter");

        final var exprCode2 = matcher.group("expr2");
        rightExpr = Registries.compileExpr(lineNr, new Parser(exprCode2));
    }

    @Override
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        var left = leftExpr.eval(context);
        var right = rightExpr.eval(context);

        if (inverter != null && inverter.equals(" not")) {
            return !left.equals(right);
        }

        if (left instanceof Number a && right instanceof Number b) {
            if (a instanceof Double || b instanceof Double)
                return a.doubleValue() == b.doubleValue();
            if (a instanceof Float || b instanceof Float)
                return a.floatValue() == b.floatValue();
            if (a instanceof Long || b instanceof Long)
                return a.longValue() == b.longValue();
            if (a instanceof Integer || b instanceof Integer)
                return a.intValue() == b.intValue();
            if (a instanceof Short || b instanceof Short)
                return a.shortValue() == b.shortValue();
            if (a instanceof Byte || b instanceof Byte)
                return a.byteValue() == b.byteValue();
            if (a instanceof BigInteger && b instanceof BigInteger)
                return a.equals(b);
            if (a instanceof BigDecimal && b instanceof BigDecimal)
                return a.equals(b);
            if (a instanceof BigDecimal && b instanceof BigInteger)
                return a.equals(new BigDecimal((BigInteger) b));
            if (a instanceof BigInteger && b instanceof BigDecimal)
                return new BigDecimal((BigInteger) a).equals(b);
        }

        return left.equals(right);
    }
}
