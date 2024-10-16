package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Event;
import dev.ultreon.scriptic.lang.obj.Struct;
import dev.ultreon.scriptic.lang.obj.TimerLike;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CodeContextImpl implements CodeContext {
    private final Stack<Loop> loops = new Stack<>();
    private final Stack<CodeBlock> blockStack = new Stack<>();
    private final Map<CodeBlock, IfStatement> ifs = new HashMap<>();
    private final Map<String, Object> eventParameters;
    private final @NotNull String[] scriptArguments;
    private final Struct<?> structure;
    private Effect lastEffect;
    private TimerLike timer;
    private Event event;

    public CodeContextImpl(Struct<?> structure, Event event, Map<String, Object> eventParameters, @NotNull String[] scriptArguments) {
        this.structure = structure;
        this.eventParameters = eventParameters;
        this.scriptArguments = scriptArguments;
    }

    @Override
    public void startLoop(Loop loop) {
        loops.push(loop);
    }

    @Override
    public Loop getCurrentLoop() {
        return loops.peek();
    }

    @Override
    public void endLoop() {
        loops.pop();
    }

    @Override
    public void setLastEffect(Effect effect) {
        this.getCurrentBlock().setLastEffect(effect);
    }

    @Override
    public Effect getLastEffect() {
        return lastEffect;
    }

    @Override
    public CodeBlock getCurrentBlock() {
        return blockStack.isEmpty() ? null : blockStack.peek();
    }

    @Override
    public void pushBlock(CodeBlock block) {
        blockStack.push(block);
    }

    @Override
    public void popBlock() {
        blockStack.pop();
    }

    @Override
    public void cancelEvent() {
        if (this.event != null) {
            this.event.cancel();
            return;
        }

        throw new IllegalStateException("Not in an event");
    }

    @Override
    public IfStatement getIfEffect(CodeBlock codeBlock) {
        return ifs.get(codeBlock);
    }

    @Override
    public void setIfEffect(CodeBlock codeBlock, IfStatement ifStatement) {
        ifs.put(codeBlock, ifStatement);
    }

    @Override
    public boolean isEventCancelable() {
        if (this.event != null) {
            return this.event.isCancelable();
        }
        return false;
    }

    @Override
    public <T> T getEventParameter(String name, Class<T> type) {
        return type.cast(eventParameters.get(name));
    }

    @Override
    public void setLastType(Type type) {
        this.getCurrentBlock().setLastType(type);
    }

    @Override
    public @NotNull String[] getScriptArguments() {
        return scriptArguments;
    }

    @Override
    public void setTimer(TimerLike timer) {
        this.timer = timer;
    }

    void setCurrentBlock(CodeBlock codeBlock) {
        blockStack.push(codeBlock);
    }

    @Override
    public TimerLike getTimer() {
        return timer;
    }

    public Struct<?> getStructure() {
        return structure;
    }
}
