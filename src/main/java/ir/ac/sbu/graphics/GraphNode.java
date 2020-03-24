package ir.ac.sbu.graphics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import ir.ac.sbu.command.CommandManager;
import ir.ac.sbu.command.DeleteNodeCmd;
import ir.ac.sbu.model.NodeModel;

public class GraphNode extends StackPane {
    private static final double radius = 15;
    private static final Color defaultNodeFillColor = Color.AQUA;
    private static final Color finalNodeFillColor = Color.RED;
    private static final Color defaultNodeStrokeColor = Color.AQUA;
    private static final Color startNodeStrokeColor = Color.BLUE;

    private Circle circle;
    private NodeModel nodeModel;
    private EventHandler<MouseEvent> onShiftClick;
    private double mouseX;
    private double mouseY;
    private ContextMenu contextMenu = new ContextMenu();

    public GraphNode(NodeModel nodeModel) {
        this.nodeModel = nodeModel;
        this.circle = new Circle(nodeModel.getX(), nodeModel.getY(), radius);

        circle.setStrokeWidth(3);
        circle.setStrokeType(StrokeType.OUTSIDE);

        setLayoutX(nodeModel.getX() - radius);
        setLayoutY(nodeModel.getY() - radius);
        nodeModel.xProperty().bind(layoutXProperty().add(radius));
        nodeModel.yProperty().bind(layoutYProperty().add(radius));

        nodeModel.finalNodeProperty().addListener((observable, oldValue, newValue) -> setFillColor(newValue));
        nodeModel.startNodeProperty().addListener((observable, oldValue, newValue) -> setStrokeColor(newValue));
        setStrokeColor(nodeModel.isStartNode());
        setFillColor(nodeModel.isFinalNode());

        Text text = new Text(String.valueOf(nodeModel.getId()));
        text.setBoundsType(TextBoundsType.VISUAL);
        getChildren().addAll(circle, text);
        setAlignment(Pos.CENTER);

        enableDrag();
        setMouseTransparent(false);
    }

    private void setStrokeColor(boolean isStartNode) {
        circle.setStroke(isStartNode ? startNodeStrokeColor : defaultNodeStrokeColor);
    }

    private void setFillColor(boolean isFinalNode) {
        circle.setFill(isFinalNode ? finalNodeFillColor : defaultNodeFillColor);
    }

    public NodeModel getNodeModel() {
        return nodeModel;
    }

    public EventHandler<MouseEvent> getOnShiftClick() {
        return onShiftClick;
    }

    public void setOnShiftClick(EventHandler<MouseEvent> onShiftClick) {
        this.onShiftClick = onShiftClick;
    }

    // make a node movable by dragging it around with the mouse.
    private void enableDrag() {
        setOnMousePressed(event -> {
            if (!event.isShiftDown()) {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            }
            event.consume();
        });
        setOnMouseClicked(event -> {
            if (event.isShiftDown() && onShiftClick != null) {
                onShiftClick.handle(event);
            }
            if (event.getButton().equals(MouseButton.SECONDARY)) {

                showContextMenu(event);
            }
            event.consume();
        });
        setOnMouseDragged(event -> {
            if (!event.isShiftDown()) {
                double deltaX = event.getSceneX() - mouseX;
                double deltaY = event.getSceneY() - mouseY;
                Bounds b = getBoundsInParent();
                if (b.getMinX() + deltaX < 0 || b.getMaxX() + deltaX > getParent().getLayoutBounds().getWidth())
                    deltaX = 0;
                if (b.getMinY() + deltaY < 0 || b.getMaxY() + deltaY > getParent().getLayoutBounds().getHeight())
                    deltaY = 0;
                relocate(getLayoutX() + deltaX, getLayoutY() + deltaY);
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            }
        });
        setOnMouseReleased(mouseEvent -> {
            getScene().setCursor(Cursor.HAND);
            mouseEvent.consume();
        });
        setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
            }
        });
        setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }

    private void showContextMenu(MouseEvent event) {
        MenuItem deleteBtn = new MenuItem("Delete");

        deleteBtn.setOnAction(this::delete);
        CheckMenuItem finalBtn = new CheckMenuItem("Final");
        finalBtn.selectedProperty().set(nodeModel.isFinalNode());
        nodeModel.finalNodeProperty().bind(finalBtn.selectedProperty());

        CheckMenuItem startBtn = new CheckMenuItem("Start");
        startBtn.selectedProperty().set(nodeModel.startNodeProperty().getValue());
        startBtn.setOnAction(event1 -> nodeModel.getGraph().setStart(nodeModel));
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(deleteBtn, finalBtn, startBtn);
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
    }

    private void delete(ActionEvent actionEvent) {
        CommandManager.getInstance().applyCommand(new DeleteNodeCmd(nodeModel));
    }
}
