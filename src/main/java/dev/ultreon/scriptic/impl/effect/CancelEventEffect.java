package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Matcher;

public class CancelEventEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^(cancel|stop)( event|)$";
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
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        if (context.isEventCancelable()) {
            throw new ScriptException("Event is not cancellable", lineNr);
        }

        context.cancelEvent();
    }
}
