package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class NumberExpr extends Expr<Number> {

    public static final String PATTERN = "^(?<number>\\d+)$";

    public NumberExpr() {
        super(Number.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        var parser = new Parser(code());
        var s = parser.readNumber();
    }

    @Override
    public @NotNull Number eval(CodeContext context) throws ScriptException {
        var parser = new Parser(code());
        return parser.readNumber();
    }
}
