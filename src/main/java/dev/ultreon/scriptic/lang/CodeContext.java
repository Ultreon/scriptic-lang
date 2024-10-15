package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface CodeContext {
    @ApiStatus.Internal
    void startLoop(Loop loop);

    Loop getCurrentLoop();

    @ApiStatus.Internal
    void endLoop();

    void setLastEffect(CEffect cEffect);

    CEffect getLastEffect();

    CodeBlock getCurrentBlock();

    @ApiStatus.Internal
    void pushBlock(CodeBlock block);

    @ApiStatus.Internal
    void popBlock();

    void cancelEvent();

    IfStatement getIfStatment(CodeBlock codeBlock);

    void setIfStatment(CodeBlock codeBlock, IfStatement ifStatement);

    default CodeBlock pushBlock(List<CEffect> blockEffect) {
        return pushBlock(blockEffect, false);
    }

    default CodeBlock pushBlock(List<CEffect> blockEffect, boolean breakable) {
        CodeBlock block = new CodeBlock(getCurrentBlock(), blockEffect, breakable, this, this::popBlock);
        pushBlock(block);

        return block;
    }

    boolean isEventCancellable();

    <T> T getEventParameter(String name, Class<T> type);
}
