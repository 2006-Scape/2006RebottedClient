package org.rebotted.script.types;

public abstract class LoopScript extends Script {

    public abstract int loop();

    @Override
    public final int operate() {
        return loop();
    }
}
