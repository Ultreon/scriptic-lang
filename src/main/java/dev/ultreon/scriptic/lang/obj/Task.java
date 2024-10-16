package dev.ultreon.scriptic.lang.obj;

public class Task implements Runnable {
    private final Runnable task;
    volatile boolean canceled = false;
    volatile Runnable cancelCallback;

    public Task(Runnable task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.run();
    }

    public void cancel() {
        if (cancelCallback != null) cancelCallback.run();
        else canceled = true;
    }
}
