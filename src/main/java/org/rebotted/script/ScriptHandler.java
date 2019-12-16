package org.rebotted.script;

import org.rebotted.script.scriptdata.ScriptData;
import org.rebotted.script.types.Script;
import org.rebotted.util.Condition;

public class ScriptHandler implements Runnable {

    private static ScriptHandler instance;
    private Thread scriptThread;
    private Script script;
    private ScriptData scriptData;
    private volatile State scriptState = State.STOPPED;
    private long breakDuration;

    public ScriptHandler() {
        instance = this;
    }

    public static ScriptHandler getInstance() {
        if(instance == null)
            instance = new ScriptHandler();
        return instance;
    }

    @Override
    public void run() {
        try {
            while (!scriptState.equals(State.STOPPED)) {
                if (script == null) {
                    stop();
                } else if (scriptState.equals(State.PAUSE)) {
                    Condition.sleep(500);
                } else if (scriptState.equals(State.BREAKING)) {
                    this.script.onBreak();
                    Condition.sleep(breakDuration);
                } else {
                    int timeToSleep = script.operate();
                    Condition.sleep(timeToSleep);
                }
            }
        } catch (Exception e) {
            stop();
        }
    }

    public void start(Script script, ScriptData scriptData) {
        if (scriptState.equals(State.RUNNING)) {
            System.out.println("Why would you try to start a script with one running?");
            return;
        }
        if (script == null) {
            System.out.println("Error starting script.");
            return;
        }
        if (scriptState.equals(State.PAUSE)) {
            System.out.println("Script resumed: " + scriptData.getName());
            this.scriptState = State.RUNNING;
            return;
        }
        System.out.println("Script Started: " + scriptData.getName());
        this.scriptState = State.RUNNING;
        this.scriptData = scriptData;
        this.script = script;
        this.scriptThread = new Thread(this);
        if (this.script.onStart()) {
            this.scriptThread.start();
        }
    }

    public void takeBreak(long duration) {
        System.out.println(scriptData.getName() + " Breaking for: " + duration);
        this.breakDuration = duration;
        this.scriptState = State.BREAKING;
    }

    public void stop() {
        System.out.println("Script Stopped: " + scriptData.getName());
        this.scriptState = State.STOPPED;
        this.script.onStop();
        this.scriptThread.interrupt();
    }

    public void pause() {
        if (scriptState.equals(State.RUNNING)) {
            System.out.println("Script Paused: " + scriptData.getName());
            this.scriptState = State.PAUSE;
        } else if (scriptState.equals(State.PAUSE)) {
            System.out.println("Script resumed: " + scriptData.getName());
            this.scriptState = State.RUNNING;
        }
    }

    public State getScriptState() {
        return scriptState;
    }

    public void setScriptState(State scriptState) {
        this.scriptState = scriptState;
    }

    public enum State {
        RUNNING, BREAKING, PAUSE, STOPPED
    }
}