package io.github.xypercode.scriptic.impl;

public interface Logger {

    void debug(String message);

    void info(String message);

    void error(String message);

    void error(String message, Throwable e);

    void warn(String message);

    void warn(String message, Throwable e);
}
