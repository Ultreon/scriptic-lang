package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.Loop;
import dev.ultreon.scriptic.lang.obj.Effect;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Matcher;

public class BreakTheLoopEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(break|stop) the loop$";
    private int lineNr;

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        Loop currentLoop = context.getCurrentLoop();
        if (currentLoop == null) {
            throw new IllegalStateException("There's no loop here");
        }

        boolean broken = currentLoop.isBroken();
        if (broken) {
            throw new ScriptException("[SANITY CHECK] Breaking already broken loop", lineNr);
        }

        currentLoop.breakLoop();
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        this.lineNr = lineNr;
    }
}
