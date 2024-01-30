package io.github.xypercode.scriptic.test;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.impl.ScriptEngine;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.ScripticLang;
import io.github.xypercode.scriptic.impl.Script;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class ScriptTests {
    @Test
    void mainScript() {
        ScripticLang.preInit();
        ScripticLang.init();

        PreTestLangEffects.init();
        TestLangExpressions.init();

        ScriptEngine engine = ScriptEngine.builder().build();

        try {
            Script script = engine.importScript(Paths.get("main.txt"));
            script.invoke();
        } catch (IOException | CompileException | ScriptException e) {
            Assertions.fail(e);
        }
    }
}
