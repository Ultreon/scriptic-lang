package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.impl.expr.*;
import dev.ultreon.scriptic.impl.expr.*;
import dev.ultreon.scriptic.lang.obj.Expr;

public class LangExpressions {
    public static final StringExpr STRING = register(ScripticLang.id("string"), new StringExpr());
    public static final NumberExpr NUMBER = register(ScripticLang.id("number"), new NumberExpr());
    public static final FloatingPointExpr FLOATING_POINT = register(ScripticLang.id("floating_point"), new FloatingPointExpr());
    public static final BooleanExpr BOOLEAN = register(ScripticLang.id("boolean"), new BooleanExpr());
    public static final VariableExpr VARIABLE = register(ScripticLang.id("variable"), new VariableExpr());
    public static final IfElseExpr IF_ELSE = register(ScripticLang.id("if_else"), new IfElseExpr());
    public static final LoopIndexExpr LOOP_INDEX = register(ScripticLang.id("loop_index"), new LoopIndexExpr());
    public static final PlusExpr PLUS = register(ScripticLang.id("plus"), new PlusExpr());
    public static final IsExpr IS = register(ScripticLang.id("is"), new IsExpr());
    public static final IsNaNExpr IS_NAN = register(ScripticLang.id("is_nan"), new IsNaNExpr());
    public static final IsInfiniteExpr IS_INFINITE = register(ScripticLang.id("is_infinite"), new IsInfiniteExpr());
    public static final IsFiniteExpr IS_FINITE = register(ScripticLang.id("is_finite"), new IsFiniteExpr());
    public static final IsPositiveExpr IS_POSITIVE = register(ScripticLang.id("is_positive"), new IsPositiveExpr());
    public static final IsNegativeExpr IS_NEGATIVE = register(ScripticLang.id("is_negative"), new IsNegativeExpr());
    public static final IsNothingExpr IS_NOTHING = register(ScripticLang.id("is_nothing"), new IsNothingExpr());
    public static final IsSomethingExpr IS_SOMETHING = register(ScripticLang.id("is_something"), new IsSomethingExpr());
    public static final IsEmptyExpr IS_EMPTY = register(ScripticLang.id("is_empty"), new IsEmptyExpr());
    public static final IsPresentExpr IS_PRESENT = register(ScripticLang.id("is_present"), new IsPresentExpr());
    public static final NotExpr NOT = register(ScripticLang.id("not"), new NotExpr());
    public static final AsStringExpr AS_STRING = register(ScripticLang.id("as_string"), new AsStringExpr());
    public static final TypeOfExpr TYPE_OF = register(ScripticLang.id("type_of"), new TypeOfExpr());

    private static <T extends Expr> T register(Identifier name, T expr) {
        Registries.EXPRESSIONS.register(name, expr);
        return expr;
    }

    public static void init() {
        // Initialize class
    }
}
