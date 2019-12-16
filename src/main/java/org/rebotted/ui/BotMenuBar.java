package org.rebotted.ui;

import javax.swing.*;

/**
 * @author Ethan
 */

public class BotMenuBar extends JMenuBar {
    private BotFrame botUI;
    private JButton startButton, pauseButton, stopButton;
    private JMenu file, scripts;
    private JMenuItem run, pause, stop;

    public BotMenuBar(BotFrame botUI) {
        this.botUI = botUI;
        configure();
    }

    private final void configure() {
        createMenu();
        configureComponents();
    }

    private void createMenu() {

        file = new JMenu("File");
        scripts = new JMenu("Script");

        final JMenuItem exit = new JMenuItem("Exit");


        run = createNewJMenuItem( "Run", true);

        pause = createNewJMenuItem("Pause", false);

        stop = createNewJMenuItem("Stop", false);

        exit.addActionListener(botUI);

        scripts.add(run);
        scripts.add(pause);
        scripts.add(stop);

        file.add(exit);


      /*  startButton = createNewButton(new ImageIcon(Images.getResource("/storage/images/run_button.png")), "Run Script", "Run", true);

        pauseButton = createNewButton(new ImageIcon(Images.getResource("/storage/images/pause_button.png")), "Pause Script", "Pause", false);

        stopButton = createNewButton(new ImageIcon(Images.getResource("/storage/images/stop_button.png")), "Stop Script", "Stop", false);*/

    }

    private JMenuItem createNewJMenuItem(String name, boolean enabled) {
        final JMenuItem tempItem = new JMenuItem(name);
        tempItem.setEnabled(enabled);
        tempItem.addActionListener(botUI);
        return tempItem;
    }

    private JButton createNewButton(ImageIcon icon, String tooltip, String action, boolean enabled) {
        final JButton tempButton = new JButton();
        tempButton.setIcon(icon);
        tempButton.setContentAreaFilled(false);
        tempButton.setRolloverEnabled(true);
        tempButton.setToolTipText(tooltip);
        tempButton.addActionListener(botUI);
        tempButton.setActionCommand(action);
        tempButton.setEnabled(enabled);
        return tempButton;
    }

    private void configureComponents() {
        removeAll();
        add(file);
        add(scripts);
        add(Box.createHorizontalGlue());
        /*add(startButton);
        add(pauseButton);
        add(stopButton);*/

    }

    public JMenu getFile() {
        return file;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JMenu getScripts() {
        return scripts;
    }

    public JMenuItem getRun() {
        return run;
    }

    public JMenuItem getPause() {
        return pause;
    }

    public JMenuItem getStop() {
        return stop;
    }
}