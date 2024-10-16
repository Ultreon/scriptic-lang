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

public class IsNaNExpr extends Expr<Boolean> {
    public static final String PATTERN = "^(?<expr1>.+) is( nan| NaN|( not|n't) a number)";
    private @UnknownNullability Expr<Object> expr1;

    public IsNaNExpr() {
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
        final var exprCode1 = matcher.group("expr1");
        expr1 = Registries.compileExpr(lineNr, new Parser(exprCode1));
    }

    @Override
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        var evaluated = expr1.eval(context);
        return evaluated instanceof Double aDouble && Double.isNaN(aDouble) || evaluated instanceof Float aFloat && Float.isNaN(aFloat);
    }
}
