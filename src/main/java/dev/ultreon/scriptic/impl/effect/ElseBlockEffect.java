package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import org.intellij.lang.annotations.RegExp;

import java.util.List;
import java.util.regex.Matcher;

public class ElseBlockEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^else:$";
    private List<Effect> blockEffect;
    private int lineNr;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        blockEffect = Effect.bulkCompile(lineNr, block);
        this.lineNr = lineNr;
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        Effect lastEffect = context.getCurrentBlock().getLastEffect();
        if (lastEffect != null && (lastEffect instanceof IfEffect || lastEffect instanceof IfBlockEffect)) {
            if (context.getIfEffect(context.getCurrentBlock()).isSucceeded()) return;

            try (var conditionalBlock = context.pushBlock(blockEffect, true)) {
                conditionalBlock.invoke();
            }
        } else {
            throw new ScriptException("Expected previous expression to be an if.", lineNr);
        }
    }

    @Override
    public boolean requiresBlock() {
        return true;
    }
}
