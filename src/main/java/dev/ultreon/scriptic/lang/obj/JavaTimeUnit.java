package dev.ultreon.scriptic.lang.obj;

import java.util.concurrent.TimeUnit;

public enum JavaTimeUnit implements TimeUnitLike {
    MILLISECONDS(TimeUnit.MILLISECONDS),
    SECONDS(TimeUnit.SECONDS),
    MINUTES(TimeUnit.MINUTES),
    HOURS(TimeUnit.HOURS),
    DAYS(TimeUnit.DAYS);

    private final TimeUnit unit;

    JavaTimeUnit(TimeUnit unit) {
        this.unit = unit;
    }

    @Override
    public long toMillis(long value) {
        return unit.toMillis(value);
    }
}
