package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Scanner;
import java.util.regex.Matcher;

public class AskEffect extends Effect {
    @RegExp
    public static final String PATTERN = "^ask (?<expr>.+)$";
    private static final Scanner SCANNER = new Scanner("John Doe\n");
    private @UnknownNullability Expr<Object> expr;

    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        String exprCode = matcher.group("expr");
        this.expr = Registries.compileExpr(lineNr, new Parser(exprCode));
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        @NotNull String eval = expr.doEval(context, String.class);
        System.out.print(eval + " ");

        String answer = SCANNER.nextLine();
        context.getCurrentBlock().setAnswer(answer);
    }
}
