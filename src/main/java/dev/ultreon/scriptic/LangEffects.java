package dev.ultreon.scriptic;

import dev.ultreon.scriptic.impl.effect.*;
import dev.ultreon.scriptic.lang.obj.Effect;

import java.util.function.Supplier;

public class LangEffects {
    @SuppressWarnings("unchecked")
    private static <T extends Effect> void register(String name, Supplier<T> supplier, T... typeGetter) {
        Registries.EFFECTS.register(name, (Supplier<Effect>) supplier, (Class<Effect>) typeGetter.getClass().getComponentType());
    }

    public static void init() {
        register(CancelEventEffect.PATTERN, CancelEventEffect::new);
        register(IfBlockEffect.PATTERN, IfBlockEffect::new);
        register(IfEffect.PATTERN, IfEffect::new);
        register(ElseBlockEffect.PATTERN, ElseBlockEffect::new);
        register(ElseEffect.PATTERN, ElseEffect::new);
        register(LoopTimesEffect.PATTERN, LoopTimesEffect::new);
        register(ForEffect.PATTERN, ForEffect::new);
        register(WhileEffect.PATTERN, WhileEffect::new);
        register(BreakTheLoopEffect.PATTERN, BreakTheLoopEffect::new);
        register(DeleteEffect.PATTERN, DeleteEffect::new);
        register(LogEffect.PATTERN, LogEffect::new);
        register(SetVariableEffect.PATTERN, SetVariableEffect::new);
    }
}
