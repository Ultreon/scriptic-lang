package io.github.xypercode.scriptic.impl.expr;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.impl.CodeContexts;
import io.github.xypercode.scriptic.impl.Script;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Expr;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.obj.compiled.CValue;

import java.util.regex.Pattern;

public class VariableExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("\\{(?<expr>[A-Za-z0-9_\\-+., ()!?;]+(::[A-Za-z0-9_\\-+., ()!?;]+)*)}$");
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
            throw new IllegalArgumentException("Invalid comment (yeah, idk what happened here): " + code);
        }

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var script = context.require(CodeContexts.SCRIPT, Script.class);
                return script.get(matcher.group("expr"));
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
