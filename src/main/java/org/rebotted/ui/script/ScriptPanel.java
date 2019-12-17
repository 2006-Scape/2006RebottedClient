package org.rebotted.ui.script;

import org.rebotted.script.scriptdata.ScriptData;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ScriptPanel extends JPanel {

    private static final long serialVersionUID = -5181188196122580695L;
    private JButton button;

    public ScriptPanel(final ScriptData scriptData) {
        setLayout(new BorderLayout());

        setToolTipText(scriptData.getDesc());

        button = new JButton("Start");

        add(button, BorderLayout.SOUTH);

        final JLabel scriptName = new JLabel(scriptData.getName());

        scriptName.setHorizontalAlignment(JLabel.CENTER);

        add(scriptName, BorderLayout.NORTH);

        setBorder(new EtchedBorder());
    }

    public JButton getButton() {
        return button;
    }

}