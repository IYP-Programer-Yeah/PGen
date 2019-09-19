package ir.ac.sbu.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AboutController {
    @FXML
    private VBox page;

    public void close(ActionEvent actionEvent) {
        ((Stage) page.getScene().getWindow()).close();
    }
}

