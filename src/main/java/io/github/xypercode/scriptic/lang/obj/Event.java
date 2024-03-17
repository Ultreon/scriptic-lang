package io.github.xypercode.scriptic.lang.obj;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.lang.LangObject;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEvent;
import io.github.xypercode.scriptic.lang.parser.Parser;
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

    @Deprecated
    public Event() {

    }

    public Event(@RegExp String pattern) {
        this(Pattern.compile(pattern));
    }

    public Event(Pattern pattern) {
        this.pattern = pattern;
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
}
