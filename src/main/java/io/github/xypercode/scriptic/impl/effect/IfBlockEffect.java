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

public class IfBlockEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(if|when) (?<condition>.+) then:$");
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

        final var conditionCode = matcher.group("condition");
        final var conditionExpr = Registries.compileExpr(lineNr, new Parser(conditionCode));

        final var blockEffect = CEffect.bulkCompile(lineNr, new Parser(block).readIndentedBlock(2));

        return new CEffect(this, code, lineNr, new CodeContextImpl()) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                boolean condition = (Boolean) conditionExpr.eval(codeBlock, context).get();
                codeBlock.context().set(CodeBlock.CTX_LAST_EFFECT, this);
                codeBlock.context().set(CodeBlock.CTX_LAST_IF_SUCCEEDED, condition);

                if (condition) {
                    var conditionalBlock = new CodeBlock(codeBlock, blockEffect, new CodeContextImpl());
                    conditionalBlock.invoke();
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
