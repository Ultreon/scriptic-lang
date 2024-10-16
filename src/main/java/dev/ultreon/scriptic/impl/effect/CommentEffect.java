package dev.ultreon.scriptic.impl.effect;

import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import org.intellij.lang.annotations.RegExp;

public class CommentEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^//.*$";

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        // Comments are ignored
    }
}
