package dev.ultreon.scriptic.lang.obj;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.LangObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;

public abstract class Struct<T extends LangObject<T>> extends LangObject<Struct<T>> {
    private final T detected;
    private final String block;
    private List<Effect> content;

    public Struct(@NotNull T detected, String block) {
        this.detected = detected;
        this.block = block;
    }

    public T getDetected() {
        return detected;
    }

    public String getBlock() {
        return block;
    }

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        detected.load(lineNr, matcher);

        content = Effect.bulkCompile(lineNr, block);

        if (content.isEmpty()) {
            throw new IllegalArgumentException("No effects found in struct: " + block);
        }
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        context.pushBlock(content);
        for (Effect effect : content) {
            context.setLastEffect(effect);
            effect.invoke(context);
        }
        context.popBlock();
    }
}
