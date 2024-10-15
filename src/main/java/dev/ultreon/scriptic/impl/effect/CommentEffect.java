package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;

import java.util.regex.Pattern;

public class CommentEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^//.*$");
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
            throw new IllegalArgumentException("Invalid comment (yeah, idk what happened here): " + code);
        }

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                // Comment
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
