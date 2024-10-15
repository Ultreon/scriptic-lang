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

import java.util.Iterator;
import java.util.regex.Pattern;

public class ForEffect extends Effect {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^for[ -](every|each) (?<type>.+) (?<iterator>.+):$");
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

        final var typeName = matcher.group("type");
        final var iteratorExpr = Registries.compileExpr(lineNr, new Parser(matcher.group("iterator")));

        final var blockEffect = CEffect.bulkCompile(lineNr, block);

        return new CEffect(this, code, lineNr) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                codeBlock.context().setLastEffect(this);

                var typeFor = Registries.getTypeFor(typeName);
                if (typeFor == null) {
                    throw new ScriptException("Unknown type: " + typeName, getLineNr());
                }

                var o = iteratorExpr.eval(context).get();
                Iterator<?> iterator;
                if (o instanceof Iterable<?> iterable) {
                    iterator = iterable.iterator();
                } else if (o instanceof Iterator<?> iterator1) {
                    iterator = iterator1;
                } else {
                    throw new ScriptException("Expected to get iterator, but got " + o.getClass().getSimpleName(), getLineNr());
                }

                ForLoop loop = new ForLoop(ForLoop.Type.VALUE);
                context.startLoop(loop);

                while (iterator.hasNext()) {
                    try (var looperBlock = context.pushBlock(blockEffect, true)) {
                        loop.setValue(iterator.next());
                        looperBlock.invoke();
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
