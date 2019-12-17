package org.rebotted.ui.script;

import org.rebotted.directory.DirectoryManager;
import org.rebotted.script.ScriptHandler;
import org.rebotted.script.loader.ScriptLoader;
import org.rebotted.script.scriptdata.ScriptData;
import org.rebotted.script.types.Script;
import org.rebotted.ui.BotFrame;
import org.rebotted.ui.themes.SubstanceDark;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ethan
 */
public class ScriptSelector extends JFrame {

    private JScrollPane scrollPane1;
    private JTable scriptTable;
    private JButton startButton;
    private JComboBox accounts;
    private JTextField search;
    private ScriptLoader scriptLoader;
    private List<ScriptData> scripts;

    public ScriptSelector(ScriptLoader scriptLoader) {
        this.scriptLoader = scriptLoader;
        scripts = scriptLoader.getScripts();
        initComponents();
    }

    private void startScript(ActionEvent e) {
        final int category = 0;
        final int name = 1;
        final int version = 2;
        final int desc = 3;
        final int author = 4;
        final int row = scriptTable.getSelectedRow();
        final String categoryName = scriptTable.getModel().getValueAt(row, category).toString();
        final String scriptName = scriptTable.getModel().getValueAt(row, name).toString();
        final String versionString = scriptTable.getModel().getValueAt(row, version).toString();
        final String descString = scriptTable.getModel().getValueAt(row, desc).toString();
        final String authorName = scriptTable.getModel().getValueAt(row, author).toString();
        for(ScriptData scriptData : scripts) {
            if(scriptData.getName().equals(scriptName) && scriptData.getAuthor().equals(authorName)) {
                if(scriptData.getDesc().equals(descString) && String.valueOf(scriptData.getVersion()).equals(versionString)) {
                    if(scriptData.getSkillCategory().getName().equals(categoryName)) {
                        startScript(scriptData);
                    }
                }
            }
        }
    }

    private void initComponents() {
        scrollPane1 = new JScrollPane();
        startButton = new JButton();
        accounts = new JComboBox();
        search = new JTextField();

        setTitle("2006Rebotted - Script Selector");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final Container contentPane = getContentPane();
        contentPane.setLayout(null);
        final java.util.List<String> columns = new ArrayList<>();
        final java.util.List<String[]> values = new ArrayList<>();
        columns.add("Category");
        columns.add("Script");
        columns.add("Version");
        columns.add("Description");
        columns.add("Author");

        for (ScriptData scriptData : scripts) {
            values.add(new String[]{scriptData.getSkillCategory().getName(), scriptData.getName(), String.valueOf(scriptData.getVersion()), scriptData.getDesc(), scriptData.getAuthor()});
        }
        final TableModel tableModel = new DefaultTableModel(values.toArray(new Object[][]{}), columns.toArray());

        scriptTable = new JTable(tableModel);

        scriptTable.setDefaultEditor(Object.class, null);

        TableColumnModel cm = scriptTable.getColumnModel();
        cm.getColumn(0).setMinWidth(110);
        cm.getColumn(0).setMaxWidth(110);
        cm.getColumn(1).setMinWidth(180);
        cm.getColumn(1).setMaxWidth(180);
        cm.getColumn(2).setMinWidth(60);
        cm.getColumn(2).setMaxWidth(60);
        cm.getColumn(3).setMinWidth(40);
        cm.getColumn(4).setMinWidth(100);
        cm.getColumn(4).setMaxWidth(100);

        scrollPane1.setViewportView(scriptTable);

        contentPane.add(scrollPane1);
        scrollPane1.setBounds(0, 0, 640, 280);

        startButton.setText("Start");
        startButton.addActionListener(e -> startScript(e));
        contentPane.add(startButton);
        startButton.setBounds(545, 280, 93, startButton.getPreferredSize().height);
        contentPane.add(accounts);
        accounts.setBounds(5, 280, 250, accounts.getPreferredSize().height);
        contentPane.add(search);
        search.setBounds(270, 281, 255, search.getPreferredSize().height);


        final Dimension preferredSize = new Dimension();

        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            Rectangle bounds = contentPane.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }

        Insets insets = contentPane.getInsets();
        preferredSize.width += insets.right;
        preferredSize.height += insets.bottom;
        contentPane.setMinimumSize(preferredSize);
        contentPane.setPreferredSize(preferredSize);

        setSize(645, 340);
        setLocationRelativeTo(getOwner());
    }

    private void startScript(ScriptData scriptData) {
        Script script = null;
        try {
            script = (Script) scriptData.getMainClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScriptHandler.getInstance().start(script, scriptData);
        BotFrame.setRunning();
        dispose();
    }
}
