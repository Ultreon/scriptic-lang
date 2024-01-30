package io.github.xypercode.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.lang.obj.Event;

public class LangEvents {
    public static final Event SCRIPT_INIT = init(ScripticLang.id("script_init"), new Event("^on script (init(ialize)?|main|load|r[ua]n)$"));

    private static <T extends Event> T init(Identifier name, T event) {
        Registries.EVENTS.register(name, event);
        return event;
    }

    public static void init() {

    }
}
