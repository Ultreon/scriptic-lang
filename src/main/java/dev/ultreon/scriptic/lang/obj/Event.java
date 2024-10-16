package dev.ultreon.scriptic.lang.obj;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.impl.struct.EventStruct;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.LangObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * Language Event.
 *
 * @author Qboi123
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public class Event extends LangObject<Event> {
    private boolean canceled;
    private boolean cancelable;

    public Event() {
        this(false);
    }

    public Event(boolean cancelable) {
        this.cancelable = cancelable;
    }

    /**
     * Compile the event.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {

    }

    @Override
    public boolean requiresBlock() {
        return true;
    }

    public void invoke(EventStruct struct, Map<String, Object> parameters, @NotNull String[] scriptArguments) throws ScriptException {
        invoke(CodeContext.of(struct, this, parameters, scriptArguments));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        for (Effect effect : getBlockEffect()) {
            effect.invoke(context);
        }
    }

    public String getRegistryName() {
        return ScripticLang.getPatternForEvent(this);
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        canceled = true;
    }
}
