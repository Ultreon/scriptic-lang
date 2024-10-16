package dev.ultreon.scriptic;

import dev.ultreon.scriptic.impl.expr.*;
import dev.ultreon.scriptic.lang.obj.Expr;

import java.util.function.Supplier;

public class LangExpressions {

    @SuppressWarnings("unchecked")
    private static <T extends Expr<?>> void register(String name, Supplier<T> expr, T... typeGetter) {
        Registries.EXPRESSIONS.register(name, (Supplier<Expr<?>>) expr, (Class<Expr<?>>) typeGetter.getClass().getComponentType());
    }

    public static void init() {
        // Initialize class
        register(StringExpr.PATTERN, StringExpr::new);
        register(NumberExpr.PATTERN, NumberExpr::new);
        register(FloatingPointExpr.PATTERN, FloatingPointExpr::new);
        register(BooleanExpr.PATTERN, BooleanExpr::new);
        register(VariableExpr.PATTERN, VariableExpr::new);
        register(IfElseExpr.PATTERN, IfElseExpr::new);
        register(LoopIndexExpr.PATTERN, LoopIndexExpr::new);
        register(PlusExpr.PATTERN, PlusExpr::new);
        register(IsExpr.PATTERN, IsExpr::new);
        register(IsNaNExpr.PATTERN, IsNaNExpr::new);
        register(IsInfiniteExpr.PATTERN, IsInfiniteExpr::new);
        register(IsFiniteExpr.PATTERN, IsFiniteExpr::new);
        register(IsPositiveExpr.PATTERN, IsPositiveExpr::new);
        register(IsNegativeExpr.PATTERN, IsNegativeExpr::new);
        register(IsNothingExpr.PATTERN, IsNothingExpr::new);
        register(IsSomethingExpr.PATTERN, IsSomethingExpr::new);
        register(IsEmptyExpr.PATTERN, IsEmptyExpr::new);
        register(IsPresentExpr.PATTERN, IsPresentExpr::new);
        register(NotExpr.PATTERN, NotExpr::new);
        register(AsStringExpr.PATTERN, AsStringExpr::new);
        register(TypeOfExpr.PATTERN, TypeOfExpr::new);
    }
}
