package ir.ac.sbu.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ir.ac.sbu.pgen.cmd.ChangeEdgeCmd;
import ir.ac.sbu.pgen.cmd.CommandManager;
import ir.ac.sbu.pgen.model.EdgeModel;

public class EdgePropertiesController {
    @FXML
    public Button okBtn;
    @FXML
    public TextField tokenText;
    @FXML
    public TextField funcText;
    @FXML
    public CheckBox graphChk;
    @FXML
    public CheckBox globalChk;
    EdgeModel edge;

    public void init(EdgeModel edge) {
        this.edge = edge;
        tokenText.setText(edge.getToken());
        funcText.setText(edge.getFunc());
        globalChk.setSelected(edge.getGlobal());
        graphChk.setSelected(edge.getGraph());
        okBtn.setOnMouseClicked(event ->
                {
                    Stage stage = (Stage) okBtn.getScene().getWindow();
                    CommandManager.getInstance().applyCommand(new ChangeEdgeCmd(edge, tokenText.getText(), funcText.getText(), graphChk.isSelected(), globalChk.isSelected()));
                    stage.close();
                }
        );
    }

}
