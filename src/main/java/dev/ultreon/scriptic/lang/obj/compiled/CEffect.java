package dev.ultreon.scriptic.lang.obj.compiled;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.CompiledCode;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public abstract class CEffect extends CompiledCode {
    private final Effect base;
    private final String code;
    private final int lineNr;

    protected CEffect(Effect base, String code, int lineNr) {
        this.base = base;
        this.code = code;
        this.lineNr = lineNr;
    }

    public static CEffect ofCodeBlock(int lineNr, String code) throws CompileException {
        return ofCodeBlock(lineNr, code, (CodeBlock) null);
    }

    @Deprecated
    public static CEffect ofCodeBlock(int lineNr, String code, CodeContext blockContext) throws CompileException {
        return ofCodeBlock(lineNr, code, null, blockContext);
    }

    @Deprecated
    public static CEffect ofCodeBlock(int lineNr, String code, CodeBlock parent) throws CompileException {
        return ofCodeBlock(lineNr, code, parent);
    }

    @Deprecated
    public static CEffect ofCodeBlock(int lineNr, String code, CodeBlock parent, CodeContext blockContext) throws CompileException {
        var parser = new Parser(code);

        List<CEffect> effects = bulkCompile(lineNr, code);

        return new CEffect(null, code, lineNr) {
            @Override
            protected void run(CodeBlock codeBlock, CodeContext context1) throws ScriptException {
                for (var effect : effects) {
                    effect.invoke(codeBlock, context1);
                }
            }
        };
    }

    public static List<CEffect> compileNextBlock(int lineNr, Parser parser) throws CompileException {
        return bulkCompile(lineNr, parser.readIndentedBlock(2));
    }

    public static List<CEffect> bulkCompile(int lineNr, String code) throws CompileException {
        var parser = new Parser(code);

        List<CEffect> effects = new ArrayList<>();

        while (!parser.isEOF()) {
            var effectFor = Registries.compileEffect(lineNr + parser.row(), parser);
            if (effectFor == null) {
                continue;
            }
            effects.add(effectFor);
        }
        return effects;
    }

    protected abstract void run(CodeBlock codeBlock, CodeContext context) throws ScriptException;

    @Deprecated
    public void invoke(CodeBlock codeBlock, CodeContext context) throws ScriptException {
        try {
            run(codeBlock, context);
            codeBlock.context().setLastEffect(this);
        } catch (Exception e) {
            if (e instanceof ScriptException scriptException) {
                throw scriptException;
            }
            throw new ScriptException(e.getMessage(), lineNr);
        }
    }

    public final Effect getBase() {
        return base;
    }

    public String getCode() {
        return code;
    }

    public final int getLineNr() {
        return lineNr;
    }

    public void invoke(CodeContext context) throws ScriptException {
        this.invoke(context.getCurrentBlock(), context);
    }
}
