package io.github.xypercode.scriptic.impl.effect;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Pattern;

public class IfEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(if|when) (?<expr1>.+)( then|,) (?<expr2b>.+)$");
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
        final var effectCode1 = matcher.group("expr1");
        final @UnknownNullability CExpr effect1 = Registries.compileExpr(lineNr, new Parser(effectCode1));

        final var effectCode2a = matcher.group("expr2a");
        final String effectCode2b;
        final CEffect effect2;
        if (effectCode2a != null) {
            effect2 = CEffect.ofCodeBlock(lineNr, new Parser(effectCode2a).readIndentedBlock(2));
        } else {
            effectCode2b = matcher.group("expr2b");
            effect2 = Registries.compileEffect(lineNr, new Parser(effectCode2b));
        }

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                boolean predicate = (Boolean) effect1.eval(codeBlock, context).get();
                if (predicate) {
                    effect2.invoke(codeBlock, context);
                }
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }

    @Override
    public boolean hasCodeBlock() {
        return true;
    }
}
