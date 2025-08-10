package io.txlens.annotations;

public enum Propagation {
    REQUIRED(0), REQUIRES_NEW(3);

    public final int value;

    private Propagation(int value) {
        this.value = value;
    }
}
