package io.github.xypercode.scriptic.lang.obj.compiled;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.CompiledCode;
import io.github.xypercode.scriptic.lang.EventData;
import io.github.xypercode.scriptic.lang.obj.Event;

import java.util.*;

public final class CEvent extends CompiledCode {
    private final Event listensTo;

    private final List<Identifier> requiresProps;
    private final List<CEffect> content;
    private final EventData data;

    public CEvent(Event listensTo, List<Identifier> requiredProps, List<CEffect> content) {
        this(listensTo, requiredProps, content, new EventData());
    }

    public CEvent(Event listensTo, List<Identifier> requiredProps, List<CEffect> content, EventData data) {
        this.listensTo = listensTo;
        this.requiresProps = requiredProps;
        this.content = content;
        this.data = data;
    }

    public void call(CEffect effect, Map<Identifier, Object> properties) throws ScriptException {
        try {
            effect.run(new CodeBlock(null, content, CodeContext.of(properties)), makeProperties(properties));
        } catch (Exception e) {
            if (e instanceof ScriptException scriptException) {
                throw scriptException;
            }
            throw new ScriptException(e.getMessage(), effect.getLineNr(), e);
        }
    }

    private CodeContext makeProperties(Map<Identifier, Object> properties) {
        var missing = new ArrayList<>(requiresProps);
        var result = new HashMap<Identifier, Object>();
        for (var entry : properties.entrySet()) {
            missing.remove(entry.getKey());
            result.put(entry.getKey(), entry.getValue());
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties: " + missing);
        }

        return CodeContext.of(result);
    }

    public Event getListensTo() {
        return listensTo;
    }

    public CEffect getEffect() {
        if (content.isEmpty()) return null;
        return content.get(0);
    }

    public EventData getData() {
        return data;
    }
}
