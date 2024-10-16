package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.ForLoop;
import dev.ultreon.scriptic.lang.Type;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.regex.Matcher;

public class ForEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^for[ -](every|each) (?<type>.+) in (?<iterator>.+)(:?)$";
    private int lineNr;
    private Type type;
    private @UnknownNullability Expr<?> iteratorExpr;

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        this.lineNr = lineNr;
        type = Registries.getTypeFor(matcher.group("type"));
        iteratorExpr = Registries.compileExpr(lineNr, new Parser(matcher.group("iterator")));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        context.setLastEffect(this);

        var o = iteratorExpr.eval(context);
        Iterator<?> iterator;
        if (o instanceof Iterable<?> iterable) {
            iterator = iterable.iterator();
        } else if (o instanceof Iterator<?> iterator1) {
            iterator = iterator1;
        } else if (o.getClass().isArray()) {
            iterator = new ArrayIterator(o);
        } else {
            throw new ScriptException("Expected to get iterator, but got " + o.getClass().getSimpleName(), lineNr);
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
    public boolean requiresBlock() {
        return true;
    }

    private class ArrayIterator implements Iterator<Object> {
        private final Object o;
        private int index = 0;

        public ArrayIterator(Object o) {
            this.o = o;
        }

        @Override
        public boolean hasNext() {
            return index < Array.getLength(o);
        }

        @Override
        public Object next() {
            return Array.get(o, index++);
        }
    }
}
