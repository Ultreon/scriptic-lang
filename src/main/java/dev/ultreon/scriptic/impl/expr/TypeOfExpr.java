package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class TypeOfExpr extends Expr {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();

    @SafeVarargs
    public static <T> void register(Consumer<? super T> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(the |)type of (?<expr>.+)$");
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
        final var exprCode = matcher.group("expr");
        final var expr = Registries.compileExpr(lineNr, new Parser(exprCode));

        return new CExpr(this, code, lineNr) {
            @SuppressWarnings("unchecked")
            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                var eval = expr.eval(context);
                var o = eval.get();

                if (o == null) {
                    throw new IllegalArgumentException("Cannot delete nothing, got " + expr);
                }

                for (var entry : REGISTRY.entrySet()) {
                    if (entry.getKey().isInstance(o)) {
                        entry.getValue().accept(o);
                    }
                }


                throw new IllegalArgumentException("Can't find the type of that object, got expression: " + exprCode);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
