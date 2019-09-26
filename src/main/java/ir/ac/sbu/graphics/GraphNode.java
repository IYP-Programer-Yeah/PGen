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
    private Circle circle;
    private NodeModel node;
    private Color color = Color.AQUA;
    private EventHandler<MouseEvent> onShiftClick;
    private double mouseX;
    private double mouseY;
    private ContextMenu contextMenu = new ContextMenu();

    public GraphNode(NodeModel n) {
        this.node = n;
        double radius = 10;
        circle = new Circle(n.getX(), n.getY(), radius);

        circle.setFill(color.deriveColor(1, 1, 1, 1));
        circle.setStroke(color);
        circle.setStrokeWidth(3);
        circle.setStrokeType(StrokeType.OUTSIDE);

        setLayoutX(n.getX() - radius);
        setLayoutY(n.getY() - radius);
        n.xProperty().bind(layoutXProperty().add(radius));
        n.yProperty().bind(layoutYProperty().add(radius));

        if (n.isFinalNode()) {
            circle.setFill(Color.RED);
        } else {
            circle.setFill(color.deriveColor(1, 1, 1, 1));
        }
        if (n.getGraph() != null && n.getGraph().getStart() == n) {
            circle.setStroke(Color.GREEN);
        } else {
            circle.setStroke(color);
        }

        n.finalNodeProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                circle.setFill(Color.RED);
            } else {
                circle.setFill(color.deriveColor(1, 1, 1, 1));
            }
        });

        n.startNodeProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                circle.setStroke(Color.GREEN);
            } else {
                circle.setStroke(color);
            }
        });
        Text text = new Text(String.valueOf(n.getId()));
        text.setBoundsType(TextBoundsType.VISUAL);
        getChildren().addAll(circle, text);
        setAlignment(Pos.CENTER);

        enableDrag();
        setMouseTransparent(false);
    }

    public NodeModel getNode() {
        return node;
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
        finalBtn.selectedProperty().set(node.isFinalNode());
        node.finalNodeProperty().bind(finalBtn.selectedProperty());

        CheckMenuItem startBtn = new CheckMenuItem("Start");
        startBtn.selectedProperty().set(node.startNodeProperty().getValue());
        startBtn.setOnAction(event1 -> node.getGraph().setStart(node));
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(deleteBtn, finalBtn, startBtn);
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
    }

    private void delete(ActionEvent actionEvent) {
        CommandManager.getInstance().applyCommand(new DeleteNodeCmd(node));
    }
}
