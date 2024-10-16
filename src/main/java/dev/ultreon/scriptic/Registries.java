package dev.ultreon.scriptic;

import dev.ultreon.scriptic.lang.Type;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Event;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.Struct;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registries {
    public static final Registry<Type> TYPES = Registry.create(new Identifier("scriptic_lang", "type"));
    public static final Registry<Struct<?>> STRUCTS = Registry.create(new Identifier("scriptic_lang", "structure"));
    public static final Registry<Expr<?>> EXPRESSIONS = Registry.create(new Identifier("scriptic_lang", "expression"));
    public static final Registry<Effect> EFFECTS = Registry.create(new Identifier("scriptic_lang", "effect"));

    /**
     * Detect and return the type for the given code.
     *
     * @param code the code to detect the type for
     * @return the detected type.
     */
    public static Type getTypeFor(String code) {
        return ScripticLang.getType(code);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr the line number at the top of the code given.
     * @param parser the parser instance for the code.
     * @return the compiled expression.
     */
    @SafeVarargs
    @UnknownNullability
    public static <T> Expr<T> compileExpr(int lineNr, Parser parser, T... typeGetter) throws CompileException {
        @SuppressWarnings("unchecked") Class<T> type = (Class<T>) typeGetter.getClass().getComponentType();

        var parsed = parser.readLine();
        if (parsed.isBlank()) {
            return null;
        }

        @Nullable Expr detected = detectExpr(parsed);
        if (detected == null) {
            throw new CompileException("Unknown expression: " + parsed, lineNr, parser.col());
        }
        if (detected.requiresBlock()) {
            parsed += "\n" + parser.readIndentedBlock(2, true);
        }

        detected.preload(lineNr, parsed);
        Matcher matcher = detected.getPattern().matcher(parser.getRow(parser.row()));
        if (!matcher.matches()) {
            throw new CompileException("Invalid expression format: " + parsed, lineNr, parser.col());
        }
        detected.load(lineNr, matcher);

        //noinspection unchecked
        return (Expr<T>) detected.verifyInstanceOf(type);
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr the line number at the top of the code given.
     * @param parser the parser instance for the code.
     * @return the compiled effect.
     */
    public static @Nullable Effect compileEffect(int lineNr, Parser parser) throws CompileException {
        int col = parser.col();
        var parsed = parser.readLine().trim();
        if (parsed.isBlank()) {
            return null;
        }

        @Nullable Effect detected = detectEffect(parsed);
        if (detected == null) {
            throw new CompileException("Unknown effect: " + parsed, lineNr, col);
        }
        if (detected.requiresBlock()) {
            parsed += "\n" + parser.readIndentedBlock(2, true);
        }
        return detected;
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr the line number at the top of the code given.
     * @param parser the parser instance for the code.
     * @return the compiled expression.
     */
    public static @Nullable Struct<?> compileStruct(int lineNr, Parser parser) throws CompileException {
        var parsed = parser.readLine();

        if (parsed.isBlank()) {
            return null;
        }

        @Nullable Struct detected = detectStruct(parsed);
        if (detected == null) {
            throw new CompileException("Unknown structure: " + parsed, lineNr, parser.col());
        }

        var block = parser.readIndentedBlock();
        detected.preload(lineNr, parsed);
        if (detected.requiresBlock()) {
            if (block.isBlank()) {
                throw new CompileException("Missing block for struct: " + parsed, lineNr, parser.col());
            }
            detected.loadBlock(lineNr, block);
        }
        Matcher matcher = Pattern.compile(detected.getRegistryName()).matcher(parsed);
        if (!matcher.matches()) {
            throw new CompileException("Invalid structure format: " + parsed, lineNr, parser.col());
        }
        detected.load(lineNr, matcher);
        if (detected.getDetected() == null) {
            throw new CompileException("Invalid structure: " + parsed, lineNr, parser.col());
        }

        return detected;
    }

    private static @Nullable Struct detectStruct(String parsed) {
        var keys = STRUCTS.keys();
        @Nullable Struct detected = null;
        for (var key : keys) {
            if (Pattern.matches(key, parsed)) {
                detected = STRUCTS.create(key);
                break;
            }
        }
        return detected;
    }

    /**
     * Detects the expression for the given code.
     *
     * @param code the code to detect the expression for
     * @return the detected expression.
     */
    @Nullable
    private static Expr detectExpr(String code) {
        var keys = EXPRESSIONS.keys();
        @Nullable Expr detected = null;
        for (var key : keys) {
            if (Pattern.matches(key, code)) {
                detected = EXPRESSIONS.create(key);
                break;
            }
        }
        return detected;
    }

    /**
     * Detects the effect for the given code.
     *
     * @param code the code to detect the effect for
     * @return the detected effect.
     */
    @Nullable
    private static Effect detectEffect(String code) {
        var keys = EFFECTS.keys();
        @Nullable Effect detected = null;
        for (var key : keys) {
            if (Pattern.matches(key, code)) {
                detected = EFFECTS.create(key);
                break;
            }
        }
        return detected;
    }

    /**
     * Detects the expression for the given code.
     *
     * @param code the code to detect the expression for
     * @return the detected expression.
     */
    @Nullable
    public static Event detectEvent(String code) {
        return ScripticLang.getEvent(code);
    }

    @SafeVarargs
    public static <T> Expr<T> compileExpr(int lineNr, String expr, T... typeGetter) throws CompileException {
        return compileExpr(lineNr, new Parser(expr), typeGetter);
    }

    public static <T> Effect compileEffect(int lineNr, String effect) throws CompileException {
        return compileEffect(lineNr, new Parser(effect));
    }

    public static Struct<?> compileStruct(int lineNr, String event) throws CompileException {
        return compileStruct(lineNr, new Parser(event));
    }
}
