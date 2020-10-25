package ir.ac.sbu.model;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class NodeModel {
    private int id;
    private DoubleProperty x;
    private DoubleProperty y;
    private GraphModel graph;

    private BooleanProperty finalNode;
    private BooleanProperty startNode;
    private List<EdgeModel> adjacent;

    public NodeModel(double x, double y, GraphModel graph, int id) {
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.graph = graph;
        this.id = id;

        adjacent = new ArrayList<>();
        finalNode = new SimpleBooleanProperty(false);
        startNode = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public GraphModel getGraph() {
        return graph;
    }

    public void setGraph(GraphModel graph) {
        this.graph = graph;
    }

    public List<EdgeModel> getAdjacentList() {
        return adjacent;
    }

    public void setAdjacent(List<EdgeModel> adjacent) {
        this.adjacent = adjacent;
    }

    public boolean isFinalNode() {
        return finalNode.get();
    }

    public BooleanProperty finalNodeProperty() {
        return finalNode;
    }

    public void setFinalNode(boolean finalNode) {
        this.finalNode.set(finalNode);
    }

    public boolean isStartNode() {
        return startNode.get();
    }

    public BooleanProperty startNodeProperty() {
        return startNode;
    }

    public void setStartNode(boolean startNode) {
        this.startNode.set(startNode);
    }
}
