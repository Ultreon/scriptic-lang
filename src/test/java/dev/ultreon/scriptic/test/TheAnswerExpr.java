package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class TheAnswerExpr extends Expr<String> {

    public static final String PATTERN = "^the answer$";

    public TheAnswerExpr() {
        super(String.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) {

    }

    @Override
    public @NotNull String eval(CodeContext context) {
        return context.getCurrentBlock().getAnswer();
    }
}
