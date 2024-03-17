package io.github.xypercode.scriptic.impl.effect;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.impl.CodeContexts;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.CodeContextImpl;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import io.github.xypercode.scriptic.lang.parser.Parser;

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

        final var blockEffect = CEffect.bulkCompile(lineNr, new Parser(block).readIndentedBlock(2));

        return new CEffect(this, code, lineNr, new CodeContextImpl()) {
            @Override
            public void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                codeBlock.context().set(CodeBlock.CTX_LAST_EFFECT, this);

                var typeFor = Registries.getTypeFor(typeName);
                if (typeFor == null) {
                    throw new ScriptException("Unknown type: " + typeName, getLineNr());
                }

                var o = iteratorExpr.eval(codeBlock, context).get();
                Iterator<?> iterator;
                if (o instanceof Iterable<?> iterable) {
                    iterator = iterable.iterator();
                } else if (o instanceof Iterator<?> iterator1) {
                    iterator = iterator1;
                } else {
                    throw new ScriptException("Expected to get iterator, but got " + o.getClass().getSimpleName(), getLineNr());
                }

                while (iterator.hasNext()) {
                    var looperBlock = new CodeBlock(codeBlock, blockEffect, new CodeContextImpl());
                    looperBlock.context().set(CodeContexts.LOOP_VALUE, iterator.next());
                    looperBlock.invoke();
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
