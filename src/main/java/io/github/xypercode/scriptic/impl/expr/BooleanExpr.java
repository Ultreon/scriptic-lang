package io.github.xypercode.scriptic.impl.expr;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Expr;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.obj.compiled.CValue;

import java.util.regex.Pattern;

public class BooleanExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^((?<true>(true|on|yes|enable(d|)))|(?<false>(false|off|no|disable(d|))))$");
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
        boolean val;
        switch (code) {
            case "true", "on", "yes", "enable", "enabled" -> val = true;
            case "false", "off", "no", "disable", "disabled" -> val = false;
            default -> throw new IllegalArgumentException("Invalid boolean value: " + code);
        }

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                return new CValue<>(val);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
