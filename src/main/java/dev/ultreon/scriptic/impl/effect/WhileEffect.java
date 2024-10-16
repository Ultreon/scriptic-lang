package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.WhileLoop;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Matcher;

public class WhileEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(while|until|loop( while)|repeat) (?<condition>.+)(:?)$";
    private Expr<Boolean> conditionExpr;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        conditionExpr = Registries.compileExpr(lineNr, new Parser(matcher.group("condition")));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        WhileLoop loop = new WhileLoop();
        context.startLoop(loop);

        boolean shouldContinue = conditionExpr.doEval(context, Boolean.class);
        while (shouldContinue) {
            try (var looperBlock = context.pushBlock(getBlockEffect(), true)) {
                looperBlock.invoke();

                if (loop.isBroken()) {
                    break;
                }

                shouldContinue = conditionExpr.eval(context);
            }
        }

        context.endLoop();
    }

    @Override
    public boolean requiresBlock() {
        return true;
    }
}
