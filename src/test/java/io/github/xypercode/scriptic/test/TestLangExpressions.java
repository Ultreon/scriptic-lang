package io.github.xypercode.scriptic.test;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScripticLang;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.Expr;

public class TestLangExpressions {
    public static final TheAnswerExpr THE_ANSWER = register(ScripticLang.id("the_answer"), new TheAnswerExpr());

    private static <T extends Expr> T register(Identifier id, T effect) {
        Registries.EXPRESSIONS.register(id, effect);
        return effect;
    }

    public static void init() {

    }
}
