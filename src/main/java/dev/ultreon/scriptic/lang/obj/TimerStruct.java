package dev.ultreon.scriptic.lang.obj;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.lang.CodeContext;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class TimerStruct extends Struct<TimerObject> {
    @RegExp
    public static final String PATTERN = "^every (?<time>\\d+) (?<unit>.+):$";
    public static final TimerObject TIMER = new TimerObject();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(TIMER::dispose));
    }

    private long time;
    private TimeUnitLike unit;

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        String timeGroup = matcher.group("time");
        String unitGroup = matcher.group("unit");

        try {
            time = Long.parseLong(timeGroup);
        } catch (NumberFormatException e) {
            throw new CompileException("Invalid time amount " + timeGroup, lineNr);
        }
        unit = ScripticLang.getTimeUnit(unitGroup);

        if (unit == null)
            throw new CompileException("Invalid time unit " + unitGroup, lineNr);

        super.load(lineNr, matcher);
    }

    @Override
    public void registerListener() {
        TimerLike timer = TimerStruct.TIMER.getTimer();
        Task task = new Task(() -> {
            try {
                invoke(CodeContext.of(this));
            } catch (ScriptException e) {
                ScripticLang.getLogger().error(e.getMessage(), e);
            }
        });
        timer.schedule(task, time, unit);
    }

    @Override
    public @Nullable TimerObject detect() {
        return TIMER;
    }

    @Override
    public String getRegistryName() {
        return PATTERN;
    }
}
