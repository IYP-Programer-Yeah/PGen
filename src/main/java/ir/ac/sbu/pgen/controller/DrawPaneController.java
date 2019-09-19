package ir.ac.sbu.pgen.controller;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import ir.ac.sbu.pgen.cmd.Command;
import ir.ac.sbu.pgen.cmd.CommandManager;
import ir.ac.sbu.pgen.cmd.MakeEdgeCmd;
import ir.ac.sbu.pgen.cmd.MakeNodeCmd;
import ir.ac.sbu.pgen.graphics.BoundLine;
import ir.ac.sbu.pgen.graphics.GraphNode;
import ir.ac.sbu.pgen.model.EdgeModel;
import ir.ac.sbu.pgen.model.GraphModel;
import ir.ac.sbu.pgen.model.NodeModel;


/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class DrawPaneController implements RefreshableController {
    Pane pane;
    GraphModel graph;
    NodeModel firstNode = null;

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
            System.out.println("new Edge");
        }
        System.out.println(node.getId());
    }

    private void mouseClickEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            CommandManager.getInstance().applyCommand(new MakeNodeCmd(graph, mouseEvent.getX(), mouseEvent.getY()));
        }
    }
}
