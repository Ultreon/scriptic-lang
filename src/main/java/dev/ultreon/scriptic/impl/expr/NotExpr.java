package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;

public class NotExpr extends Expr<Boolean> {
    public static final String PATTERN = "^(! ?|not )(?<expr1>.+)$";
    private @UnknownNullability Expr<Boolean> expr;

    public NotExpr() {
        super(Boolean.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        expr = Registries.compileExpr(lineNr, matcher.group("expr1"));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {

    }

    @Override
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        return !expr.eval(context);
    }
}
