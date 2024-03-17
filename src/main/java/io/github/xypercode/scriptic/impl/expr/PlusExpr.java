package io.github.xypercode.scriptic.impl.expr;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Expr;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.obj.compiled.CValue;
import io.github.xypercode.scriptic.lang.parser.Parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class PlusExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<expr1>.+) \\+ (?<expr2>.+)$");
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr the line number of the code.
     * @param code   the code.
     * @return the compiled code.
     */
    @Override
    public CExpr compile(int lineNr, String code) throws CompileException {
        var pattern = getPattern();

        var matcher = pattern.matcher(code);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }

        final var exprCode1 = matcher.group("expr1");
        final var expr1 = Registries.compileExpr(lineNr, new Parser(exprCode1));

        final var exprCode2 = matcher.group("expr2");
        final var expr2 = Registries.compileExpr(lineNr, new Parser(exprCode2));

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var eval1 = expr1.eval(codeBlock, context);
                var eval2 = expr2.eval(codeBlock, context);
                var valA = eval1.get();
                var valB = eval2.get();
                if (valA instanceof BigDecimal a && valB instanceof BigDecimal b) {
                    return new CValue<>(a.add(b));
                } else if (valA instanceof BigDecimal a && valB instanceof BigInteger b) {
                    return new CValue<>(a.add(new BigDecimal(b)));
                } else if (valA instanceof BigDecimal a && valB instanceof Double b) {
                    return new CValue<>(a.add(new BigDecimal(b)));
                } else if (valA instanceof BigDecimal a && valB instanceof Float b) {
                    return new CValue<>(a.add(new BigDecimal(b)));
                } else if (valA instanceof BigDecimal a && valB instanceof Long b) {
                    return new CValue<>(a.add(new BigDecimal(b)));
                } else if (valA instanceof BigDecimal a && valB instanceof Integer b) {
                    return new CValue<>(a.add(new BigDecimal(b)));
                } else if (valA instanceof BigDecimal a && valB instanceof Short b) {
                    return new CValue<>(a.add(new BigDecimal(b)));
                } else if (valA instanceof BigDecimal a && valB instanceof Byte b) {
                    return new CValue<>(a.add(new BigDecimal(b)));
                } else if (valA instanceof BigInteger a && valB instanceof BigDecimal b) {
                    return new CValue<>(b.add(new BigDecimal(a)));
                } else if (valA instanceof Double a && valB instanceof BigDecimal b) {
                    return new CValue<>(b.add(new BigDecimal(a)));
                } else if (valA instanceof Float a && valB instanceof BigDecimal b) {
                    return new CValue<>(b.add(new BigDecimal(a)));
                } else if (valA instanceof Long a && valB instanceof BigDecimal b) {
                    return new CValue<>(b.add(new BigDecimal(a)));
                } else if (valA instanceof Integer a && valB instanceof BigDecimal b) {
                    return new CValue<>(b.add(new BigDecimal(a)));
                } else if (valA instanceof Short a && valB instanceof BigDecimal b) {
                    return new CValue<>(b.add(new BigDecimal(a)));
                } else if (valA instanceof Byte a && valB instanceof BigDecimal b) {
                    return new CValue<>(b.add(new BigDecimal(a)));
                } else if (valA instanceof BigInteger a && valB instanceof BigInteger b) {
                    return new CValue<>(a.add(b));
                } else if (valA instanceof Integer a && valB instanceof Integer b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Integer a && valB instanceof Short b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Integer a && valB instanceof Byte b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Short a && valB instanceof Integer b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Byte a && valB instanceof Integer b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Long a && valB instanceof Long b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Long a && valB instanceof Integer b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Long a && valB instanceof Short b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Long a && valB instanceof Byte b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Integer a && valB instanceof Long b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Short a && valB instanceof Long b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Byte a && valB instanceof Long b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Double a && valB instanceof Double b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Double a && valB instanceof Float b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Double a && valB instanceof Long b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Double a && valB instanceof Integer b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Double a && valB instanceof Short b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Double a && valB instanceof Byte b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Float a && valB instanceof Double b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Long a && valB instanceof Double b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Integer a && valB instanceof Double b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Short a && valB instanceof Double b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Byte a && valB instanceof Double b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Float a && valB instanceof Float b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Float a && valB instanceof Long b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Float a && valB instanceof Integer b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Float a && valB instanceof Short b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Float a && valB instanceof Byte b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Long a && valB instanceof Float b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Integer a && valB instanceof Float b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Short a && valB instanceof Float b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Byte a && valB instanceof Float b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Short a && valB instanceof Short b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Short a && valB instanceof Byte b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Byte a && valB instanceof Short b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof Byte a && valB instanceof Byte b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof String a && valB instanceof String b) {
                    return new CValue<>(a + b);
                } else if (valA instanceof String a) {
                    return new CValue<>(a + valA);
                } else if (valB instanceof String b) {
                    return new CValue<>(valA.toString() + b);
                } else {
                    throw new IllegalArgumentException("Invalid types: " + valA.getClass().getName() + " and " + valB.getClass().getName());
                }
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
