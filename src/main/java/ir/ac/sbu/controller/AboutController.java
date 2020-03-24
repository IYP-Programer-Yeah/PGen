package ir.ac.sbu.controller;

import ir.ac.sbu.utility.ResourceUtility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AboutController {
    @FXML
    private VBox page;
    @FXML
    private Label versionLabel;

    @FXML
    private void initialize() throws IOException {
        InputStream resourceAsStream = ResourceUtility.getResourceAsStream("version.properties");
        Properties versionProperties = new Properties();
        versionProperties.load(resourceAsStream);
        versionLabel.setText(versionProperties.getProperty("version"));
    }

    public void close(ActionEvent actionEvent) {
        ((Stage) page.getScene().getWindow()).close();
    }
}
