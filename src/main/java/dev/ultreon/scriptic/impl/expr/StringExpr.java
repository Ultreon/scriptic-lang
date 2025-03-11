package dev.ultreon.scriptic.impl.expr;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Expr;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class StringExpr extends Expr<String> {

    public static final String PATTERN = "^((\")(?<text1>.+)(\")|(')(?<text2>.+)(')|(‘)(?<text3>.+)(’))$";
    private final List<StringComponent> entries = new ArrayList<>();

    public StringExpr() {
        super(String.class);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher the matcher for the code.
     */
    @Override
    public void load(int lineNr, Matcher matcher) throws CompileException {
        var parser = new Parser(code());
        String s = parser.readString() + "\0";

        final StringBuilder sb = new StringBuilder();
        int off = 0;

        while (off < s.length()) {
            char ch = s.charAt(off);
            if (ch == '%') {
                off++;
                if (s.charAt(off) == '%') {
                    sb.append('%');
                    off++;
                    continue;
                }

                if (s.charAt(off) == 'n') {
                    sb.append('\n');
                    off++;
                    continue;
                }

                if (s.charAt(off) == 't') {
                    sb.append('\t');
                    off++;
                    continue;
                }

                if (s.charAt(off) == 'r') {
                    sb.append('\r');
                    off++;
                    continue;
                }

                if (s.charAt(off) == 'f') {
                    sb.append('\f');
                    off++;
                    continue;
                }

                if (s.charAt(off) == 'u') {
                    StringBuilder hex = new StringBuilder();
                    off++;
                    for (int i = 0; i < 4; i++) {
                        char hexChar = s.charAt(off);
                        if (!Character.isDigit(hexChar) && (hexChar < 'a' || hexChar > 'f') && (hexChar < 'A' || hexChar > 'F')) {
                            throw new CompileException("Invalid hexadecimal character in string literal", getLineNr());
                        }
                        hex.append(hexChar);
                        off++;
                    }
                    sb.append((char) Integer.parseInt(hex.toString(), 16));
                    continue;
                }

                if (s.charAt(off) == 'x') {
                    StringBuilder hex = new StringBuilder();
                    off++;
                    for (int i = 0; i < 2; i++) {
                        char hexChar = s.charAt(off);
                        if (!Character.isDigit(hexChar) && (hexChar < 'a' || hexChar > 'f') && (hexChar < 'A' || hexChar > 'F')) {
                            throw new CompileException("Invalid hexadecimal character in string literal", getLineNr());
                        }
                        hex.append(hexChar);
                        off++;
                    }
                    sb.append((char) Integer.parseInt(hex.toString(), 16));
                    continue;
                }

                String string = sb.toString();
                this.add(string);
                sb.setLength(0);

                StringBuilder expr = new StringBuilder();
                while (off < s.length() && (ch = s.charAt(off)) != '%') {
                    if (ch == '\0') {
                        throw new CompileException("Unterminated string literal", getLineNr());
                    }
                    expr.append(ch);
                    off++;
                }

                off++;

                try {
                    Expr<Object> exprObj = Registries.compileExpr(getLineNr(), new Parser(expr.toString()));
                    this.add(exprObj);
                } catch (CompileException e) {
                    throw new CompileException(e.getMessage(), getLineNr());
                }

                continue;
            }

            if (ch != '\0')
                sb.append(ch);
            off++;
        }

        String string = sb.toString();
        this.add(string);
    }

    private void add(String string) {
        this.entries.add(new SimpleStringComponent(string));
    }

    private void add(Expr<Object> exprObj) {
        this.entries.add(new ExprStringComponent(exprObj));
    }

    @Override
    public @NotNull String eval(CodeContext context) throws ScriptException {
        StringBuilder sb = new StringBuilder();
        for (StringComponent component : entries) {
            sb.append(component.text(context));
        }
        return sb.toString();
    }

    private interface StringComponent {
        String text(CodeContext context) throws ScriptException;
    }

    private final class SimpleStringComponent implements StringComponent {
        private final String text;

        private SimpleStringComponent(String text) {
            this.text = text;
        }

        public String text() {
            return text;
        }

        @Override
        public String text(CodeContext context) {
            return text;
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (SimpleStringComponent) obj;
            return Objects.equals(this.text, that.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text);
        }
    }

    private final class ExprStringComponent implements StringComponent {
        private final Expr<?> text;

        private ExprStringComponent(Expr<?> text) {
            this.text = text;
        }

        public Expr<?> text() {
            return text;
        }

        @Override
        public String text(CodeContext context) throws ScriptException {
            return String.valueOf(text.eval(context));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ExprStringComponent) obj;
            return Objects.equals(this.text, that.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text);
        }

        @Override
        public String toString() {
            return "ExprStringComponent[" +
                    "text=" + text + ']';
        }
    }
}
