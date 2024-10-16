package dev.ultreon.scriptic.impl.expr;

public final class Null {
    public static final Null NULL = new Null();

    private Null() {

    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(null);
    }
}
