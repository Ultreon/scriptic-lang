package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.CompileException;


@SuppressWarnings("unused")
public abstract class LangObject<T extends LangObject<T>> {
    /**
     * Compiles a piece of code for this language object.
     *
     * @param lineNr the line number of the code.
     * @param code the code.
     * @return the compiled code.
     */
    public abstract CompiledCode compile(int lineNr, String code) throws CompileException;

    public boolean hasCodeBlock() {
        return false;
    }
}
