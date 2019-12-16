package org.rebotted.script.types;

public abstract class Task {

    private final int priority;

    public Task(int priority) {
        this.priority = priority;
    }

    public Task() {
        this.priority = 0;
    }

    public abstract boolean activate();

    public abstract void execute();

    public final int priority() {
        return priority;
    }

}
