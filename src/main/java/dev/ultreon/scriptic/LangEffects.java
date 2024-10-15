package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.impl.effect.*;
import dev.ultreon.scriptic.impl.effect.*;
import dev.ultreon.scriptic.lang.obj.Effect;

public class LangEffects {
    public static final CancelEventEffect CANCEL = register(ScripticLang.id("cancel"), new CancelEventEffect());
    public static final IfBlockEffect IF_BLOCK = register(ScripticLang.id("if_block"), new IfBlockEffect());
    public static final IfEffect IF = register(ScripticLang.id("if"), new IfEffect());
    public static final ElseBlockEffect ELSE_BLOCK = register(ScripticLang.id("else_block"), new ElseBlockEffect());
    public static final ElseEffect ELSE = register(ScripticLang.id("else"), new ElseEffect());
    public static final LoopTimesEffect LOOP_TIMES = register(ScripticLang.id("loop_times"), new LoopTimesEffect());
    public static final ForEffect FOR = register(ScripticLang.id("for"), new ForEffect());
    public static final WhileEffect WHILE = register(ScripticLang.id("while"), new WhileEffect());
    public static final BreakTheLoopEffect BREAK_THE_LOOP = register(ScripticLang.id("break_the_loop"), new BreakTheLoopEffect());
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
