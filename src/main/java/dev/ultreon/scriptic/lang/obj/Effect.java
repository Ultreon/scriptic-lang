package dev.ultreon.scriptic.lang.obj;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.lang.LangObject;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Effect extends LangObject<Effect> {

    public abstract Pattern getPattern();

    public boolean parse(Parser parser) {
        var code = parser.readLine();
        if (hasCodeBlock()) {
            parser.readIndentedBlock(2, true);
        }

        var off = code.endsWith(".") ? 1 : 0;

        return getPattern().matcher(code.substring(0, code.length() - off)).matches();
    }

    public Identifier getRegistryName() {
        return Registries.EFFECTS.getKey(this);
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr the line number of the code.
     * @param code   the code.
     * @return the compiled code.
     */
    @Override
    public abstract CEffect compile(int lineNr, String code) throws CompileException;

    public Matcher match(String code) {
        Matcher matcher = getPattern().matcher(new Parser(code).readLine());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid match: " + code);
        }

        return matcher;
    }
}
