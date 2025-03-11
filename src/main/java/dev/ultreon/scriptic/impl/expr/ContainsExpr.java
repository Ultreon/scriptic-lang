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
import java.util.function.BiPredicate;
import java.util.regex.Matcher;

public class ContainsExpr extends Expr<Boolean> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, BiPredicate> REGISTRY = new HashMap<>();
    public static final String PATTERN = "^(?<expr1>.+) contains (?<expr2>.+)$";

    static {
        ContainsExpr.<String>register((a, b) -> {
            if (b instanceof CharSequence) {
                CharSequence charSequence = (CharSequence) b;
                return a.contains(charSequence);
            }

            throw new IllegalStateException("Invalid type: " + b.getClass().getName());
        });
    }

    private @UnknownNullability Expr<Object> expr1;
    private @UnknownNullability Expr<Object> expr2;

    public ContainsExpr() {
        super(Boolean.class);
    }

    @SafeVarargs
    public static <T> void register(BiPredicate<? super T, Object> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
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

        final var exprCode2 = matcher.group("expr2");
        expr2 = Registries.compileExpr(lineNr, new Parser(exprCode2));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Boolean eval(CodeContext context) throws ScriptException {
        return REGISTRY.getOrDefault(expr1.eval(context).getClass(), (a, b) -> false).test(expr1.eval(context), expr2.eval(context));
    }
}
