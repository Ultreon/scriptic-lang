package dev.ultreon.scriptic;

import dev.ultreon.scriptic.impl.struct.EventStruct;
import dev.ultreon.scriptic.lang.obj.Struct;
import dev.ultreon.scriptic.lang.obj.TimerStruct;

import java.util.function.Supplier;

public class LangStructs {
    @SuppressWarnings("unchecked")
    private static <T extends Struct<?>> void create(String pattern, Supplier<T> supplier, T... typeGetter) {
        Registries.STRUCTS.register(pattern, (Supplier<Struct<?>>) supplier, (Class<Struct<?>>) typeGetter.getClass().getComponentType());
    }

    public static void init() {
        create("^on (?<event>.+)(:?)$", EventStruct::new);
        create(TimerStruct.PATTERN, TimerStruct::new);
    }
}
