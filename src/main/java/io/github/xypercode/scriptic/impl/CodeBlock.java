package io.github.xypercode.scriptic.impl;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.ScripticLang;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class CodeBlock {
    public static final Identifier CTX_LAST_EXPR_VALUE = ScripticLang.id("expr/last_expr_value");
    public static final Identifier CTX_LAST_EFFECT = ScripticLang.id("effect/last_effect");
    public static final Identifier CTX_LAST_IF_SUCCEEDED = ScripticLang.id("effect/last/if/succeeded");
    private final @Nullable CodeBlock parent;
    private final List<CEffect> contents;
    private final CodeContext context;
    private boolean executing;

    public CodeBlock(@Nullable CodeBlock parent, List<CEffect> contents, CodeContext context) {
        this.parent = parent;
        this.contents = contents;
        this.context = context;
    }

    public void invoke() throws ScriptException {
        if (this.executing)
            return;

        this.executing = true;

        try {
            for (CEffect effect : contents) {
                effect.invoke(parent, context);
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

    public List<CEffect> contents() {
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

}
