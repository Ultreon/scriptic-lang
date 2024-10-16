package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class IfElseExpr extends Expr<Object> {
    public static final String PATTERN = "^(if|when) (?<expr1>.+) then (?<expr2b>.+) else (?<expr3b>.+)$";
    private Expr<Object> expr2;
    private Expr<Object> expr3;
    private Expr<Boolean> expr1;

    public IfElseExpr() {
        super(Object.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {        final var exprCode1 = matcher.group("expr1");
        expr1 = Registries.compileExpr(lineNr, new Parser(exprCode1));

        final var exprCode2a = matcher.group("expr2a");
        final String exprCode2b = matcher.group("expr2b");
        expr2 = Registries.compileExpr(lineNr, exprCode2b);

        final var exprCode3a = matcher.group("expr3a");
        final String exprCode3b = matcher.group("expr3b");
        expr3 = Registries.compileExpr(lineNr, exprCode3b);
    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        boolean predicate = (Boolean) expr1.eval(context);
        if (predicate) {
            return expr2.eval(context);
        } else {
            return expr3.eval(context);
        }
    }
}
