package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class AsStringExpr extends Expr<String> {
    public static final String PATTERN = "^(?<expr>.+)( converted|) (as|to) (string|text)$";
    private Expr expr;

    public AsStringExpr() {
        super(String.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        expr = Registries.compileExpr(lineNr, matcher.group("expr"));
    }

    @Override
    public @NotNull String eval(CodeContext context) throws ScriptException {
        return String.valueOf(expr.eval(context));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        var eval = expr.eval(context);

        context.getCurrentBlock().setLastExprValue(eval);
    }
}
