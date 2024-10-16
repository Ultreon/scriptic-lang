package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.IfStatement;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Matcher;

public class IfBlockEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(if|when) (?<condition>.+)( then|)(:?)$";
    private Expr<Boolean> conditionExpr;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        conditionExpr = Registries.compileExpr(lineNr, matcher.group("condition"));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        IfStatement ifStatement = new IfStatement();
        boolean condition = conditionExpr.eval(context);

        context.setIfEffect(context.getCurrentBlock(), ifStatement);
        context.setLastEffect(this);

        if (condition) ifStatement.success();

        if (condition) {
            try (var conditionalBlock = context.pushBlock(getBlockEffect(), true)) {
                conditionalBlock.invoke();
            }
        }
    }

    @Override
    public boolean requiresBlock() {
        return true;
    }
}
