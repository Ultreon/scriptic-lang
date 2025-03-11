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

public class IsPositiveExpr extends Expr<Boolean> {
    public static final String PATTERN = "^(?<expr1>.+) is positive$";
    private @UnknownNullability Expr<Object> expr1;

    public IsPositiveExpr() {
        super(Boolean.class);
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
        Object eval = expr1.eval(context);
        if (eval instanceof BigInteger) {
            BigInteger number = (BigInteger) eval;
            return number.signum() > 0;
        }
        if (eval instanceof BigDecimal) {
            BigDecimal number = (BigDecimal) eval;
            return number.signum() > 0;
        }
        if (eval instanceof Number) {
            Number number = (Number) eval;
            return number.doubleValue() > 0;
        }
        throw new ScriptException("Value is not a number: " + eval, getLineNr());
    }
}
