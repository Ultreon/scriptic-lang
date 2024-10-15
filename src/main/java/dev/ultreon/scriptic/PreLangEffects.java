package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.impl.effect.CommentEffect;
import dev.ultreon.scriptic.lang.obj.Effect;

public class PreLangEffects {
    public static final CommentEffect COMMENT = register(ScripticLang.id("comment"), new CommentEffect());

    private static <T extends Effect> T register(Identifier name, T effect) {
        Registries.EFFECTS.register(name, effect);
        return effect;
    }

    public static void init() {
        // Initialize class
    }
}
