package dev.ultreon.scriptic.impl;

public class StdOutLogger implements Logger {
    @Override
    public void debug(String message) {
        System.err.println("[DEBUG] " + message);
    }

    @Override
    public void info(String message) {
        System.err.println("[INFO] " + message);
    }

    @Override
    public void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    @Override
    public void error(String message, Throwable e) {
        System.err.println("[ERROR] " + message);
        e.printStackTrace();
    }

    @Override
    public void warn(String message) {
        System.err.println("[WARN] " + message);
    }

    @Override
    public void warn(String message, Throwable e) {
        System.err.println("[WARN] " + message);
        e.printStackTrace();
    }

    @Override
    public void print(String message) {
        System.out.println(message);
    }
}
