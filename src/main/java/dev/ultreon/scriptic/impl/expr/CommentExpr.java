package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;

public class CommentExpr extends Expr<Object> {
    private @UnknownNullability Expr<Object> expr1;

    public CommentExpr() {
        super(Object.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        var expr = matcher.group("expr");
        expr1 = Registries.compileExpr(lineNr, new Parser(expr));
    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        return expr1.eval(context);
    }
}
