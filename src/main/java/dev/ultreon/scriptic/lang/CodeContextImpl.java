package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.obj.compiled.CEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CodeContextImpl implements CodeContext {
    private final Stack<Loop> loops = new Stack<>();
    private final Stack<CodeBlock> blockStack = new Stack<>();
    private final Map<CodeBlock, IfStatement> ifs = new HashMap<>();
    private final Map<String, Object> eventParameters;
    private CEffect lastEffect;
    private CEvent event;

    public CodeContextImpl(CEvent event, Map<String, Object> eventParameters) {
        this.event = event;
        this.eventParameters = eventParameters;
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
    public void setLastEffect(CEffect cEffect) {
        this.lastEffect = cEffect;
    }

    @Override
    public CEffect getLastEffect() {
        return lastEffect;
    }

    @Override
    public CodeBlock getCurrentBlock() {
        return blockStack.peek();
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
        this.event.cancel();
    }

    @Override
    public IfStatement getIfStatment(CodeBlock codeBlock) {
        return ifs.get(codeBlock);
    }

    @Override
    public void setIfStatment(CodeBlock codeBlock, IfStatement ifStatement) {
        ifs.put(codeBlock, ifStatement);
    }

    @Override
    public boolean isEventCancellable() {
        return event.isCancellable();
    }

    @Override
    public <T> T getEventParameter(String name, Class<T> type) {
        return type.cast(eventParameters.get(name));
    }

    void setCurrentBlock(CodeBlock codeBlock) {
        blockStack.push(codeBlock);
    }
}
