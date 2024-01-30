package io.github.xypercode.scriptic.impl;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.ScriptException;

import java.io.IOException;
import java.nio.file.Path;

public final class ScriptEngine {
    private final boolean allowsExternalCode;

    private ScriptEngine(Builder builder) {
        this.allowsExternalCode = builder.allowExternalCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean allowsExternalScripts() {
        return allowsExternalCode;
    }

    public void importScripts() throws ScriptException, CompileException, IOException {
        Script.importScripts(this);
    }

    public Script importScript(Path path) throws ScriptException, CompileException, IOException {
        return Script.importScript(path, this);
    }

    public static class Builder {
        private boolean allowExternalCode = true;

        private Builder() {

        }

        public Builder blockExternalScripts() {
            allowExternalCode = false;
            return this;
        }

        public ScriptEngine build() {
            return new ScriptEngine(this);
        }
    }
}
