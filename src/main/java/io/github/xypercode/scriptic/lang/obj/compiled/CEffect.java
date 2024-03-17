package io.github.xypercode.scriptic.lang.obj.compiled;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.CodeContextImpl;
import io.github.xypercode.scriptic.lang.CompiledCode;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CEffect extends CompiledCode {
    private final Effect base;
    private final String code;
    private final int lineNr;
    private final CodeContext context;

    protected CEffect(Effect base, String code, int lineNr) {
        this(base, code, lineNr, new CodeContextImpl());
    }

    protected CEffect(Effect base, String code, int lineNr, CodeContext context) {
        this.base = base;
        this.code = code;
        this.lineNr = lineNr;
        this.context = context;
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
        return ofCodeBlock(lineNr, code, parent, new CodeContextImpl());
    }

    @Deprecated
    public static CEffect ofCodeBlock(int lineNr, String code, CodeBlock parent, CodeContext blockContext) throws CompileException {
        var parser = new Parser(code);

        List<CEffect> effects = bulkCompile(lineNr, code);

        return new CEffect(null, code, lineNr, blockContext) {
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

    public void invoke(CodeBlock codeBlock, CodeContext context) throws ScriptException {
        try {
            run(codeBlock, context);
            codeBlock.context().set(CodeBlock.CTX_LAST_EFFECT, this);
        } catch (Exception e) {
            if (e instanceof ScriptException scriptException) {
                throw scriptException;
            }
            throw new ScriptException(e.getMessage(), lineNr);
        }
    }

    public void invoke(CodeBlock codeBlock, Map<Identifier, Object> properties) throws ScriptException {
        run(codeBlock, CodeContext.of(properties));
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

    public CodeContext context() {
        return context;
    }
}
