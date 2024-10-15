package dev.ultreon.scriptic.lang;

public class IfStatement {
    private boolean succeeded = false;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void success() {
        succeeded = true;
    }
}
