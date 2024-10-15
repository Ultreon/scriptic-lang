package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class NameOfExpr extends Expr {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Function> REGISTRY = new HashMap<>();

    static {
        register(File::getName);
        register((Path path) -> path.getFileName().toString());
        register((Locale locale) -> locale.getDisplayName());
    }

    @SafeVarargs
    public static <T> void register(Function<? super T, String> consumer, T... typeGetter) {
        REGISTRY.put(typeGetter.getClass().getComponentType(), consumer);
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("^(the |)name of (?<expr>.+)$");
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr the line number of the code.
     * @param code   the code.
     * @return the compiled code.
     */
    @Override
    public CExpr compile(int lineNr, String code) throws CompileException {
        var pattern = getPattern();

        var matcher = pattern.matcher(code);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
        final var exprCode = matcher.group("expr");
        final var expr = Registries.compileExpr(lineNr, new Parser(exprCode));

        return new CExpr(this, code, lineNr) {
            @SuppressWarnings("unchecked")
            @Override
            public CValue<?> calc(CodeContext context) throws ScriptException {
                var eval = expr.eval(context);

                var o = eval.get();

                if (o == null) {
                    return new CValue<>(null);
                }

                for (var entry : REGISTRY.entrySet()) {
                    if (entry.getKey().isInstance(o)) {
                        return new CValue<>(entry.getValue().apply(o));
                    }
                }

                throw new IllegalArgumentException("Expected to have something that has a name, got expression: " + exprCode);
            }

            @Override
            public String toString() {
                return code;
            }
        };
    }
}
