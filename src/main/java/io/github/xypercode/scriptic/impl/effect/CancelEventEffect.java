package io.github.xypercode.scriptic.impl.effect;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.ScripticLang;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;

import java.util.regex.Pattern;

public class CancelEventEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(cancel|stop)( event|)$");
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr the line number of the code.
     * @param code   the code.
     * @return the compiled code.
     */
    @Override
    public CEffect compile(int lineNr, String code) throws CompileException {
        var pattern = getPattern();

        var matcher = pattern.matcher(code);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var cancel = (Runnable) context.get(ScripticLang.id("cancel"));
                if (cancel == null) {
                    throw new IllegalArgumentException("Event is not cancellable");
                }
                cancel.run();
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
