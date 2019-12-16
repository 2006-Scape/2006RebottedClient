package org.rebotted.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.rebotted.bot.data.APIData;
import org.rebotted.script.loader.ScriptLoader;
import org.rebotted.script.scriptdata.ScriptData;


import javax.swing.*;


public class ScriptUI {
    private static ScriptController controller;
    private static JFrame frame;
    private JFXPanel jfxPanel = new JFXPanel();
    private ScriptLoader scriptLoader;

    public ScriptUI(final APIData apiData) {
        scriptLoader = new ScriptLoader(apiData);
        loadUI();
    }

    public static ScriptController getController() {
        return controller;
    }

    public void loadUI() {
        Platform.runLater(() -> {
            try {
                frame = new JFrame("2006Rebotted - Script Selector");
                jfxPanel = new JFXPanel();
                final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ScriptUI.fxml"));
                final Parent root = fxmlLoader.load();
                final Scene scene = new Scene(root, 597, 353);
                scene.getStylesheets().add(getClass().getResource("/dark.css").toExternalForm());
                jfxPanel.setScene(scene);
                controller = fxmlLoader.getController();
                controller.getScriptTable().setItems(loadLocalScripts());
                SwingUtilities.invokeLater(() -> {
                    frame.add(jfxPanel);
                    frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                    frame.pack();
                    frame.setResizable(false);
                });
            } catch (Exception e) {

            }
        });
    }

    public void show() {
        controller.getScriptTable().setItems(loadLocalScripts());
        frame.setVisible(true);
        System.out.println("Script Selector Loaded.");
    }

    public static void hide() {
        frame.setVisible(false);
    }

    private ObservableList<ScriptData> loadLocalScripts() {
        final ObservableList<ScriptData> scripts = FXCollections.observableArrayList();
        for (ScriptData scriptData : scriptLoader.getScripts()) {
            scripts.add(scriptData);
        }
        return scripts;
    }
}
