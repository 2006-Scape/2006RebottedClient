package org.rebotted.ui;

import org.rebotted.Configuration;
import org.rebotted.GameApplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class BotFrame extends JFrame implements ActionListener  {

    private static final long serialVersionUID = 1L;

    private final BotMenuBar botMenuBar;

    public BotFrame(GameApplet applet, boolean resizable) {
        setTitle(Configuration.CLIENT_NAME);
        setResizable(resizable);
        botMenuBar = new BotMenuBar(this);
        setJMenuBar(botMenuBar);
        add(applet, BorderLayout.CENTER);
        setMinimumSize(new Dimension(774, 559));
        setSize(new Dimension(774, 559));
        pack();
        setLocationRelativeTo(getParent());
        setLocationRelativeTo(getOwner());
        setVisible(true);
        requestFocus();
        toFront();
        applet.initClientFrame(766, 536);
        System.out.println("Client Launched.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand().toLowerCase()) {
            case "run":
                System.out.println("run was clicked..");
                break;
        }
    }
}