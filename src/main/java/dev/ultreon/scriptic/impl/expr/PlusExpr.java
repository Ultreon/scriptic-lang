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
        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            BigDecimal a = (BigDecimal) left;
            BigDecimal b = (BigDecimal) right;
            return a.add(b);
        } else if (left instanceof BigDecimal && right instanceof BigInteger) {
            BigDecimal a = (BigDecimal) left;
            BigInteger b = (BigInteger) right;
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal && right instanceof Double) {
            BigDecimal a = (BigDecimal) left;
            Double b = (Double) right;
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal && right instanceof Float) {
            BigDecimal a = (BigDecimal) left;
            Float b = (Float) right;
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal && right instanceof Long) {
            BigDecimal a = (BigDecimal) left;
            Long b = (Long) right;
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal && right instanceof Integer) {
            BigDecimal a = (BigDecimal) left;
            Integer b = (Integer) right;
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal && right instanceof Short) {
            BigDecimal a = (BigDecimal) left;
            Short b = (Short) right;
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigDecimal && right instanceof Byte) {
            BigDecimal a = (BigDecimal) left;
            Byte b = (Byte) right;
            return a.add(new BigDecimal(b));
        } else if (left instanceof BigInteger && right instanceof BigDecimal) {
            BigInteger a = (BigInteger) left;
            BigDecimal b = (BigDecimal) right;
            return b.add(new BigDecimal(a));
        } else if (left instanceof Double && right instanceof BigDecimal) {
            Double a = (Double) left;
            BigDecimal b = (BigDecimal) right;
            return b.add(new BigDecimal(a));
        } else if (left instanceof Float && right instanceof BigDecimal) {
            Float a = (Float) left;
            BigDecimal b = (BigDecimal) right;
            return b.add(new BigDecimal(a));
        } else if (left instanceof Long && right instanceof BigDecimal) {
            Long a = (Long) left;
            BigDecimal b = (BigDecimal) right;
            return b.add(new BigDecimal(a));
        } else if (left instanceof Integer && right instanceof BigDecimal) {
            Integer a = (Integer) left;
            BigDecimal b = (BigDecimal) right;
            return b.add(new BigDecimal(a));
        } else if (left instanceof Short && right instanceof BigDecimal) {
            Short a = (Short) left;
            BigDecimal b = (BigDecimal) right;
            return b.add(new BigDecimal(a));
        } else if (left instanceof Byte && right instanceof BigDecimal) {
            Byte a = (Byte) left;
            BigDecimal b = (BigDecimal) right;
            return b.add(new BigDecimal(a));
        } else if (left instanceof BigInteger && right instanceof BigInteger) {
            BigInteger a = (BigInteger) left;
            BigInteger b = (BigInteger) right;
            return a.add(b);
        } else if (left instanceof Integer && right instanceof Integer) {
            Integer a = (Integer) left;
            Integer b = (Integer) right;
            return a + b;
        } else if (left instanceof Integer && right instanceof Short) {
            Integer a = (Integer) left;
            Short b = (Short) right;
            return a + b;
        } else if (left instanceof Integer && right instanceof Byte) {
            Integer a = (Integer) left;
            Byte b = (Byte) right;
            return a + b;
        } else if (left instanceof Short && right instanceof Integer) {
            Short a = (Short) left;
            Integer b = (Integer) right;
            return a + b;
        } else if (left instanceof Byte && right instanceof Integer) {
            Byte a = (Byte) left;
            Integer b = (Integer) right;
            return a + b;
        } else if (left instanceof Long && right instanceof Long) {
            Long a = (Long) left;
            Long b = (Long) right;
            return a + b;
        } else if (left instanceof Long && right instanceof Integer) {
            Long a = (Long) left;
            Integer b = (Integer) right;
            return a + b;
        } else if (left instanceof Long && right instanceof Short) {
            Long a = (Long) left;
            Short b = (Short) right;
            return a + b;
        } else if (left instanceof Long && right instanceof Byte) {
            Long a = (Long) left;
            Byte b = (Byte) right;
            return a + b;
        } else if (left instanceof Integer && right instanceof Long) {
            Integer a = (Integer) left;
            Long b = (Long) right;
            return a + b;
        } else if (left instanceof Short && right instanceof Long) {
            Short a = (Short) left;
            Long b = (Long) right;
            return a + b;
        } else if (left instanceof Byte && right instanceof Long) {
            Byte a = (Byte) left;
            Long b = (Long) right;
            return a + b;
        } else if (left instanceof Double && right instanceof Double) {
            Double a = (Double) left;
            Double b = (Double) right;
            return a + b;
        } else if (left instanceof Double && right instanceof Float) {
            Double a = (Double) left;
            Float b = (Float) right;
            return a + b;
        } else if (left instanceof Double && right instanceof Long) {
            Double a = (Double) left;
            Long b = (Long) right;
            return a + b;
        } else if (left instanceof Double && right instanceof Integer) {
            Double a = (Double) left;
            Integer b = (Integer) right;
            return a + b;
        } else if (left instanceof Double && right instanceof Short) {
            Double a = (Double) left;
            Short b = (Short) right;
            return a + b;
        } else if (left instanceof Double && right instanceof Byte) {
            Double a = (Double) left;
            Byte b = (Byte) right;
            return a + b;
        } else if (left instanceof Float && right instanceof Double) {
            Float a = (Float) left;
            Double b = (Double) right;
            return a + b;
        } else if (left instanceof Long && right instanceof Double) {
            Long a = (Long) left;
            Double b = (Double) right;
            return a + b;
        } else if (left instanceof Integer && right instanceof Double) {
            Integer a = (Integer) left;
            Double b = (Double) right;
            return a + b;
        } else if (left instanceof Short && right instanceof Double) {
            Short a = (Short) left;
            Double b = (Double) right;
            return a + b;
        } else if (left instanceof Byte && right instanceof Double) {
            Byte a = (Byte) left;
            Double b = (Double) right;
            return a + b;
        } else if (left instanceof Float && right instanceof Float) {
            Float a = (Float) left;
            Float b = (Float) right;
            return a + b;
        } else if (left instanceof Float && right instanceof Long) {
            Float a = (Float) left;
            Long b = (Long) right;
            return a + b;
        } else if (left instanceof Float && right instanceof Integer) {
            Float a = (Float) left;
            Integer b = (Integer) right;
            return a + b;
        } else if (left instanceof Float && right instanceof Short) {
            Float a = (Float) left;
            Short b = (Short) right;
            return a + b;
        } else if (left instanceof Float && right instanceof Byte) {
            Float a = (Float) left;
            Byte b = (Byte) right;
            return a + b;
        } else if (left instanceof Long && right instanceof Float) {
            Long a = (Long) left;
            Float b = (Float) right;
            return a + b;
        } else if (left instanceof Integer && right instanceof Float) {
            Integer a = (Integer) left;
            Float b = (Float) right;
            return a + b;
        } else if (left instanceof Short && right instanceof Float) {
            Short a = (Short) left;
            Float b = (Float) right;
            return a + b;
        } else if (left instanceof Byte && right instanceof Float) {
            Byte a = (Byte) left;
            Float b = (Float) right;
            return a + b;
        } else if (left instanceof Short && right instanceof Short) {
            Short a = (Short) left;
            Short b = (Short) right;
            return a + b;
        } else if (left instanceof Short && right instanceof Byte) {
            Short a = (Short) left;
            Byte b = (Byte) right;
            return a + b;
        } else if (left instanceof Byte && right instanceof Short) {
            Byte a = (Byte) left;
            Short b = (Short) right;
            return a + b;
        } else if (left instanceof Byte && right instanceof Byte) {
            Byte a = (Byte) left;
            Byte b = (Byte) right;
            return a + b;
        } else if (left instanceof String && right instanceof String) {
            String a = (String) left;
            String b = (String) right;
            return a + b;
        } else if (left instanceof String) {
            String a = (String) left;
            return a + right;
        } else if (right instanceof String) {
            String b = (String) right;
            return left + b;
        } else {
            throw new IllegalArgumentException("Invalid types: " + left.getClass().getName() + " and " + right.getClass().getName());
        }
    }
}
