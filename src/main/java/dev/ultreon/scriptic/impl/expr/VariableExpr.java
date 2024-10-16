package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.impl.Script;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class VariableExpr extends Expr<Object> {
    public static final String PATTERN = "\\{(?<expr>[A-Za-z0-9_\\-+., ()!?;]+(::[A-Za-z0-9_\\-+., ()!?;]+)*)}$";
    private String name;

    public VariableExpr() {
        super(Object.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        name = matcher.group("expr");
    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        var script = context.getEventParameter("script", Script.class);
        return script.get(name);
    }
}
