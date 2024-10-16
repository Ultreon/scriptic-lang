package dev.ultreon.scriptic.lang.obj;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.LangObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;

public abstract class Struct<T extends LangObject<T>> extends LangObject<Struct<?>> {
    private T detected;
    private List<Effect> content;

    public Struct() {

    }

    public abstract @Nullable T detect();

    public T getDetected() {
        return detected;
    }

    public String getBlock() {
        return block;
    }

    @Override
    public final void preload(int lineNr, String code) {
        super.preload(lineNr, code);

        T detect = detect();
        if (detect == null) {
            throw new IllegalArgumentException("Structure could not be detected: " + code);
        }

        detected = detect;
    }

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        detected.load(lineNr, matcher);

        content = Effect.bulkCompile(lineNr, block);

        if (content.isEmpty()) {
            throw new IllegalArgumentException("No effects found in struct: " + block);
        }

        this.registerListener();
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

    public boolean requiresBlock() {
        return true;
    }

    public void registerListener() {

    }
}
