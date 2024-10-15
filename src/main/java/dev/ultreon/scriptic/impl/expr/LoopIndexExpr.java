package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.ForLoop;
import dev.ultreon.scriptic.lang.Loop;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;

import java.util.regex.Pattern;

public class LoopIndexExpr extends Expr {

    @Override
    public Pattern getPattern() {
        return Pattern.compile("^loop[ \\-]index$");
    }

    @Override
    public CExpr compile(int lineNr, String code) throws CompileException {
        return new CExpr(this, code, lineNr) {
            @Override
            protected CValue<?> calc(CodeContext context) throws ScriptException {
                Loop currentLoop = context.getCurrentLoop();
                if (currentLoop == null) {
                    throw new ScriptException("There's no loop here", lineNr);
                }

                if (currentLoop instanceof ForLoop) {
                    Number index = ((ForLoop) currentLoop).getIndex();
                    if (index == null) {
                        throw new ScriptException("Loop isn't indexable", lineNr);
                    }
                    return new CValue<>(index);
                }

                throw new ScriptException("Not an indexable loop", lineNr);
            }
        };
    }
}
