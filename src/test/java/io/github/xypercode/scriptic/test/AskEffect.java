package io.github.xypercode.scriptic.test;

import io.github.xypercode.scriptic.CompileException;
import io.github.xypercode.scriptic.Registries;
import io.github.xypercode.scriptic.ScriptException;
import io.github.xypercode.scriptic.impl.CodeBlock;
import io.github.xypercode.scriptic.lang.CodeContext;
import io.github.xypercode.scriptic.lang.obj.Effect;
import io.github.xypercode.scriptic.lang.obj.compiled.CEffect;
import io.github.xypercode.scriptic.lang.obj.compiled.CExpr;
import io.github.xypercode.scriptic.lang.obj.compiled.CValue;
import io.github.xypercode.scriptic.lang.parser.Parser;

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
                CValue<?> eval = expr.eval(codeBlock, context);
                String cast = eval.cast();
                System.out.print(cast + " ");

                String answer = SCANNER.next("^.+$");
                context.set(TestCodeContexts.ANSWER, answer);
            }
        };
    }
}
