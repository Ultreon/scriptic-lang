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

public class SetVariableEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("(set|assign)( global|)( variable| value| data|) \\{(?<name>[A-Za-z0-9_\\-+., ()!?;]+(::[A-Za-z0-9_\\-+., ()!?;]+)*)} to (?<value>.+)$");
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

        var value = matcher.group("value");
        var effect = Registries.compileExpr(lineNr, new Parser(value));

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var script = context.getEventParameter("script", Script.class);
                script.set(matcher.group("name"), effect.eval(context));
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
