package ir.ac.sbu.controller;

import ir.ac.sbu.command.CommandManager;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;
import ir.ac.sbu.service.ExportService;
import ir.ac.sbu.service.LLParser;
import ir.ac.sbu.service.SaveLoadService;
import ir.ac.sbu.utility.DialogUtility;
import ir.ac.sbu.utility.GenerateUID;
import ir.ac.sbu.utility.ResourceUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {
    @FXML
    private ListView<GraphModel> list;
    @FXML
    private AnchorPane pane;
    @FXML
    private VBox mainContainer;
    @FXML
    private MenuItem exportMenuItem;
    @FXML
    private MenuItem checkMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem loadMenuItem;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button addGraphBtn;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private MenuItem exportTableMenuItem;
    @FXML
    private MenuItem exportCSVTableMenuItem;

    private DrawPaneController drawPaneController;
    private ObservableList<GraphModel> graphs = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        drawPaneController = new DrawPaneController(pane);
        CommandManager.init(drawPaneController);
        GraphModel graph = new GraphModel("MAIN");
        drawPaneController.setGraph(graph);
        list.setItems(graphs);
        graphs.add(graph);

        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            drawPaneController.setGraph(newValue);
            drawPaneController.refresh();
        });

        list.setCellFactory(param ->
        {
            ListCell<GraphModel> cell = new ListCell<GraphModel>() {
                @Override
                protected void updateItem(GraphModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        textProperty().bind(item.nameProperty());
                    }
                }
            };

            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteBtn = new MenuItem("Delete");
            MenuItem renameBtn = new MenuItem("Rename");

            deleteBtn.setOnAction(event -> cell.getListView().getItems().remove(cell.getItem()));
            renameBtn.setOnAction(event ->
                    DialogUtility.showInputDialog(cell.getItem().getName(), "Rename graph")
                            .ifPresent(s -> cell.getItem().setName(s)));

            contextMenu.getItems().addAll(deleteBtn, renameBtn);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                    if (cell.getItem() == null) {
                        cell.textProperty().unbind();
                        cell.setText("");
                    }
                } else {
                    if (cell.getItem().getName().equals("MAIN")) {
                        deleteBtn.setDisable(true);
                        renameBtn.setDisable(true);
                    }
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });

        mainContainer.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        mainContainer.addEventHandler(KeyEvent.KEY_RELEASED, event -> drawPaneController.setFirstNode(null));
        exportMenuItem.setOnAction(this::export);
        saveMenuItem.setOnAction(this::save);
        loadMenuItem.setOnAction(this::load);
        checkMenuItem.setOnAction(this::build);
        exportTableMenuItem.setOnAction(this::prettyTable);
        exportCSVTableMenuItem.setOnAction(this::csvTable);
        addGraphBtn.setOnAction(event -> graphs.addAll(new GraphModel("New Graph")));
    }

    private void build(ActionEvent actionEvent) {
        renumber(null);
        LLParser parser = new LLParser();
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save Table to", "*.npt");
        if (selectedFile != null) {
            ShowMessages(parser.buildTable(graphs, selectedFile));
        }
    }

    private void prettyTable(ActionEvent actionEvent) {
        renumber(null);
        LLParser parser = new LLParser();
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save Pretty Table to", "*.prt");
        if (selectedFile != null) {
            ShowMessages(parser.buildPrettyTable(graphs, selectedFile));
        }
    }

    private void csvTable(ActionEvent actionEvent) {
        renumber(null);
        LLParser parser = new LLParser();
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save CSV Table to", "*.csv");
        if (selectedFile != null) {
            ShowMessages(parser.buildCSVTable(graphs, selectedFile));
        }
    }

    private void ShowMessages(List<String> messages) {
        if (messages.isEmpty()) {
            DialogUtility.showSuccessDialog("Successful!");
        } else {
            DialogUtility.showErrorDialog(messages);
        }
    }

    private void load(ActionEvent actionEvent) {
        File selectedFile = DialogUtility.showOpenDialog(pane.getScene().getWindow(), "*.pgs");
        if (selectedFile != null) {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.load(list);
            drawPaneController.setGraph(list.getItems().get(0));
            drawPaneController.refresh();
        }
    }

    private void save(ActionEvent actionEvent) {
        renumber(null);
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save PGen File", "*.pgs");
        if (selectedFile != null) {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.save(graphs);
        }
    }

    private void export(ActionEvent actionEvent) {
        File selectedDirectory = DialogUtility.showDirectoryDialog(pane.getScene().getWindow());
        if (selectedDirectory != null) {
            ExportService exportService = new ExportService(selectedDirectory);
            exportService.exportGraphs(graphs);
            DialogUtility.showSuccessDialog("Exported successfully");
        }
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) {
            if (keyEvent.getCode().equals(KeyCode.N)) {
                list.getItems().addAll(new GraphModel("2"));
            }
            if (keyEvent.getCode().equals(KeyCode.Z) && !keyEvent.isShiftDown()) {
                CommandManager.getInstance().rollBack();
            }
            if (keyEvent.isShiftDown() && keyEvent.getCode().equals(KeyCode.Z)) {
                CommandManager.getInstance().redoCommand();
            }
        }
    }

    public void aboutMenu(ActionEvent actionEvent) {
        showModal(ResourceUtility.getResource("fxml/About.fxml"), "About");
    }

    public void licenseMenu(ActionEvent actionEvent) {
        showModal(ResourceUtility.getResource("fxml/License.fxml"), "License");
    }

    private void showModal(URL resource, String title) {
        final FXMLLoader loader = new FXMLLoader(resource);

        Parent root = null;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renumber(ActionEvent actionEvent) {
        List<NodeModel> nodes = list.getItems().stream().
                flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setId(i);
        }
        GenerateUID.setIdCounter(nodes.size());
        drawPaneController.refresh();
    }

    public void manualMenu(ActionEvent actionEvent) {
        showModal(ResourceUtility.getResource("fxml/Help.fxml"), "Help");
    }
}
