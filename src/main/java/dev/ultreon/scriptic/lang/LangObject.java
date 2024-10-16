package dev.ultreon.scriptic.lang;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.obj.Effect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("unused")
public abstract class LangObject<T extends LangObject<T>> {
    private String code;
    protected String block;
    protected List<Effect> blockEffect;
    private int lineNr;
    private Pattern pattern;

    public final Pattern getPattern() {
        if (pattern != null) return pattern;

        var name = getRegistryName();
        var pattern = Pattern.compile(name);
        this.pattern = pattern;
        return pattern;
    }

    public abstract String getRegistryName();

    /**
     * Compiles a piece of code for this language object.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     * @return the compiled code.
     */
    public abstract void load(int lineNr, Matcher matcher) throws CompileException;

    public void preload(int lineNr, String code) {
        this.lineNr = lineNr;
        this.code = code;
    }

    public void loadBlock(int lineNr, String code) throws CompileException {
        this.block = code;
        this.blockEffect = Effect.bulkCompile(lineNr, code);
    }

    public boolean requiresBlock() {
        return false;
    }

    public abstract void invoke(CodeContext context) throws ScriptException;

    public String code() {
        return code;
    }

    public int getLineNr() {
        return lineNr;
    }

    public List<Effect> getBlockEffect() throws ScriptException {
        if (blockEffect == null) {
            throw new ScriptException("There's no block here: " + code, lineNr);
        }
        return blockEffect;
    }

    @Override
    public String toString() {
        return lineNr + ": " + code;
    }
}
