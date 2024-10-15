package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.IfStatement;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public class IfBlockEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(if|when) (?<condition>.+)( then|):$");
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

        final var blockEffect = CEffect.bulkCompile(lineNr, block);

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                IfStatement ifStatement = new IfStatement();
                boolean condition = (Boolean) conditionExpr.eval(context).get();

                context.setIfStatment(codeBlock, ifStatement);
                context.setLastEffect(this);

                if (condition) ifStatement.success();

                if (condition) {
                    try (var conditionalBlock = context.pushBlock(blockEffect, true)) {
                        conditionalBlock.invoke();
                    }
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
