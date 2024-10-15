package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.impl.expr.CommentExpr;
import dev.ultreon.scriptic.lang.obj.Expr;

public class PreLangExpressions {
    public static final CommentExpr COMMENT = register(ScripticLang.id("comment"), new CommentExpr());

    public static <T extends Expr> T register(Identifier name, T expr) {
        Registries.EXPRESSIONS.register(name, expr);
        return expr;
    }

    public static void init() {
        // Preload all expressions
    }
}
