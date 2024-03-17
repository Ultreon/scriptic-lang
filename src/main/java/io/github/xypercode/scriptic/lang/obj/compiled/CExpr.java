package io.github.xypercode.scriptic.lang.obj.compiled;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.CodeContextImpl;
import io.github.xypercode.scriptic.lang.CompiledCode;
import io.github.xypercode.scriptic.lang.obj.Expr;
import io.github.xypercode.scriptic.lang.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public abstract class CExpr extends CompiledCode {
    private final Expr base;
    private final String code;
    private final int lineNr;
    private final CodeContext context;

    protected CExpr(Expr base, String code, int lineNr) {
        this(base, code, lineNr, new CodeContextImpl());
    }

    protected CExpr(Expr base, String code, int lineNr, CodeContext context) {
        this.base = base;
        this.code = code;
        this.lineNr = lineNr;
        this.context = context;
    }

    public static CExpr ofCodeBlock(int lineNr, String code) throws CompileException {
        return ofCodeBlock(lineNr, code, (CodeBlock) null);
    }

    public static CExpr ofCodeBlock(int lineNr, String code,  CodeContext blockContext) throws CompileException {
        return ofCodeBlock(lineNr, code, null, blockContext);
    }

    public static CExpr ofCodeBlock(int lineNr, String code, CodeBlock parent) throws CompileException {
        return ofCodeBlock(lineNr, code, parent, new CodeContextImpl());
    }

    public static CExpr ofCodeBlock(int lineNr, String code, CodeBlock parent, CodeContext blockContext) throws CompileException {
        var parser = new Parser(code);
        List<CExpr> expressions = new ArrayList<>();

        var block = new CExpr(null, code, lineNr, blockContext) {
            /**
             * Evaluates this expression.
             *
             * @param codeBlock the code block this expression is in.
             * @param context   the context to evaluate this expression in.
             * @return the result of this expression.
             */
            @Override
            public CNone calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                for (var expr : expressions) {
                    expr.eval(codeBlock, context);
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

    protected abstract CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException;

    public CValue<?> eval(CodeBlock codeBlock, CodeContext context) throws ScriptException {
        try {
            var calc = calc(codeBlock, context);
            codeBlock.context().set(CodeBlock.CTX_LAST_EXPR_VALUE, this);
            return calc;
        } catch (Exception e) {
            if (e instanceof ScriptException scriptException) {
                throw scriptException;
            }
            throw new ScriptException(e.getMessage(), lineNr);
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

    public CodeContext context() {
        return context;
    }
}
