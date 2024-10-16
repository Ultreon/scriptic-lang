package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.impl.Logger;
import dev.ultreon.scriptic.impl.StdOutLogger;
import dev.ultreon.scriptic.lang.Type;
import dev.ultreon.scriptic.lang.obj.Event;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ScripticLang {
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

    public static void registerEvent(String pattern, Event event) {
        events.put(event.getClass(), event);
        eventPatterns.put(pattern, event);
    }

    public static void registerType(String pattern, String name, Type type) {
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
            if (Pattern.matches(entry.getKey(), event.getPattern().pattern()))
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
}
