package dev.ultreon.scriptic.test;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.lang.obj.Effect;

public class PreTestLangEffects {
    public static final AskEffect ASK = register(ScripticLang.id("test_ask"), new AskEffect());

    private static <T extends Effect> T register(Identifier id, T effect) {
        Registries.EFFECTS.register(id, effect);
        return effect;
    }

    public static void init() {

    }
}
