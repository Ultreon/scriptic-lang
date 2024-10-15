package dev.ultreon.scriptic.lang.obj.compiled;

public final class CNone extends CValue<Void> {
    public static final CNone INSTANCE = new CNone();

    private CNone() {
        super(null);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "nothing";
    }

    @Override
    public <R> R cast0(R[] typeGetter) {
        throw new IllegalArgumentException("There's no value here. What did ya expect?");
    }
}
