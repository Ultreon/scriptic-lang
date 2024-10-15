package dev.ultreon.scriptic;

public class ScriptException extends Exception {
    public ScriptException(int lineNr) {
        super("An error occurred (line " + lineNr + ")");
    }

    public ScriptException(String message, int lineNr) {
        super(message + " (line " + lineNr + ")");
    }

    public ScriptException(String message, int lineNr, Exception e) {
        super(message + " (line " + lineNr + ")", e);
    }

    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, Exception e) {
        super(message, e);
    }
}
