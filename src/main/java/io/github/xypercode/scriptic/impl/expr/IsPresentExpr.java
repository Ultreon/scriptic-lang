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
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@SuppressWarnings("rawtypes")
public class IsPresentExpr extends Expr {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Predicate> REGISTRY = new HashMap<>();

    static {
        IsPresentExpr.register((Optional t) -> t.isPresent());
    }

    @SafeVarargs
    private static <T> void register(Predicate<T> predicate, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), predicate);
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<expr1>.+) is(?<inverter> not) present$");
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

        final var inverter = matcher.group("inverter");

        return new CExpr(this, code, lineNr) {
            @SuppressWarnings("unchecked")
            @Override
            public CValue<?> calc(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                var eval1 = expr1.eval(codeBlock, context);
                var valA = eval1.get();

                var inverted = inverter != null && inverter.equals(" not");
                for (var p : REGISTRY.entrySet()) {
                    if (p.getKey().isInstance(valA)) {
                        return inverted ? new CValue<>(!p.getValue().test(valA)) : new CValue<>(p.getValue().test(valA));
                    }
                }
                
                return new CValue<>(inverted);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
