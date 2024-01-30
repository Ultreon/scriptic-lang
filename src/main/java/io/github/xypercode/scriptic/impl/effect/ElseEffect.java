package io.github.xypercode.scriptic.impl.effect;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.CodeContextImpl;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import io.github.xypercode.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public class ElseEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^else (?<expr>.+)$");
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

        final var effectCode = matcher.group("expr");
        final var effect = Registries.compileEffect(lineNr, new Parser(effectCode));

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var lastEffect = codeBlock.context().get(CodeBlock.CTX_LAST_EFFECT, CEffect.class);
                if (lastEffect != null && lastEffect.getBase() instanceof IfEffect) {
                    if (Boolean.TRUE.equals(codeBlock.context().get(CodeBlock.CTX_LAST_IF_SUCCEEDED, Boolean.class))) {
                        effect.invoke(codeBlock, context);
                    }
                    // If the 'if' effect has not succeeded, we don't execute the else effect.
                } else {
                    throw new ScriptException("Expected previous expression to be an if.", lineNr);
                }
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
