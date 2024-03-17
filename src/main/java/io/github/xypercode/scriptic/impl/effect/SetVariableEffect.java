package io.github.xypercode.scriptic.impl.effect;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.impl.CodeContexts;
import io.github.xypercode.scriptic.impl.Script;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import io.github.xypercode.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public class SetVariableEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("set( global|)( variable| value| data) \\{(?<name>[A-Za-z0-9_\\-+., ()!?;]+(::[A-Za-z0-9_\\-+., ()!?;]+)*)} to (?<value>.+)$");
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
                var script = context.require(CodeContexts.SCRIPT, Script.class);
                script.set(matcher.group("name"), effect.eval(codeBlock, context));
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
