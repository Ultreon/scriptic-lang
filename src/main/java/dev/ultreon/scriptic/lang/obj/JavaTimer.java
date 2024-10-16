package dev.ultreon.scriptic.lang.obj;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class JavaTimer implements TimerLike {
    private final Timer timer;

    public JavaTimer() {
        this.timer = new Timer();
    }

    @Override
    public void schedule(Task task, long delay, long period) {
        this.timer.schedule(createTask(task), delay, period);
    }

    @Override
    public void schedule(Task task, long delay) {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delay);
    }

    @Override
    public void schedule(Task task, long delay, TimeUnitLike unit) {
        this.timer.schedule(createTask(task), unit.toMillis(delay));
    }

    @Override
    public void schedule(Task task) {
        this.timer.schedule(createTask(task), 0);
    }

    private static @NotNull TimerTask createTask(Task task) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        task.cancelCallback = timerTask::cancel;
        if (task.canceled) timerTask.cancel();
        return timerTask;
    }

    @Override
    public void cancel() {
        this.timer.cancel();
    }
}
