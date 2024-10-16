package dev.ultreon.scriptic.lang.obj;

import dev.ultreon.scriptic.CompileException;
import dev.ultreon.scriptic.Registries;
import dev.ultreon.scriptic.ScriptException;
import dev.ultreon.scriptic.ScripticLang;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.LangObject;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public abstract class Expr<T> extends LangObject<Expr<?>> {
    private final Class<?> returnType;

    protected Expr(Class<?> returnType) {
        this.returnType = returnType;
    }

    public String getRegistryName() {
        return Registries.EXPRESSIONS.getKey(this);
    }

    /**
     * Compiles a piece of code for this expression.
     *
     * @param lineNr  the line number of the code.
     * @param matcher
     */
    @Override
    public abstract void load(int lineNr, Matcher matcher) throws CompileException;

    public abstract @NotNull T eval(CodeContext context) throws ScriptException;

    public @NotNull <R> R doEval(CodeContext context, Class<R> returnType) throws ScriptException {
        T eval = eval(context);
        return returnType.cast(eval);
    }

    public boolean isInstanceOf(Class<?> numberClass) {
        return numberClass.isAssignableFrom(getReturnType());
    }

    private Class<?> getReturnType() {
        return returnType;
    }

    @Override
    public void invoke(CodeContext context) throws ScriptException {
        eval(context);
    }

    public <R> Expr<R> verifyInstanceOf(Class<R> numberClass) throws CompileException {
        if (!isInstanceOf(numberClass)) {
            throw new CompileException("Expression " + code() + " must return " + ScripticLang.getTypeName(numberClass) + ".");
        }

        //noinspection unchecked
        return (Expr<R>) this;
    }
}
