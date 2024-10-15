package dev.ultreon.scriptic;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.Registry;
import dev.ultreon.scriptic.lang.Type;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Event;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.obj.compiled.CEvent;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Pattern;

public class Registries {
    public static final Registry<Type> TYPES = Registry.create(new Identifier("scriptic_lang", "type"));
    public static final Registry<Expr> EXPRESSIONS = Registry.create(new Identifier("scriptic_lang", "expression"));
    public static final Registry<Effect> EFFECTS = Registry.create(new Identifier("scriptic_lang", "effect"));
    public static final Registry<Event> EVENTS = Registry.create(new Identifier("scriptic_lang", "event"));

    /**
     * Detect and return the type for the given code.
     *
     * @param code the code to detect the type for
     * @return the detected type.
     */
    public static Type getTypeFor(String code) {
        var values = TYPES.values();
        for (var expr : values) {
            if (expr.isValid(code)) {
                return expr;
            }
        }
        return null;
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr the line number at the top of the code given.
     * @param parser the parser instance for the code.
     * @return the compiled expression.
     */
    @UnknownNullability
    public static CExpr compileExpr(int lineNr, Parser parser) throws CompileException {
        var parsed = parser.readLine();
        if (parsed.isBlank()) {
            return null;
        }

        @Nullable Expr detected = detectExpr(parsed);
        if (detected == null) {
            throw new CompileException("Code not suitable here: " + parsed, lineNr, parser.col());
        }
        if (detected.hasCodeBlock()) {
            parsed += "\n" + parser.readIndentedBlock(2, true);
        }
        try {
            return detected.compile(lineNr, parsed);
        } catch (CompileException e) {
            throw e;
        } catch (Exception e) {
            throw new CompileException(e.getMessage(), parser.row(), parser.col());
        }
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr the line number at the top of the code given.
     * @param parser the parser instance for the code.
     * @return the compiled effect.
     */
    @UnknownNullability
    public static CEffect compileEffect(int lineNr, Parser parser) throws CompileException {
        var parsed = parser.readLine();
        if (parsed.isBlank()) {
            return null;
        }

        @Nullable Effect detected = detectEffect(parsed);
        if (detected == null) {
            throw new CompileException("Code not suitable here: " + parsed, lineNr, parser.col());
        }
        if (detected.hasCodeBlock()) {
            parsed += "\n" + parser.readIndentedBlock(2, true);
        }
        try {
            return detected.compile(lineNr, parsed);
        } catch (CompileException e) {
            throw new CompileException(e.getMessage(), parser.row(), parser.col());
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
        var values = EXPRESSIONS.values();
        @Nullable Expr detected = null;
        for (var expr : values) {
            if (expr.parse(new Parser(code))) {
                detected = expr;
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
        var values = EFFECTS.values();
        @Nullable Effect detected = null;
        for (var effect : values) {
            if (effect.parse(new Parser(code))) {
                detected = effect;
                break;
            }
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
    public static CEvent compileEvent(int lineNr, Parser parser) throws CompileException {
        var parsed = parser.readLine();

        if (parsed.isBlank()) {
            return null;
        }
        if (Pattern.matches("^.+:?$", parsed)) {
            var eventExpr = parsed.substring(0, parsed.length() - 1);
            @Nullable Event detected = detectEvent(eventExpr);
            if (detected == null) {
                throw new CompileException("Invalid event: " + parsed, lineNr, parser.col());
            }

            var block = parser.readIndentedBlock();

            return detected.compile(lineNr, block);
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
    private static Event detectEvent(String code) {
        var values = EVENTS.values();
        @Nullable Event detected = null;
        for (var event : values) {
            if (event.parse(new Parser(code))) {
                detected = event;
                break;
            }
        }
        return detected;
    }
}
