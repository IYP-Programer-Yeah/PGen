package ir.ac.sbu;

import ir.ac.sbu.utility.ResourceUtility;
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
        Parent root = FXMLLoader.load(ResourceUtility.getResource("fxml/Main.fxml"));
        primaryStage.setTitle("PGen");
        primaryStage.getIcons().add(new Image(ResourceUtility.getResourceAsStream("assets/Icon.png")));

        primaryStage.setScene(createScene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private static Scene createScene(Parent root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(ResourceUtility.getResource("styles/styles.css").toString());
        return scene;
    }
}
