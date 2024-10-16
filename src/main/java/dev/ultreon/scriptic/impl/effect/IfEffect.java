package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class IfEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(if|when) (?<condition>.+)( then|,) (?<effect>.+)$";
    private Expr<Boolean> condition;
    private @Nullable Effect effect;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        condition = Registries.compileExpr(lineNr, new Parser(matcher.group("condition")));
        effect = Registries.compileEffect(lineNr, matcher.group("effect"));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        if (effect != null) {
            boolean predicate = condition.eval(context);
            if (predicate) {
                effect.invoke(context);
            }
        }
    }

    @Override
    public boolean requiresBlock() {
        return true;
    }
}
