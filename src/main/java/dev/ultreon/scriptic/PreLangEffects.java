package dev.ultreon.scriptic;

import dev.ultreon.scriptic.impl.effect.CommentEffect;
import dev.ultreon.scriptic.lang.obj.Effect;
import org.intellij.lang.annotations.RegExp;

import java.util.function.Supplier;

public class PreLangEffects {
    @SuppressWarnings("unchecked")
    private static <T extends Effect> void register(@RegExp String name, Supplier<T> effect, T... typeGetter) {
        Registries.EFFECTS.register(name, (Supplier<Effect>) effect, (Class<Effect>) typeGetter.getClass().getComponentType());
    }

    public static void init() {
        register(CommentEffect.PATTERN, CommentEffect::new);
    }
}
