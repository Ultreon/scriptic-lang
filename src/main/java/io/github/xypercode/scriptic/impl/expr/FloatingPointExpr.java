package io.github.xypercode.scriptic.impl.expr;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Expr;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.obj.compiled.CValue;
import io.github.xypercode.scriptic.lang.parser.Parser;

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
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                return new CValue<>(s);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
