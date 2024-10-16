package dev.ultreon.scriptic.lang.obj;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.LangObject;

import java.util.regex.Matcher;

public class TimerObject extends LangObject<TimerObject> {
    private TimerLike timer;

    public void setTimer(TimerLike timer) {
        this.timer = timer;
    }

    @Override
    public String getRegistryName() {
        return "timer";
    }

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        // Implementation goes here
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        for (Effect effect : blockEffect) {
            context.setLastEffect(effect);
            context.setTimer(this.timer);
            effect.invoke(context);
        }
    }

    public void dispose() {
        if (this.timer != null)
            this.timer.cancel();
        this.timer = null;
    }

    public TimerLike getTimer() {
        if (timer == null) {
            ScripticLang.getLogger().warn("Timer is null! Fallback to Java timer");
            timer = new JavaTimer();
            return timer;
        }
        else return timer;
    }
}
