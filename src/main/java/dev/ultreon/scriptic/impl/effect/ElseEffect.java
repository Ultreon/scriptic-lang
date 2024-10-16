package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class ElseEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^else (?<expr>.+)$";
    private @Nullable Effect effect;
    private int lineNr;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        this.lineNr = lineNr;
        effect = Registries.compileEffect(lineNr, matcher.group("expr"));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        var ifStatement = context.getCurrentBlock().getIfStatement();
        if (ifStatement == null)
            throw new ScriptException("Expected previous expression to be an if.", lineNr);
        if (!Boolean.TRUE.equals(ifStatement.isSucceeded()))
            return;

        effect.invoke(context);
        // If the 'if' effect has not succeeded, we don't execute the else effect.
    }
}
