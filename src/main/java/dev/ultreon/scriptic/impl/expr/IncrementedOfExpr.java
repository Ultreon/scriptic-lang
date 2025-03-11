package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.math.BigInteger;
import java.util.regex.Matcher;

public class IncrementedOfExpr extends Expr {
    public static final String PATTERN = "^incremented (value |)of (?<expr>.+)$";
    private @UnknownNullability Expr<Object> expr;

    public IncrementedOfExpr() {
        super(Number.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        var exprCode = matcher.group("expr");
        expr = Registries.compileExpr(lineNr, new Parser(exprCode));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {

    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        var eval = expr.eval(context);

        if (eval instanceof BigInteger) {
            BigInteger bigInteger = (BigInteger) eval;
            return bigInteger.add(BigInteger.ONE);
        } else if (eval instanceof Double) {
            Double doubleValue = (Double) eval;
            return doubleValue + 1;
        } else if (eval instanceof Float) {
            Float floatValue = (Float) eval;
            return floatValue + 1;
        } else if (eval instanceof Long) {
            Long longValue = (Long) eval;
            return longValue + 1;
        } else if (eval instanceof Integer) {
            Integer intValue = (Integer) eval;
            return intValue + 1;
        } else if (eval instanceof Short) {
            Short shortValue = (Short) eval;
            return shortValue + 1;
        } else if (eval instanceof Byte) {
            Byte byteValue = (Byte) eval;
            return byteValue + 1;
        }

        throw new ScriptException("Invalid type for incremented expression: " + ScripticLang.getTypeName(eval.getClass()));
    }
}
