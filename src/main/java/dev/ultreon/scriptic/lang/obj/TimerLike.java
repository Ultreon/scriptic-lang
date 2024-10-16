package dev.ultreon.scriptic.lang.obj;

public interface TimerLike {
    void schedule(Task task, long delay, long period);

    void schedule(Task task, long delay);

    void schedule(Task task, long delay, TimeUnitLike unit);

    void schedule(Task task);

    void cancel();
}
