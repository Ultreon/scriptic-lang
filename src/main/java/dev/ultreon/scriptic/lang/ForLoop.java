package dev.ultreon.scriptic.lang;

public class ForLoop implements Loop {
    private final Type loopType;
    private Number idx;
    private boolean broken;
    private Object value;

    public ForLoop(Type loopType) {
        this.loopType = loopType;
    }

    public void setIndex(Number idx) {
        this.idx = idx;
    }

    public Number getIndex() {
        return idx;
    }

    public Type getLoopType() {
        return loopType;
    }

    @Override
    public boolean isBroken() {
        return broken;
    }

    @Override
    public void breakLoop() {
        broken = true;
    }

    public void setValue(Object next) {
        this.value = next;
    }

    public Object getValue() {
        return value;
    }

    public enum Type {
        INDEX,
        VALUE,
    }
}
