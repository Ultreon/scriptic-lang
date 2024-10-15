package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class IsNegativeExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<expr1>.+) is finite$");
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

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                var eval1 = expr1.eval(context);
                var valA = eval1.get();
                if (valA instanceof BigInteger number) {
                    return new CValue<>(number.signum() < 0);
                }
                if (valA instanceof BigDecimal number) {
                    return new CValue<>(number.signum() < 0);
                }
                if (valA instanceof Number number) {
                    return new CValue<>(number.doubleValue() < 0);
                }
                throw new ScriptException("Value is not a number: " + valA, getLineNr());
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
