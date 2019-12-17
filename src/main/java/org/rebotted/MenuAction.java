package org.rebotted;

public class MenuAction {
    private int hash = 0;
    private String action;
    private String target;
    private int mouseX = 0;
    private int mouseY = 0;
    private int actionId;

    public MenuAction(int hash, String action, String target, int mouseX, int mouseY, int actionId) {
        this.hash = hash;
        this.action = action;
        this.target = target;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.actionId = actionId;
    }

    public MenuAction(int hash, String action, String target, int actionId) {
        this.hash = hash;
        this.action = action;
        this.target = target;
        this.actionId = actionId;
    }

    public int getHash() {
        return hash;
    }

    public String getAction() {
        return action;
    }

    public String getTarget() {
        return target;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getActionId() {
        return actionId;
    }
}
