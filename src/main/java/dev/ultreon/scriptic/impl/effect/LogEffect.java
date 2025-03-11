package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.impl.Script;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Matcher;

public class LogEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(write|log|say)(?: (?<expr>.+)|)( to( the)? (console|terminal|stdout)|)$";
    private Expr<?> expr;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        expr = matcher.group("expr") != null ? Registries.compileExpr(lineNr, matcher.group("expr")) : null;

        if (expr == null) {
            throw new IllegalArgumentException("Invalid log effect: " + block);
        }
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        if (expr == null) return;
        var eval = expr.eval(context);
        if (!(eval instanceof String)) {
            throw new ScriptException("Expected to get text, but got " + ScripticLang.getTypeName(eval.getClass()));
        }
        String string = (String) eval;
        Script.getLogger().print(string);
    }
}
