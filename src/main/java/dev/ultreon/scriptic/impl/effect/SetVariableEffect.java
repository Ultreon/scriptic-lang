package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.impl.Script;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;

public class SetVariableEffect extends Effect {
    public static final String PATTERN = "(set|assign)( global|)( variable| value| data|) \\{(?<name>[A-Za-z0-9_\\-+., ()!?;]+(::[A-Za-z0-9_\\-+., ()!?;]+)*)} to (?<value>.+)$";
    private @UnknownNullability Expr expr;
    private String name;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        var value = matcher.group("value");
        expr = Registries.compileExpr(lineNr, new Parser(value));
        name = matcher.group("name");
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        var script = context.getEventParameter("script", Script.class);
        script.set(name, expr.eval(context));
    }
}
