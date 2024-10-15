package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;

import java.util.regex.Pattern;

public class TheAnswerExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^the answer$");
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
        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                return new CValue<>(context.getCurrentBlock().getAnswer());
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
