package dev.ultreon.scriptic.impl.expr;

import com.ultreon.libs.collections.v0.iterator.IntIterable;
import com.ultreon.libs.collections.v0.iterator.IntIterator;
import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class RangeExpr extends Expr<Iterable<Integer>> {
    public static final String PATTERN = "^(range )?(?<start>\\d+)\\.\\.(?<end>\\d+)$";
    private int start;
    private int end;

    public RangeExpr() {
        super(Iterable.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        start = Integer.parseInt(matcher.group("start"));
        end = Integer.parseInt(matcher.group("end"));
    }

    @Override
    public @NotNull Iterable<Integer> eval(CodeContext context) throws ScriptException {
        return new RangeIterable(start, end);
    }

    private static class RangeIterable implements IntIterable {
        private final int start1;
        private final int end1;
        private final int step1;

        public RangeIterable(int start, int end) {
            this(start, end, 1);
        }

        public RangeIterable(int start, int end, int step) {
            this.start1 = start;
            this.end1 = end;
            this.step1 = step;
        }

        @Override
        public @NotNull IntIterator iterator() {
            return new IntIterator() {
                private int i = start1;

                @Override
                public boolean hasNext() {
                    return i < end1;
                }

                @Override
                public int nextInt() {
                    var result = i;
                    i += step1;
                    return result;
                }
            };
        }
    }
}
