package io.github.xypercode.scriptic.test;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScripticLang;
import io.github.xypercode.scriptic.lang.obj.Effect;

public class PreTestLangEffects {
    public static final AskEffect ASK = register(ScripticLang.id("test_ask"), new AskEffect());

    private static <T extends Effect> T register(Identifier id, T effect) {
        Registries.EFFECTS.register(id, effect);
        return effect;
    }

    public static void init() {

    }
}
