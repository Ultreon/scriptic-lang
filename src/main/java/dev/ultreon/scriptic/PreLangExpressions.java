package dev.ultreon.scriptic;

import dev.ultreon.scriptic.impl.expr.CommentExpr;
import dev.ultreon.scriptic.lang.obj.Expr;
import org.intellij.lang.annotations.Language;

import java.util.function.Supplier;

public class PreLangExpressions {
    @SuppressWarnings("unchecked")
    public static <T extends Expr> void register(@Language("RegExp") String name, Supplier<T> expr, T... typeGetter) {
        Registries.EXPRESSIONS.register(name, (Supplier<Expr<?>>) expr, (Class<Expr<?>>) typeGetter.getClass().getComponentType());
    }

    public static void init() {
        register("(?<expr>.*) *//.*$", CommentExpr::new);
    }
}
