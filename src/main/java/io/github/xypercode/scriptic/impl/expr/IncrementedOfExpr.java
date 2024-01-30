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

import java.math.BigInteger;
import java.util.regex.Pattern;

public class IncrementedOfExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^incremented (value |)of (?<expr>.+)$");
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

//        System.out.println("code[compile[logExpr]] = " + code);

        var matcher = pattern.matcher(code);
        if (!matcher.matches()) {
            throw new CompileException("Invalid code: " + code, lineNr);
        }

        var exprCode = matcher.group("expr");
        final var expr = Registries.compileExpr(lineNr, new Parser(exprCode));

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var eval = expr.eval(codeBlock, context);
                return new CValue<>(((BigInteger)eval.get()).add(BigInteger.ONE));
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
