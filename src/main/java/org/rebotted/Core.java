package org.rebotted;

import org.rebotted.bot.data.RebottedAPI;
import org.rebotted.bot.loader.APILoader;
import org.rebotted.directory.DirectoryManager;
import org.rebotted.ui.themes.SubstanceDark;

import javax.swing.*;

public class Core {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        DirectoryManager.init();
        SwingUtilities.invokeLater(() -> {
            try {
                System.setProperty("insubstantial.checkEDT", "false"); //turns off printing Substance throwables.
                System.setProperty("insubstantial.logEDT", "false"); //turns off printing Substance exceptions.
                JFrame.setDefaultLookAndFeelDecorated(true);
                JPopupMenu.setDefaultLightWeightPopupEnabled(false);
                ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
                toolTipManager.setLightWeightPopupEnabled(false);
                UIManager.setLookAndFeel(new SubstanceDark());
                Client.initFrame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
