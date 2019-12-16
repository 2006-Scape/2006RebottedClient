package org.rebotted.script.types;

import org.rebotted.script.ScriptHandler;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public abstract class TaskScript extends Script implements Comparator<Task> {

    private final List<Task> tasks = new LinkedList<>();

    private synchronized Task get() {
        try {
            for (Task action : tasks) {
                if (action != null && action.activate()) {
                    return action;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final void provide(List<Task> tasks) {
        if (tasks.size() > 0) {
            for (Task task : tasks) {
                this.tasks.add(task);
            }
        }
    }

    @Override
    public final int operate() {
        try {
            final Task action = get();
            if (action != null) {
                action.execute();
                return 200;
            }
        } catch (Exception e) {
            ScriptHandler.getInstance().stop();
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int compare(Task o1, Task o2) {
        return o1.priority() - o2.priority();
    }

}