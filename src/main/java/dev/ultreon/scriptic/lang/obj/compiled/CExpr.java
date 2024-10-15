package dev.ultreon.scriptic.lang.obj.compiled;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.CompiledCode;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public abstract class CExpr extends CompiledCode {
    private final Expr base;
    private final String code;
    private final int lineNr;

    protected CExpr(Expr base, String code, int lineNr) {
        this.base = base;
        this.code = code;
        this.lineNr = lineNr;
    }

    public static CExpr ofCodeBlock(int lineNr, String code) throws CompileException {
        return ofCodeBlock(lineNr, code, null);
    }

    public static CExpr ofCodeBlock(int lineNr, String code, CodeBlock parent) throws CompileException {
        var parser = new Parser(code);
        List<CExpr> expressions = new ArrayList<>();

        var block = new CExpr(null, code, lineNr) {
            /**
             * Evaluates this expression.
             *
             * @param context   the context to evaluate this expression in.
             * @return the result of this expression.
             */
            @Override
            public CNone calc(CodeContext context) throws ScriptException {
                for (var expr : expressions) {
                    expr.eval(context);
                }
                return CNone.INSTANCE;
            }
        };

        while (!parser.isEOF()) {
            var exprFor = Registries.compileExpr(lineNr + parser.row(), parser);
            if (exprFor == null) {
                continue;
            }
            expressions.add(exprFor);
        }
        return block;
    }

    protected abstract CValue<?> calc(CodeContext context) throws ScriptException;

    public CValue<?> eval(CodeContext context) throws ScriptException {
        try {
            var calc = calc(context);
            context.getCurrentBlock().setLastExprValue(calc);
            return calc;
        } catch (Exception e) {
            if (e instanceof ScriptException scriptException) {
                throw scriptException;
            }
            throw new ScriptException(e.getMessage(), lineNr, e);
        }
    }

    public final Expr getBase() {
        return base;
    }

    public String getCode() {
        return code;
    }

    public final int getLineNr() {
        return lineNr;
    }
}
