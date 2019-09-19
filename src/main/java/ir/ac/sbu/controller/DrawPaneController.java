package ir.ac.sbu.controller;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import ir.ac.sbu.pgen.cmd.CommandManager;
import ir.ac.sbu.pgen.cmd.MakeEdgeCmd;
import ir.ac.sbu.pgen.cmd.MakeNodeCmd;
import ir.ac.sbu.pgen.graphics.BoundLine;
import ir.ac.sbu.pgen.graphics.GraphNode;
import ir.ac.sbu.pgen.model.EdgeModel;
import ir.ac.sbu.pgen.model.GraphModel;
import ir.ac.sbu.pgen.model.NodeModel;

public class DrawPaneController implements RefreshableController {
    private Pane pane;
    private GraphModel graph;
    private NodeModel firstNode = null;

    public DrawPaneController(Pane pane) {
        this.pane = pane;
        pane.setOnMouseReleased(this::mouseClickEvent);
    }

    public void refresh() {
        pane.getChildren().clear();

        for (NodeModel nodeModel : graph.getNodes()) {
            for (EdgeModel edge : nodeModel.getAdjacent()) {
                BoundLine line = new BoundLine(edge);
                pane.getChildren().addAll(line, line.getAnchor(), line.getText(), line.getArrowEnd());
                line.toBack();
            }
        }

        for (NodeModel nodeModel : graph.getNodes()) {
            GraphNode graphNode = new GraphNode(nodeModel);
            graphNode.setOnShiftClick(this::onShiftClick);
            pane.getChildren().add(graphNode);
        }


    }

    private void onShiftClick(MouseEvent mouseEvent) {
        NodeModel node = ((GraphNode) mouseEvent.getSource()).getNode();
        if (firstNode == null)
            firstNode = node;
        else {
            CommandManager.getInstance().applyCommand( new MakeEdgeCmd(firstNode, node));
            firstNode = node;
        }
    }

    private void mouseClickEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            CommandManager.getInstance().applyCommand(new MakeNodeCmd(graph, mouseEvent.getX(), mouseEvent.getY()));
        }
    }

    public void setGraph(GraphModel graph) {
        this.graph = graph;
    }

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public GraphModel getGraph() {
        return graph;
    }

    public NodeModel getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(NodeModel firstNode) {
        this.firstNode = firstNode;
    }
}