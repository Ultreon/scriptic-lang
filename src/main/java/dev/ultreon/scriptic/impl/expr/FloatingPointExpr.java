package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public class FloatingPointExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<number>\\d+([,.])\\d+)$");
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
        var parser = new Parser(code);
        var s = parser.readNumber();

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                return new CValue<>(s);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
