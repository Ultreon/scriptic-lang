package dev.ultreon.scriptic.lang.obj;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.lang.LangObject;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.obj.compiled.CEvent;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Language Event.
 *
 * @author Qboi123
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public class Event extends LangObject<Event> {
    private Pattern pattern;
    private boolean cancelable;

    @Deprecated
    public Event() {

    }

    public Event(@RegExp String pattern) {
        this(pattern, false);
    }

    public Event(@RegExp String pattern, boolean cancelable) {
        this(Pattern.compile(pattern), cancelable);
    }

    public Event(Pattern pattern) {
        this(pattern, false);
    }

    public Event(Pattern pattern, boolean cancelable) {
        this.pattern = pattern;
        this.cancelable = cancelable;
    }

    /**
     * Compile the event.
     *
     * @param lineNr the line number of the code.
     * @param code the code.
     * @return the compiled event.
     */
    @Override
    public CEvent compile(int lineNr, String code) throws CompileException {
        return new CEvent(this, List.of(), CEffect.bulkCompile(lineNr, code));
    }

    /**
     * Check if parsed code is valid for this event.
     *
     * @param code the code to check.
     * @return true if valid, false otherwise.
     */
    public boolean parse(Parser code) {
        var s = code.readLine();
        return pattern.matcher(s).matches();
    }

    public Identifier getRegistryName() {
        return Registries.EVENTS.getKey(this);
    }

    public boolean isCancelable() {
        return cancelable;
    }
}
