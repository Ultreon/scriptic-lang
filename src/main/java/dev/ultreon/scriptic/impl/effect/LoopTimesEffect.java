package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.ForLoop;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;

public class LoopTimesEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(loop|repeat) (?<amountExpr>.+)( times|x|⨉)(:?)$";
    private @UnknownNullability Expr<?> amountText;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        amountText = Registries.compileExpr(lineNr, new Parser(matcher.group("amountExpr")));;
    }

    @Override
    public boolean requiresBlock() {
        return true;
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        ForLoop loop = new ForLoop(ForLoop.Type.INDEX);
        context.startLoop(loop);

        long idx = 0L;
        try (var looperBlock = context.pushBlock(getBlockEffect(), true)) {
            loop: while (idx < amountText.doEval(context, Number.class).longValue()) {
                loop.setIndex(idx);
                for (Effect effect : getBlockEffect()) {
                    effect.invoke(context);

                    if (loop.isBroken())
                        break loop;
                }
                idx++;
            }
        }

        context.endLoop();
    }
}
