package ir.ac.sbu.utility;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class DialogUtility {
    private DialogUtility() {
    }

    public static Optional<String> showInputDialog(String defaultValue, String title) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        setIcon(dialog, "assets/dialog/Information.png");
        return dialog.showAndWait();
    }

    public static File showSaveDialog(Window owner, String title, String... extensions) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("extensions", extensions));
        return chooser.showSaveDialog(owner);
    }

    public static File showOpenDialog(Window owner, String... extensions) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open a File");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("extensions", extensions));
        return chooser.showOpenDialog(owner);
    }

    public static File showDirectoryDialog(Window owner) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a directory");
        return chooser.showDialog(owner);
    }

    public static void showSuccessDialog(String message) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel("Result", "assets/dialog/Success.png", ButtonType.OK);
        setSimpleContent(dialog, message);
        dialog.showAndWait();
    }

    public static void showErrorDialog(String message) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel("Result", "assets/dialog/Error.png", ButtonType.OK);
        setSimpleContent(dialog, message);
        dialog.showAndWait();
    }

    public static void showErrorDialog(List<String> messages) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel("Result", "assets/dialog/Error.png", ButtonType.OK);
        setListContent(dialog, messages);
        dialog.showAndWait();
    }

    public static boolean showWarningDialog(String message) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel("Confirmation", "assets/dialog/Warning.png", ButtonType.YES, ButtonType.NO);
        setSimpleContent(dialog, message);
        Optional<ButtonType> optional = dialog.showAndWait();
        return optional.isPresent() && optional.get() == ButtonType.YES;
    }

    private static Dialog<ButtonType> CustomDialogWithLabel(String title, String iconPath, ButtonType... buttonTypes) {
        // Create the custom dialog.
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        setIcon(dialog, iconPath);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypes);
        return dialog;
    }

    private static void setSimpleContent(Dialog<ButtonType> dialog, String message) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.add(new Label(message), 0, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    }

    private static void setListContent(Dialog<ButtonType> dialog, List<String> messages) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        ListView<String> listView = new ListView<>(FXCollections.observableArrayList(messages));
        grid.add(listView, 0, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    }

    private static void setIcon(Dialog dialog, String iconPath) {
        // Set the icon
        dialog.setGraphic(null);

        // Set stage icon
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().addAll(new Image(ResourceUtility.getResourceAsStream(iconPath)));
    }
}
