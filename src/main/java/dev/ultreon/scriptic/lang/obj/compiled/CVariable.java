package dev.ultreon.scriptic.lang.obj.compiled;

import dev.ultreon.scriptic.lang.CompiledCode;

public class CVariable extends CompiledCode {
    private final String name;
    private CValue<?> value;

    public CVariable(String name, CValue<?> value) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public CValue<?> get() {
        return value;
    }

    public void set(CValue<?> value) {
        this.value = value;
    }

    public String toString() {
        return name + " = " + value;
    }

    public boolean equals(Object o) {
        return o instanceof CVariable && name.equals(((CVariable) o).name) && value.equals(((CVariable) o).value);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CVariable clone() {
        return new CVariable(name, value);
    }
}
