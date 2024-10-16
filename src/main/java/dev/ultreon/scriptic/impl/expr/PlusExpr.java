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
import java.util.regex.Matcher;

public class PlusExpr extends Expr<Object> {
    public static final String PATTERN = "^(?<expr1>[^+]+) \\+ (?<expr2>.+)$";
    private @UnknownNullability Expr<Object> leftExpr;
    private @UnknownNullability Expr<Object> rightExpr;

    public PlusExpr() {
        super(Object.class);
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

        final var exprCode2 = matcher.group("expr2");
        rightExpr = Registries.compileExpr(lineNr, new Parser(exprCode2));
    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        var left = leftExpr.eval(context);
        var right = rightExpr.eval(context);
        if (left instanceof BigDecimal a && right instanceof BigDecimal b) {
            return a.add(b);
        } else if (left instanceof BigDecimal a && right instanceof BigInteger b) {
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal a && right instanceof Double b) {
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal a && right instanceof Float b) {
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal a && right instanceof Long b) {
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal a && right instanceof Integer b) {
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal a && right instanceof Short b) {
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal a && right instanceof Byte b) {
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigInteger a && right instanceof BigDecimal b) {
            return b.add(new BigDecimal(a));
        } else if (left instanceof Double a && right instanceof BigDecimal b) {
            return b.add(new BigDecimal(a));
        } else if (left instanceof Float a && right instanceof BigDecimal b) {
            return b.add(new BigDecimal(a));
        } else if (left instanceof Long a && right instanceof BigDecimal b) {
            return b.add(new BigDecimal(a));
        } else if (left instanceof Integer a && right instanceof BigDecimal b) {
            return b.add(new BigDecimal(a));
        } else if (left instanceof Short a && right instanceof BigDecimal b) {
            return b.add(new BigDecimal(a));
        } else if (left instanceof Byte a && right instanceof BigDecimal b) {
            return b.add(new BigDecimal(a));
        } else if (left instanceof BigInteger a && right instanceof BigInteger b) {
            return a.add(b);
        } else if (left instanceof Integer a && right instanceof Integer b) {
            return a + b;
        } else if (left instanceof Integer a && right instanceof Short b) {
            return a + b;
        } else if (left instanceof Integer a && right instanceof Byte b) {
            return a + b;
        } else if (left instanceof Short a && right instanceof Integer b) {
            return a + b;
        } else if (left instanceof Byte a && right instanceof Integer b) {
            return a + b;
        } else if (left instanceof Long a && right instanceof Long b) {
            return a + b;
        } else if (left instanceof Long a && right instanceof Integer b) {
            return a + b;
        } else if (left instanceof Long a && right instanceof Short b) {
            return a + b;
        } else if (left instanceof Long a && right instanceof Byte b) {
            return a + b;
        } else if (left instanceof Integer a && right instanceof Long b) {
            return a + b;
        } else if (left instanceof Short a && right instanceof Long b) {
            return a + b;
        } else if (left instanceof Byte a && right instanceof Long b) {
            return a + b;
        } else if (left instanceof Double a && right instanceof Double b) {
            return a + b;
        } else if (left instanceof Double a && right instanceof Float b) {
            return a + b;
        } else if (left instanceof Double a && right instanceof Long b) {
            return a + b;
        } else if (left instanceof Double a && right instanceof Integer b) {
            return a + b;
        } else if (left instanceof Double a && right instanceof Short b) {
            return a + b;
        } else if (left instanceof Double a && right instanceof Byte b) {
            return a + b;
        } else if (left instanceof Float a && right instanceof Double b) {
            return a + b;
        } else if (left instanceof Long a && right instanceof Double b) {
            return a + b;
        } else if (left instanceof Integer a && right instanceof Double b) {
            return a + b;
        } else if (left instanceof Short a && right instanceof Double b) {
            return a + b;
        } else if (left instanceof Byte a && right instanceof Double b) {
            return a + b;
        } else if (left instanceof Float a && right instanceof Float b) {
            return a + b;
        } else if (left instanceof Float a && right instanceof Long b) {
            return a + b;
        } else if (left instanceof Float a && right instanceof Integer b) {
            return a + b;
        } else if (left instanceof Float a && right instanceof Short b) {
            return a + b;
        } else if (left instanceof Float a && right instanceof Byte b) {
            return a + b;
        } else if (left instanceof Long a && right instanceof Float b) {
            return a + b;
        } else if (left instanceof Integer a && right instanceof Float b) {
            return a + b;
        } else if (left instanceof Short a && right instanceof Float b) {
            return a + b;
        } else if (left instanceof Byte a && right instanceof Float b) {
            return a + b;
        } else if (left instanceof Short a && right instanceof Short b) {
            return a + b;
        } else if (left instanceof Short a && right instanceof Byte b) {
            return a + b;
        } else if (left instanceof Byte a && right instanceof Short b) {
            return a + b;
        } else if (left instanceof Byte a && right instanceof Byte b) {
            return a + b;
        } else if (left instanceof String a && right instanceof String b) {
            return a + b;
        } else if (left instanceof String a) {
            return a + right;
        } else if (right instanceof String b) {
            return left + b;
        } else {
            throw new IllegalArgumentException("Invalid types: " + left.getClass().getName() + " and " + right.getClass().getName());
        }
    }
}
