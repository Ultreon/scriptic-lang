package dev.ultreon.scriptic.impl.expr;

import com.ultreon.libs.collections.v0.iterator.IntIterable;
import com.ultreon.libs.collections.v0.iterator.IntIterator;
import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class RangeExpr extends Expr {
    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(range )?(?<start>\\d+)\\.\\.(?<end>\\d+)$");
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr the line number of the code.
     * @param code   the code.
     * @return the compiled code.
     */
    @Override
    public CExpr compile(int lineNr, String code) throws CompileException {
        var pattern = getPattern();
        var matcher = pattern.matcher(code);

        if (!matcher.matches()) throw new IllegalArgumentException("Invalid code: " + code);

        var start = Integer.parseInt(matcher.group("start"));
        var end = Integer.parseInt(matcher.group("end"));

        return new CExpr(this, code, lineNr) {
            static class RangeIterable implements IntIterable {
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

            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                return new CValue<>(new RangeIterable(start, end));
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
