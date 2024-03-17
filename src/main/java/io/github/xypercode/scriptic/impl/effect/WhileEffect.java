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

public class WhileEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(while|until|loop( while)|repeat) (?<condition>.+)$");
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

        var parser = new Parser(code);
        var code1 = parser.readLine();
        var matcher = pattern.matcher(code1);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid if statement: " + code1);
        }

        var block = parser.readIndentedBlock();

        final var conditionExpr = Registries.compileExpr(lineNr, new Parser(matcher.group("condition")));

        final var blockEffect = CEffect.bulkCompile(lineNr, new Parser(block).readIndentedBlock(2));

        return new CEffect(this, code, lineNr, new CodeContextImpl()) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                codeBlock.context().set(CodeBlock.CTX_LAST_EFFECT, this);

                boolean shouldContinue = conditionExpr.eval(codeBlock, context).cast();
                while (shouldContinue) {
                    var looperBlock = new CodeBlock(codeBlock, blockEffect, new CodeContextImpl());
                    looperBlock.invoke();
                    shouldContinue = conditionExpr.eval(codeBlock, context).cast();
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
