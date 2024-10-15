package dev.ultreon.scriptic.lang.obj.compiled;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CompiledCode;
import dev.ultreon.scriptic.lang.EventData;
import dev.ultreon.scriptic.lang.obj.Event;

import java.util.*;

public final class CEvent extends CompiledCode {
    private final Event listensTo;

    private final List<Identifier> requiresProps;
    private final List<CEffect> content;
    private final EventData data;
    private boolean cancelable;
    private final ThreadLocal<Boolean> canceled = ThreadLocal.withInitial(() -> false);

    public CEvent(Event listensTo, List<Identifier> requiredProps, List<CEffect> content) {
        this(listensTo, requiredProps, content, new EventData());
    }

    public CEvent(Event listensTo, List<Identifier> requiredProps, List<CEffect> content, EventData data) {
        this.listensTo = listensTo;
        this.requiresProps = requiredProps;
        this.content = content;
        this.data = data;

        this.cancelable = listensTo.isCancelable();
    }

    public void call(Map<String, Object> properties) throws ScriptException {
        try {
            canceled.remove();
            CodeBlock.ofEvent(this, makeProperties(properties), content).invoke();
        } catch (Exception e) {
            if (e instanceof ScriptException scriptException) {
                throw scriptException;
            }
            throw new ScriptException(e.getMessage(), content.get(0).getLineNr(), e);
        }
    }

    private Map<String, Object> makeProperties(Map<String, Object> properties) {
        var missing = new ArrayList<>(requiresProps);
        var result = new HashMap<String, Object>();
        for (var entry : properties.entrySet()) {
            missing.remove(entry.getKey());
            result.put(entry.getKey(), entry.getValue());
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties: " + missing);
        }

        return properties;
    }

    public Event getListensTo() {
        return listensTo;
    }

    public EventData getData() {
        return data;
    }

    public boolean isCancellable() {
        return cancelable;
    }

    public void cancel() {
        cancelable = false;
        this.canceled.set(true);
    }
}
