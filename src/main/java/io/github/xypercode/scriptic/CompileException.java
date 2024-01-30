package io.github.xypercode.scriptic;

public class CompileException extends Exception {
    public CompileException(String s, int line) {
        this(s, line, -1);
    }

    public CompileException(String s, int line, int col) {
        super(s + " (line " + line + (col > 0 ? ", column " + col : "") + ")");
    }

    public CompileException(String s) {
        super(s);
    }
}
