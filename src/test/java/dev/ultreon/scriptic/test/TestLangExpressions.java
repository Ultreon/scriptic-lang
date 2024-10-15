package dev.ultreon.scriptic.test;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.lang.obj.Expr;

public class TestLangExpressions {
    public static final TheAnswerExpr THE_ANSWER = register(ScripticLang.id("the_answer"), new TheAnswerExpr());

    private static <T extends Expr> T register(Identifier id, T effect) {
        Registries.EXPRESSIONS.register(id, effect);
        return effect;
    }

    public static void init() {

    }
}
