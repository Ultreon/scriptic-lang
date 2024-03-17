package io.github.xypercode.scriptic.lang.obj;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.lang.LangObject;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.parser.Parser;

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
