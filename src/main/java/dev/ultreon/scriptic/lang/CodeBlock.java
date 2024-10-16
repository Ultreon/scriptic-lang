package dev.ultreon.scriptic.lang;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.TimerLike;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.List;
import java.util.Objects;

public final class CodeBlock implements Closeable {
    public static final Identifier CTX_LAST_EXPR_VALUE = ScripticLang.id("expr/last_expr_value");
    public static final Identifier CTX_LAST_EFFECT = ScripticLang.id("effect/last_effect");
    public static final Identifier CTX_LAST_IF_SUCCEEDED = ScripticLang.id("effect/last/if/succeeded");
    private final @Nullable CodeBlock parent;
    private final List<Effect> contents;
    private final CodeContext context;
    private final Runnable popBlock;
    private final boolean breakable;
    private boolean executing;
    private Effect lastEffect;
    private Effect currentEffect;
    private Object lastExprValue;
    private IfStatement ifStatement;
    private String answer = null;
    private Type lastType;

    CodeBlock(@Nullable CodeBlock parent, List<Effect> contents, CodeContextImpl context, Runnable popBlock) {
        this.parent = parent;
        this.contents = contents;
        this.context = context;
        this.popBlock = popBlock;
        this.breakable = false;

        context.setCurrentBlock(this);
    }

    CodeBlock(@Nullable CodeBlock parent, List<Effect> contents, boolean breakable, CodeContext context, Runnable popBlock) {
        this.parent = parent;
        this.contents = contents;
        this.context = context;
        this.popBlock = popBlock;
        this.breakable = breakable;
    }

    public void invoke() throws ScriptException {
        if (this.executing)
            return;

        this.executing = true;

        try {
            for (Effect effect : contents) {
                this.lastEffect = currentEffect;
                this.currentEffect = effect;
                if (this.ifStatement != null) {
                    effect.invoke(context);
                    this.ifStatement = null;
                } else {
                    effect.invoke(context);
                }
            }
        } catch (ScriptException e) {
            this.executing = false;
            throw e;
        }

        this.executing = false;
    }

    public @Nullable CodeBlock parent() {
        return parent;
    }

    public List<Effect> contents() {
        return contents;
    }

    public CodeContext context() {
        return context;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CodeBlock) obj;
        return Objects.equals(this.parent, that.parent) &&
               Objects.equals(this.contents, that.contents) &&
               Objects.equals(this.context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, contents, context);
    }

    @Override
    public String toString() {
        return "CodeBlock[" +
               "parent=" + parent + ", " +
               "contents=" + contents + ", " +
               "context=" + context + ']';
    }

    public Effect getLastEffect() {
        return lastEffect;
    }

    public void setLastExprValue(Object value) {
        this.lastExprValue = value;
    }

    public Object getLastExprValue() {
        return lastExprValue;
    }

    @Override
    public void close() {
        this.popBlock.run();
    }

    public IfStatement getIfStatement() {
        return ifStatement;
    }

    public void setIfStatement(IfStatement ifStatement) {
        this.ifStatement = ifStatement;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setLastEffect(Effect lastEffect) {
        this.lastEffect = lastEffect;
    }

    public void setCurrentEffect(Effect currentEffect) {
        this.currentEffect = currentEffect;
    }

    public Effect getCurrentEffect() {
        return currentEffect;
    }

    public void setLastType(Type type) {
        this.lastType = type;
    }

    public Type getLastType() {
        return lastType;
    }
}
