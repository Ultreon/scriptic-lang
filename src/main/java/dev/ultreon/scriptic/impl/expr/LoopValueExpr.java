package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.ForLoop;
import dev.ultreon.scriptic.lang.Loop;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class LoopValueExpr extends Expr<Object> {
    @RegExp
    public static final String PATTERN = "^loop[ \\-]value$";

    public LoopValueExpr() {
        super(Object.class);
    }

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {

    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        Loop currentLoop = context.getCurrentLoop();
        if (currentLoop == null) {
            throw new ScriptException("There's no loop here", getLineNr());
        }

        if (currentLoop instanceof ForLoop) {
            Object value = ((ForLoop) currentLoop).getValue();
            if (value == null) {
                throw new ScriptException("Loop isn't valueable", getLineNr());
            }
            return value;
        }

        throw new ScriptException("Not an valueable loop", getLineNr());
    }
}
