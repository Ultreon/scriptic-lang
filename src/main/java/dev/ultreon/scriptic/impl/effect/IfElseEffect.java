package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;

public class IfElseEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(if|when) (?<condition>.+) then (?<true>.+) else (?<false>.+)$";
    private Effect ifTrue;
    private Effect ifFalse;
    private @UnknownNullability Expr<Boolean> condition;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        condition = Registries.compileExpr(lineNr, new Parser(matcher.group("condition")));
        ifTrue = Registries.compileEffect(lineNr, matcher.group("true"));

        ifFalse = Registries.compileEffect(lineNr, matcher.group("false"));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        boolean predicate = condition.eval(context);
        if (predicate) {
            ifTrue.invoke(context);
        } else {
            ifFalse.invoke(context);
        }
    }
}
