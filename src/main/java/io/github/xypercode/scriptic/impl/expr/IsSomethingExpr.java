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

import java.util.regex.Pattern;

public class IsSomethingExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<expr1>.+) is( any(thing|one)?| something|(n't| not) (nothing|none|null|nil))$");
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
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var eval1 = expr1.eval(codeBlock, context);
                var valA = eval1.get();
                return new CValue<>(valA == null);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
