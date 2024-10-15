package dev.ultreon.scriptic.lang.obj;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.lang.LangObject;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.regex.Pattern;

public abstract class Expr extends LangObject<Expr> {

    public abstract Pattern getPattern();

    public boolean parse(Parser parser) {
        var code = parser.readLine();
        if (hasCodeBlock()) {
            parser.readIndentedBlock(2, true);
        }

        return getPattern().matcher(code).matches();
    }

    public Identifier getRegistryName() {
        return Registries.EXPRESSIONS.getKey(this);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr the line number of the code.
     * @param code   the code.
     * @return the compiled code.
     */
    @Override
    public abstract CExpr compile(int lineNr, String code) throws CompileException;
}
