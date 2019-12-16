package org.rebotted.ui;

import org.rebotted.Configuration;
import org.rebotted.GameApplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class BotFrame extends JFrame implements ActionListener  {

    private final GameApplet applet;
    public Toolkit toolkit = Toolkit.getDefaultToolkit();
    public Dimension screenSize = toolkit.getScreenSize();
    public int screenWidth = (int) screenSize.getWidth();
    public int screenHeight = (int) screenSize.getHeight();
    private static final long serialVersionUID = 1L;
    private final BotMenuBar botMenuBar;
    protected final Insets insets;

    public BotFrame(GameApplet applet, boolean resizable) {
        this.applet = applet;
        setTitle(Configuration.CLIENT_NAME);
        setResizable(resizable);
        insets = getInsets();
        botMenuBar = new BotMenuBar(this);
        setJMenuBar(botMenuBar);
        add(applet, BorderLayout.CENTER);
        setMinimumSize(new Dimension(774, 559));
        setSize(774, 559);
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