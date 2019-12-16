package org.rebotted.script.types;

public abstract class Script {

    private final long startTime = System.currentTimeMillis();

    public abstract boolean onStart();

    public abstract void onStop();

    public abstract void onBreak();

    public abstract int operate();

    public final long getRuntime() {
        return System.currentTimeMillis() - startTime;
    }
}