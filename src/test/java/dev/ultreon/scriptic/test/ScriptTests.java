package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.impl.Logger;
import dev.ultreon.scriptic.impl.ScriptEngine;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.impl.Script;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.nio.file.Paths;

class ScriptTests {
    @Test
    void mainScript() {
        ScripticLang.preInit();
        ScripticLang.init();

        PreTestLangEffects.init();
        TestLangExpressions.init();

        Script.setLogger(new Logger() {
            @Override
            public void debug(String message) {
                System.out.println("[DEBUG] " + message);
            }

            @Override
            public void info(String message) {
                System.out.println("[INFO] " + message);
            }

            @Override
            public void error(String message) {
                System.out.println("[ERROR] " + message);
            }

            @Override
            public void error(String message, Throwable e) {
                System.out.println("[ERROR] " + message);

                e.printStackTrace();
            }

            @Override
            public void warn(String message) {
                System.out.println("[WARN] " + message);
            }

            @Override
            public void warn(String message, Throwable e) {
                System.out.println("[WARN] " + message);

                e.printStackTrace();
            }
        });

        ScriptEngine engine = ScriptEngine.builder().build();

        try {
            Script script = engine.importScript(Paths.get("main.txt"));
            script.invoke();
        } catch (CompileException | ScriptException | IOException e) {
            Assertions.fail(e);
        }
    }

    public static void main(String[] args) {
        try {
            new ScriptTests().mainScript();
        } catch (AssertionFailedError e) {
            Throwable cause = e.getCause();
            if (cause instanceof ScriptException || cause instanceof CompileException) {
                System.err.println("Error: " + cause.getMessage());
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
