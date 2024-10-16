package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.ForLoop;
import dev.ultreon.scriptic.lang.Loop;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class LoopIndexExpr extends Expr<Long> {

    public static final String PATTERN = "^loop[ \\-]index$";

    public LoopIndexExpr() {
        super(Long.class);
    }

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {

    }

    @Override
    public @NotNull Long eval(CodeContext context) throws ScriptException {
        Loop currentLoop = context.getCurrentLoop();
        if (currentLoop == null) {
            throw new ScriptException("There's no loop here", getLineNr());
        }

        if (currentLoop instanceof ForLoop) {
            Number index = ((ForLoop) currentLoop).getIndex();
            if (index == null) {
                throw new ScriptException("Loop isn't indexable", getLineNr());
            }
            return index.longValue();
        }

        throw new ScriptException("Not an indexable loop", getLineNr());
    }
}
