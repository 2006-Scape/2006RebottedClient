package org.rebotted.ui;

import org.rebotted.Client;
import org.rebotted.Configuration;
import org.rebotted.GameApplet;
import org.rebotted.script.ScriptHandler;
import org.rebotted.script.loader.ScriptLoader;
import org.rebotted.ui.menu.BotMenuBar;
import org.rebotted.ui.script.ScriptSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class BotFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static BotMenuBar botMenuBar;
    private final Client client;

    public BotFrame(Client client, boolean resizable) {
        this.client = client;
        final GameApplet applet = client;
        setTitle(Configuration.CLIENT_NAME);
        setResizable(resizable);
        BotFrame.botMenuBar = new BotMenuBar(this);
        setJMenuBar(botMenuBar);
        add(applet, BorderLayout.CENTER);
        setMinimumSize(new Dimension(774, 567));
        setSize(new Dimension(774, 567));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(getParent());
        setLocationRelativeTo(getOwner());
        setVisible(true);
        requestFocus();
        toFront();
        applet.initClientFrame(766, 536);
        System.out.println("Client Launched.");
    }

    public static void setPaused() {
        botMenuBar.setPausedButtons(false);
        botMenuBar.setRunButtons(true);
        botMenuBar.setStopButtons(true);
    }

    public static void setRunning() {
        botMenuBar.setRunButtons(false);
        botMenuBar.setPausedButtons(true);
        botMenuBar.setStopButtons(true);
    }

    public static void setStopped() {
        botMenuBar.setStopButtons(false);
        botMenuBar.setPausedButtons(false);
        botMenuBar.setRunButtons(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand().toLowerCase()) {
            case "run":
                if (ScriptHandler.getInstance().getScriptState() == ScriptHandler.State.STOPPED) {
                    new ScriptSelector(new ScriptLoader(client.getApiData()));
                } else if (ScriptHandler.getInstance().getScriptState() == ScriptHandler.State.PAUSE) {
                    ScriptHandler.getInstance().setScriptState(ScriptHandler.State.RUNNING);
                    setRunning();
                }
                break;
            case "pause":
                setPaused();
                ScriptHandler.getInstance().pause();
                break;
            case "stop":
                setStopped();
                ScriptHandler.getInstance().stop();
                break;
            case "entities":
                Configuration.namesAboveHeads = !Configuration.namesAboveHeads;
                break;
        }
    }
}