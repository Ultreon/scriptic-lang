package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.regex.Matcher;

public class FloatingPointExpr extends Expr<BigDecimal> {

    public static final String PATTERN = "^(?<number>\\d+([,.])\\d+)$";

    public FloatingPointExpr() {
        super(BigDecimal.class);
    }

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {

    }

    @Override
    public @NotNull BigDecimal eval(CodeContext context) throws ScriptException {
        var parser = new Parser(code());
        return parser.readNumber();
    }
}
