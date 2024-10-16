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

public class IsAnExpr extends Expr<Boolean> {
    public static final String PATTERN = "^(?<expr1>.+) is (a|an)( instance of|) (?<expr2>.+)$";
    private String typeName;
    private @UnknownNullability Expr<Object> expr;
    private int lineNr;

    public IsAnExpr() {
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
        this.lineNr = lineNr;
        final var exprCode1 = matcher.group("expr1");
        expr = Registries.compileExpr(lineNr, new Parser(exprCode1));

        typeName = matcher.group("expr2");
    }

    @Override
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        var eval = expr.eval(context);

        var typeFor = Registries.getTypeFor(typeName);
        if (typeFor == null) {
            throw new ScriptException("Unknown type: " + typeName, lineNr);
        }

        return typeFor.isInstance(eval);
    }
}
