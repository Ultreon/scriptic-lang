package io.github.xypercode.scriptic.impl.expr;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Expr;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.obj.compiled.CValue;
import io.github.xypercode.scriptic.lang.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public class ContainsExpr extends Expr {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, BiPredicate> REGISTRY = new HashMap<>();

    static {
        ContainsExpr.<String>register((a, b) -> {
            if (b instanceof CharSequence charSequence) {
                return a.contains(charSequence);
            }

            throw new IllegalStateException("Invalid type: " + b.getClass().getName());
        });
    }

    @SafeVarargs
    public static <T> void register(BiPredicate<? super T, Object> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<expr1>.+) contains (?<expr2>.+)$");
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
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }

        final var exprCode1 = matcher.group("expr1");
        final var expr1 = Registries.compileExpr(lineNr, new Parser(exprCode1));

        final var exprCode2 = matcher.group("expr2");
        final var expr2 = Registries.compileExpr(lineNr, new Parser(exprCode2));

        return new CExpr(this, code, lineNr) {
            @SuppressWarnings("unchecked")
            @Override
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var eval1 = expr1.eval(codeBlock, context);
                var valA = eval1.get();

                var eval2 = expr2.eval(codeBlock, context);
                var valB = eval2.get();

                if (valA == null) {
                    return new CValue<>(null);
                }

                for (var entry : REGISTRY.entrySet()) {
                    if (entry.getKey().isInstance(valA)) {
                        return new CValue<>(entry.getValue().test(valA, valB));
                    }
                }

                throw new IllegalArgumentException("Expected to have something that is a container, got expression: " + exprCode1);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
