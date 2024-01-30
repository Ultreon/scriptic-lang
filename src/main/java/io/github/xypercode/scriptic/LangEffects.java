package io.github.xypercode.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.impl.effect.*;
import io.github.xypercode.scriptic.lang.obj.Effect;

public class LangEffects {
    public static final CancelEventEffect CANCEL = register(ScripticLang.id("cancel"), new CancelEventEffect());
    public static final IfBlockEffect IF_BLOCK = register(ScripticLang.id("if_block"), new IfBlockEffect());
    public static final IfEffect IF = register(ScripticLang.id("if"), new IfEffect());
    public static final ElseBlockEffect ELSE_BLOCK = register(ScripticLang.id("else_block"), new ElseBlockEffect());
    public static final ElseEffect ELSE = register(ScripticLang.id("else"), new ElseEffect());
    public static final DeleteEffect DELETE = register(ScripticLang.id("delete"), new DeleteEffect());
    public static final LogEffect LOG = register(ScripticLang.id("log"), new LogEffect());
    public static final SetVariableEffect SET_VARIABLE = register(ScripticLang.id("set_variable"), new SetVariableEffect());

    private static <T extends Effect> T register(Identifier name, T effect) {
        Registries.EFFECTS.register(name, effect);
        return effect;
    }

    public static void init() {
        // Initialize class
    }
}
