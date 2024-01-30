package io.github.xypercode.scriptic.lang.obj.compiled;

import io.github.xypercode.scriptic.ScriptException;

import java.util.Objects;

public sealed class CValue<T> permits CNone {
    private final T value;

    public CValue(T value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }

    public String toString() {
        return value.toString();
    }

    public boolean equals(Object o) {
        return o instanceof CValue<?> otherValue && value.equals(otherValue);
    }

    public CValue<T> copy() {
        return new CValue<>(value);
    }

    public T value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @SafeVarargs
    public final <R> R cast(R... typeGetter) throws ScriptException {
        return cast0(typeGetter);
    }

    @SuppressWarnings("unchecked")
    protected <R> R cast0(R[] typeGetter) throws ScriptException {
        var componentType = typeGetter.getClass().getComponentType();
        if (componentType.isInstance(value)) {
            return (R) value;
        }

        throw new ScriptException("Value is not of type " + componentType, 0);
    }
}
