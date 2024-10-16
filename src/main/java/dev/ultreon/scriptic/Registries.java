package dev.ultreon.scriptic;

import dev.ultreon.scriptic.impl.struct.EventStruct;
import dev.ultreon.scriptic.lang.Type;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Event;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registries {
    public static final Registry<Type> TYPES = Registry.create(new Identifier("scriptic_lang", "type"));
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
    public static @Nullable EventStruct compileEvent(int lineNr, Parser parser) throws CompileException {
        var parsed = parser.readLine();

        if (parsed.isBlank()) {
            return null;
        }
        if (Pattern.matches("^on .+:?$", parsed)) {
            var eventExpr = parsed.substring(0, parsed.length() - 1);
            @Nullable Event detected = detectEvent(eventExpr);
            if (detected == null) {
                throw new CompileException("Invalid event: " + parsed, lineNr, parser.col());
            }

            var block = parser.readIndentedBlock();

            return new EventStruct(detected, block);
        } else {
            throw new CompileException("Expected to find a colon at the end of the line, which means it's a event.", parser.row(), parser.col());
        }
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
    private static Event detectEvent(String code) {
        return ScripticLang.getEvent(code);
    }

    @SafeVarargs
    public static <T> Expr<T> compileExpr(int lineNr, String expr, T... typeGetter) throws CompileException {
        return compileExpr(lineNr, new Parser(expr), typeGetter);
    }

    public static <T> Effect compileEffect(int lineNr, String effect) throws CompileException {
        return compileEffect(lineNr, new Parser(effect));
    }

    public static EventStruct compileEvent(int lineNr, String event) throws CompileException {
        return compileEvent(lineNr, new Parser(event));
    }
}
