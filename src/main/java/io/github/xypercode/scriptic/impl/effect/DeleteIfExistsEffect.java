package io.github.xypercode.scriptic.impl.effect;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class DeleteIfExistsEffect extends Effect {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Consumer> REGISTRY = new HashMap<>();

    static {
        register((File file) -> {
            if (file.exists()) file.deleteOnExit();
        });
        register((Path path) -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        });
    }

    @SafeVarargs
    public static <T> void register(Consumer<? super T> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(delete|discard|remove) (?<expr>.+) if ((it )?exists|existent)$");
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr the line number of the code.
     * @param code   the code.
     * @return the compiled code.
     */
    @Override
    public CEffect compile(int lineNr, String code) throws CompileException {
        var pattern = Pattern.compile("^(delete|discard|remove) (?<expr>.+)|$");

        var matcher = pattern.matcher(code);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
        var effectCode = matcher.group("expr");
        var expr = Registries.compileExpr(lineNr, new Parser(effectCode));

        return new CEffectImpl(code, lineNr, expr);
    }

    private class CEffectImpl extends CEffect {
        private final String code;
        private final CExpr expr;

        public CEffectImpl(String code, int lineNr, CExpr expr) {
            super(DeleteIfExistsEffect.this, code, lineNr);
            this.code = code;
            this.expr = expr;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
            if (expr != null) {
                var eval = expr.eval(codeBlock, context);
                var o = eval.get();
                if (o == null) {
                    throw new IllegalArgumentException("Cannot delete nothing, got " + expr);
                }

                for (var entry : REGISTRY.entrySet()) {
                    if (entry.getKey().isInstance(o)) {
                        entry.getValue().accept(o);
                    }
                }

                throw new IllegalArgumentException("Expected to find something deletable, but got " + expr);
            }
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
