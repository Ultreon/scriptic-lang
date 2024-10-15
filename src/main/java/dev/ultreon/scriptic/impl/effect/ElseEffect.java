package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.parser.Parser;

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
                var ifStatement = codeBlock.getIfStatement();
                if (ifStatement == null)
                    throw new ScriptException("Expected previous expression to be an if.", lineNr);
                if (!Boolean.TRUE.equals(ifStatement.isSucceeded()))
                    return;

                effect.invoke(context);
                // If the 'if' effect has not succeeded, we don't execute the else effect.
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
