package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.impl.Logger;
import dev.ultreon.scriptic.impl.StdOutLogger;

public class ScripticLang {
    private static Logger logger = new StdOutLogger();

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
}
