package dev.ultreon.scriptic;

import dev.ultreon.scriptic.impl.Logger;
import dev.ultreon.scriptic.impl.StdOutLogger;
import dev.ultreon.scriptic.lang.Type;
import dev.ultreon.scriptic.lang.obj.Event;
import dev.ultreon.scriptic.lang.obj.JavaTimeUnit;
import dev.ultreon.scriptic.lang.obj.TimeUnitLike;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ScripticLang {
    private static final Map<String, TimeUnitLike> TIME_UNIT_REGISTRY = new HashMap<>();
    private static Logger logger = new StdOutLogger();

    private static final Map<Class<? extends Event>, Event> events = new HashMap<>();
    private static final Map<String, Event> eventPatterns = new HashMap<>();
    private static final Map<String, Type> types = new HashMap<>();
    private static final Map<Class<?>, String> typeNames = new HashMap<>();

    public static void preInit() {
        PreLangExpressions.init();
    }

    public static void init() {
        LangEvents.init();
        LangTypes.init();
        LangEffects.init();
        LangExpressions.init();
        LangStructs.init();
    }

    public static Identifier id(String path) {
        return new Identifier("scriptic_lang", path);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        ScripticLang.logger = logger;
    }

    public static void registerEvent(@Language("regexp") String pattern, Event event) {
        events.put(event.getClass(), event);
        eventPatterns.put(pattern, event);
    }

    public static void registerType(@Language("regexp") String pattern, String name, Type type) {
        types.put(pattern, type);
        typeNames.put(type.getType(), name);
    }

    public static Event getEvent(Class<? extends Event> event) {
        return events.get(event);
    }

    public static Type getType(String name) {
        for (Map.Entry<String, Type> entry : types.entrySet()) {
            if (Pattern.matches(entry.getKey(), name))
                return entry.getValue();
        }
        return null;
    }

    public static String getTypeName(Class<?> aClass) {
        for (Map.Entry<Class<?>, String> entry : typeNames.entrySet()) {
            if (entry.getKey() == Object.class) continue;
            if (entry.getKey().isAssignableFrom(aClass))
                return entry.getValue();
        }

        // What magic do we have here? Well, we have to return something.
        return "<UNKNOWN>";
    }

    public static String getPatternForEvent(Event event) {
        for (Map.Entry<String, Event> entry : eventPatterns.entrySet()) {
            if (entry.getValue() == event)
                return entry.getKey();
        }
        return null;
    }

    public static @Nullable Event getEvent(String code) {
        for (Map.Entry<String, Event> entry : eventPatterns.entrySet()) {
            if (Pattern.matches(entry.getKey(), code))
                return entry.getValue();
        }
        return null;
    }

    public static TimeUnitLike getTimeUnit(String unitGroup) {
        if (TIME_UNIT_REGISTRY.isEmpty()) {
            getLogger().warn("Time unit registry is empty! Fallback to Java time unit");

            registerTimeUnits(JavaTimeUnit.class);
        }

        for (Map.Entry<String, TimeUnitLike> entry : TIME_UNIT_REGISTRY.entrySet()) {
            if (Pattern.matches(entry.getKey(), unitGroup.toLowerCase()))
                return entry.getValue();
        }
        return null;
    }

    public static <T extends Enum<T> & TimeUnitLike> void registerTimeUnits(Class<T> unit) {
        for (T unitEnum : unit.getEnumConstants()) {
            TIME_UNIT_REGISTRY.put(unitEnum.name().toLowerCase(), unitEnum);
        }
    }
}
