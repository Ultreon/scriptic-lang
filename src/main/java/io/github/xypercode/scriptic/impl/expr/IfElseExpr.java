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

public class IfElseExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(if|when) (?<expr1>.+) then (?<expr2b>.+) else (?<expr3b>.+)$");
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

        final var exprCode2a = matcher.group("expr2a");
        final String exprCode2b;
        final CExpr expr2;
        if (exprCode2a != null) {
            expr2 = CExpr.ofCodeBlock(lineNr, new Parser(exprCode2a).readIndentedBlock(2));
        } else {
            exprCode2b = matcher.group("expr2b");
            expr2 = Registries.compileExpr(lineNr, new Parser(exprCode2b));
        }

        final var exprCode3a = matcher.group("expr3a");
        final String exprCode3b;
        final CExpr expr3;
        if (exprCode3a != null) {
            expr3 = CExpr.ofCodeBlock(lineNr, new Parser(exprCode3a).readIndentedBlock(2));
        } else {
            exprCode3b = matcher.group("expr3b");
            expr3 = Registries.compileExpr(lineNr, new Parser(exprCode3b));
        }

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                boolean predicate = (Boolean) expr1.eval(codeBlock, context).get();
                if (predicate) {
                    return expr2.eval(codeBlock, context);
                } else {
                    return expr3.eval(codeBlock, context);
                }
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
