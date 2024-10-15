package dev.ultreon.scriptic.lang;

public class WhileLoop implements Loop {
    private boolean broken;

    @Override
    public boolean isBroken() {
        return broken;
    }

    @Override
    public void breakLoop() {
        broken = true;
    }
}
