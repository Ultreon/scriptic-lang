package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class StringExpr extends Expr<String> {

    public static final String PATTERN = "^((\")(?<text1>.+)(\")|(')(?<text2>.+)(')|(‘)(?<text3>.+)(’))$";

    public StringExpr() {
        super(String.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {

    }

    @Override
    public @NotNull String eval(CodeContext context) throws ScriptException {
        var parser = new Parser(code());
        return parser.readString();
    }
}
