package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.lang.obj.Expr;

import java.util.function.Supplier;

public class TestLangExpressions {
    @SuppressWarnings("unchecked")
    private static <T extends Expr> void register(String id, Supplier<T> effect, T... typeGetter) {
        Registries.EXPRESSIONS.register(id, (Supplier<Expr<?>>) effect, (Class<Expr<?>>) typeGetter.getClass().getComponentType());
    }

    public static void init() {
        register(TheAnswerExpr.PATTERN, TheAnswerExpr::new);
    }
}
