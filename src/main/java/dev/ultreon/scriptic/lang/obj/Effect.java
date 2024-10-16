package dev.ultreon.scriptic.lang.obj;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.lang.LangObject;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public abstract class Effect extends LangObject<Effect> {
    public static List<Effect> bulkCompile(int lineNr, String code) throws CompileException {
        var parser = new Parser(code);

        List<Effect> effects = new ArrayList<>();

        while (!parser.isEOF()) {
            String row = parser.readLine().trim();
            if (row.isBlank()) continue;
            int line = lineNr + parser.row() - 1;
            var effectFor = Registries.compileEffect(line, row);

            if (effectFor == null) {
                continue;
            }
            effectFor.preload(line, row);
            effects.add(effectFor);
            effectFor.load(line, effectFor.match(row));
            String s = parser.readIndentedBlock();
            if (!s.isBlank()) {
                effectFor.loadBlock(line, s);
            }
        }
        return effects;
    }

    public boolean parse(Parser parser) {
        var code = parser.readLine();
        if (requiresBlock()) {
            parser.readIndentedBlock(2, true);
        }

        var off = code.endsWith(".") ? 1 : 0;

        return getPattern().matcher(code.substring(0, code.length() - off)).matches();
    }

    public String getRegistryName() {
        return Registries.EFFECTS.getKey(this);
    }

    /**
     * Compiles a piece of code for this effect.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     * @return the compiled code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {

    }

    public Matcher match(String code) {
        Matcher matcher = getPattern().matcher(new Parser(code).readLine());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid match: " + code);
        }

        return matcher;
    }
}
