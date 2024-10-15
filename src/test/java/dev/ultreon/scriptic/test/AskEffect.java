package dev.ultreon.scriptic.test;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeBlock;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Effect;
import dev.ultreon.scriptic.lang.obj.compiled.CEffect;
import dev.ultreon.scriptic.lang.obj.compiled.CExpr;
import dev.ultreon.scriptic.lang.obj.compiled.CValue;
import dev.ultreon.scriptic.lang.parser.Parser;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AskEffect extends Effect {
    private static final Pattern PATTERN = Pattern.compile("^ask (?<expr>.+)$");
    private static final Scanner SCANNER = new Scanner(System.in);

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public CEffect compile(int lineNr, String code) throws CompileException {
        Matcher match = match(code);
        String exprCode = match.group("expr");
        CExpr expr = Registries.compileExpr(lineNr, new Parser(exprCode));

        return new CEffect(this, code, lineNr) {
            @Override
            protected void run(CodeBlock codeBlock, CodeContext context) throws ScriptException {
                CValue<?> eval = expr.eval(context);
                String cast = eval.cast();
                System.out.print(cast + " ");

                String answer = SCANNER.nextLine();
                context.getCurrentBlock().setAnswer(answer);
            }
        };
    }
}
