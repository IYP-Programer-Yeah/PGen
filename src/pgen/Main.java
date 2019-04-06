package pgen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
        primaryStage.setTitle("PGen");
        primaryStage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/fxml/Icon.png")));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
