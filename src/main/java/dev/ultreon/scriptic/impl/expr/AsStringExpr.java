package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public class AsStringExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<expr>.+)( converted|) (as|to) (string|text)$");
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
            throw new CompileException("Invalid code: " + code, lineNr);
        }
        var exprCode = matcher.group("expr");
        var expr = Registries.compileExpr(lineNr, new Parser(exprCode));
        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                var eval = expr.eval(context);

                return new CValue<>(eval.get().toString());
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
