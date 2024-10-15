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

public class IsExpr extends Expr {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();

    @SafeVarargs
    public static <T> void register(Consumer<? super T> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(?<expr1>.+) is(?<inverter> not)? (?<expr2>.+)$");
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

        final var exprCode2 = matcher.group("expr2");
        final var expr2 = Registries.compileExpr(lineNr, new Parser(exprCode2));

        return new CExpr(this, code, lineNr) {
            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                var eval1 = expr1.eval(context);
                var eval2 = expr2.eval(context);
                var valA = eval1.get();
                var valB = eval2.get();


                if (inverter != null && inverter.equals(" not")) {
                    if (valA instanceof Number numA && valB instanceof Number numB) {
                        return valA instanceof Double || valA instanceof Float || valB instanceof Double || valB instanceof Float
                                ? new CValue<>(numA.doubleValue() != numB.doubleValue())
                                : new CValue<>(numA.longValue() != numB.longValue());
                    }
                    return new CValue<>(!valA.equals(valB));
                }

                if (valA instanceof Number numA && valB instanceof Number numB) {
                    return valA instanceof Double || valA instanceof Float || valB instanceof Double || valB instanceof Float
                            ? new CValue<>(numA.doubleValue() == numB.doubleValue())
                            : new CValue<>(numA.longValue() == numB.longValue());
                }
                return new CValue<>(valA.equals(valB));
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
