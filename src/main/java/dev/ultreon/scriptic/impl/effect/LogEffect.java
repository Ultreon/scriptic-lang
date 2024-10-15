package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.impl.Script;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public class LogEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(write|log|say)(?: (?<expr>.+)|)( to( the)? (console|terminal|stdout)|)$");
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

        var exprCode = matcher.group("expr");
        final var effect = exprCode != null ? Registries.compileExpr(lineNr, new Parser(exprCode)) : null;

        if (effect == null) {
            throw new IllegalArgumentException("Invalid log effect: " + code);
        }

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                if (effect != null) {
                    var eval = effect.eval(context);
                    var o = eval.get();
                    if (!(o instanceof String string)) {
                        throw new IllegalArgumentException("Expected to get text, but got " + o.getClass().getName());
                    }
                    Script.getLogger().info(string);
                }
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
