package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class BooleanExpr extends Expr<Boolean> {
    public static final String PATTERN = "^((?<true>(true|on|yes|enable(d|)))|(?<false>(false|off|no|disable(d|))))$";
    private boolean val;

    public BooleanExpr() {
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
        switch (matcher.group()) {
            case "true", "on", "yes", "enable", "enabled" -> val = true;
            case "false", "off", "no", "disable", "disabled" -> val = false;
            default -> throw new IllegalArgumentException("Invalid boolean value: " + block);
        }
    }

    @Override
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        return val;
    }
}
