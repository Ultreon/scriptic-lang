package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;

@SuppressWarnings("rawtypes")
public class IsPresentExpr extends Expr<Object> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Predicate> REGISTRY = new HashMap<>();
    public static final String PATTERN = "^(?<expr1>.+) is(?<inverter> not) present$";

    static {
        IsPresentExpr.register((Optional t) -> t.isPresent());
    }

    private String inverter;
    private @UnknownNullability Expr<Object> expr1;

    public IsPresentExpr() {
        super(Object.class);
    }

    @SafeVarargs
    private static <T> void register(Predicate<T> predicate, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), predicate);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        final var exprCode1 = matcher.group("expr1");
        expr1 = Registries.compileExpr(lineNr, new Parser(exprCode1));

        inverter = matcher.group("inverter");
    }

    @Override
    public @NotNull Object eval(CodeContext context) throws ScriptException {
        var eval = expr1.eval(context);
        var inverted = inverter != null && inverter.equals(" not");

        for (var p : REGISTRY.entrySet()) {
            if (p.getKey().isInstance(eval)) {
                return p.getValue().test(eval);
            }
        }

        return eval;
    }
}
