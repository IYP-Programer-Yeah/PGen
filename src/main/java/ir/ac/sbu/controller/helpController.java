package ir.ac.sbu.controller;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HelpController {
    public AnchorPane pane;

    public void close(ActionEvent actionEvent) {
        ((Stage) pane.getScene().getWindow()).close();
    }
}
