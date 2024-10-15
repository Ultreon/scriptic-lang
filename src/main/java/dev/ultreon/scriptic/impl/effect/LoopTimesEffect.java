package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.ForLoop;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public class LoopTimesEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(loop|repeat) (?<amountExpr>.+)( times|x|⨉):$");
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

        final var amountText = Registries.compileExpr(lineNr, new Parser(matcher.group("amountExpr")));

        final var blockEffect = CEffect.bulkCompile(lineNr, block);

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                codeBlock.context().setLastEffect(this);

                ForLoop loop = new ForLoop(ForLoop.Type.INDEX);
                context.startLoop(loop);

                long idx = 0L;
                try (var looperBlock = context.pushBlock(blockEffect, true)) {
                    loop: while (idx < amountText.eval(context).<Number>cast().longValue()) {
                        loop.setIndex(idx);
                        for (CEffect effect : blockEffect) {
                            effect.invoke(context);

                            if (loop.isBroken())
                                break loop;
                        }
                        idx++;
                    }
                }

                context.endLoop();
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
