package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

public class ScriptArgumentsExpr extends Expr<String[]> {
    @RegExp
    public static final String PATTERN = "^(program|script) arguments$";

    public ScriptArgumentsExpr() {
        super(String[].class);
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
    public @NotNull String[] eval(CodeContext context) throws ScriptException {
        return context.getScriptArguments();
    }
}
