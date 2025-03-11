package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.impl.Logger;
import dev.ultreon.scriptic.impl.ScriptEngine;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.impl.Script;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.nio.file.Paths;

class ScriptTests {
    private String[] args;

    void mainScript() {
        ScripticLang.preInit();
        ScripticLang.init();

        PreTestLangEffects.init();
        TestLangExpressions.init();

        Script.setLogger(new Logger() {
            @Override
            public void debug(String message) {
                System.err.println("[DEBUG] " + message);
            }

            @Override
            public void info(String message) {
                System.err.println("[INFO] " + message);
            }

            @Override
            public void error(String message) {
                System.err.println("[ERROR] " + message);
            }

            @Override
            public void error(String message, Throwable e) {
                System.err.println("[ERROR] " + message);

                e.printStackTrace();
            }

            @Override
            public void warn(String message) {
                System.err.println("[WARN] " + message);
            }

            @Override
            public void warn(String message, Throwable e) {
                System.err.println("[WARN] " + message);

                e.printStackTrace();
            }

            @Override
            public void print(String message) {
                System.out.println(message);
            }
        });

        ScriptEngine engine = ScriptEngine.builder().build();

        try {
            Script script = engine.importScript(Paths.get("main.txt"), args != null ? args : new String[]{"Bananas", "Apple"});
            script.invoke();
        } catch (CompileException | ScriptException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            ScriptTests scriptTests = new ScriptTests();
            scriptTests.args = args;
            scriptTests.mainScript();
            System.exit(0);
        } catch (AssertionFailedError e) {
            Throwable cause = e.getCause();
            if (cause instanceof ScriptException || cause instanceof CompileException) {
                System.err.println("Error: " + cause.getMessage());
            }
            System.exit(1);
        }
    }

}
