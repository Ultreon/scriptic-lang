package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.lang.obj.Effect;

import java.util.function.Supplier;

public class PreTestLangEffects {
    @SuppressWarnings("unchecked")
    @SafeVarargs
    private static <T extends Effect> void register(String id, Supplier<T> effect, T... typeGetter) {
        Registries.EFFECTS.register(id, (Supplier<Effect>) effect, (Class<Effect>) typeGetter.getClass().getComponentType());
    }

    public static void init() {
        register(AskEffect.PATTERN, AskEffect::new);
    }
}
