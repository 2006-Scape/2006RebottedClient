package org.rebotted.ui.script;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rebotted.script.ScriptHandler;
import org.rebotted.script.scriptdata.ScriptData;
import org.rebotted.script.scriptdata.SkillCategory;
import org.rebotted.script.types.Script;
import org.rebotted.ui.BotFrame;


import java.net.URL;
import java.util.ResourceBundle;

public class ScriptController implements Initializable {

    @FXML
    private Button startButton;

    @FXML
    private TableView<ScriptData> scriptTable;

    @FXML
    private TableColumn<ScriptData, SkillCategory> category;

    @FXML
    private TableColumn<ScriptData, String> scriptName;

    @FXML
    private TableColumn<ScriptData, String> author;

    @FXML
    private TableColumn<ScriptData, String> description;

    @FXML
    private TableColumn<ScriptData, Double> version;

    public void startScript() {
        if (ScriptHandler.getInstance().getScriptState() == ScriptHandler.State.PAUSE) {
            ScriptHandler.getInstance().pause();
        } else if (ScriptHandler.getInstance().getScriptState() == ScriptHandler.State.STOPPED) {
            final ScriptData scriptData = scriptTable.getSelectionModel().getSelectedItem();
            if(scriptData == null) {
                System.err.println("Please select a script before pressing start!");
                return;
            }
            startScript(scriptData);
            ScriptUI.hide();
            BotFrame.setRunning();
        } else if (ScriptHandler.getInstance().getScriptState() == ScriptHandler.State.RUNNING) {
            System.out.println("You already have a script running!");
        }
    }

    public void searchScripts() {

    }

    public TableView<ScriptData> getScriptTable() {
        return scriptTable;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        category.setCellValueFactory(new PropertyValueFactory<>("skillCategory"));

        scriptName.setCellValueFactory(new PropertyValueFactory<>("name"));

        author.setCellValueFactory(new PropertyValueFactory<>("author"));

        description.setCellValueFactory(new PropertyValueFactory<>("desc"));

        version.setCellValueFactory(new PropertyValueFactory<>("version"));

    }

    private void startScript(ScriptData scriptData) {
        Script script = null;
        try {
            script = (Script) scriptData.getMainClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScriptHandler.getInstance().start(script, scriptData);
    }
}
