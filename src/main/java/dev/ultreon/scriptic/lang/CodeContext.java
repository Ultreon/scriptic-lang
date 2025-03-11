package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.impl.struct.EventStruct;
import dev.ultreon.scriptic.lang.obj.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface CodeContext {
    @ApiStatus.Internal
    void startLoop(Loop loop);

    Loop getCurrentLoop();

    @ApiStatus.Internal
    void endLoop();

    void setLastEffect(Effect effect);

    Effect getLastEffect();

    CodeBlock getCurrentBlock();

    @ApiStatus.Internal
    void pushBlock(CodeBlock block);

    @ApiStatus.Internal
    void popBlock();

    void cancelEvent();

    IfStatement getIfEffect(CodeBlock codeBlock);

    void setIfEffect(CodeBlock codeBlock, IfStatement ifStatement);

    default CodeBlock pushBlock(List<Effect> blockEffect) {
        return pushBlock(blockEffect, false);
    }

    default CodeBlock pushBlock(List<Effect> blockEffect, boolean breakable) {
        CodeBlock block = new CodeBlock(getCurrentBlock(), blockEffect, breakable, this, this::popBlock);
        pushBlock(block);

        return block;
    }

    boolean isEventCancelable();

    <T> T getEventParameter(String name, Class<T> type);

    static CodeContext of(EventStruct eventStruct, Event event, Map<String, Object> eventParameters, @NotNull String[] scriptArguments) {
        return new CodeContextImpl(eventStruct, event, eventParameters, scriptArguments);
    }

    static CodeContext of(Struct<?> struct) {
        return new CodeContextImpl(struct, null, null, new String[0]);
    }

    void setLastType(Type type);

    @NotNull String[] getScriptArguments();

    void setTimer(TimerLike timer);

    TimerLike getTimer();
}
