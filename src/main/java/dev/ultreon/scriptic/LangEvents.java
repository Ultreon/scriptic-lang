package dev.ultreon.scriptic;

import dev.ultreon.scriptic.lang.obj.Event;

public class LangEvents {
    public static final Event SCRIPT_INIT = create("^on script (init(ialize)?|main|load|r[ua]n):$", new Event());

    private static <T extends Event> T create(String pattern, T event) {
        ScripticLang.registerEvent(pattern, event);
        return event;
    }

    public static void init() {

    }
}
