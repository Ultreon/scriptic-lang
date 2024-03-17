package io.github.xypercode.scriptic.impl;

public class StdOutLogger implements Logger {
    @Override
    public void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }

    @Override
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void error(String message) {
        System.out.println("[ERROR] " + message);
    }

    @Override
    public void error(String message, Throwable e) {
        System.out.println("[ERROR] " + message);
        e.printStackTrace();
    }

    @Override
    public void warn(String message) {
        System.out.println("[WARN] " + message);
    }

    @Override
    public void warn(String message, Throwable e) {
        System.out.println("[WARN] " + message);
        e.printStackTrace();
    }
}
