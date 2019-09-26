package ir.ac.sbu.model;

import javafx.beans.property.*;

public class EdgeModel {
    private NodeModel start;
    private NodeModel end;
    private StringProperty token;
    private StringProperty function;
    private BooleanProperty graph;
    private BooleanProperty global;
    private DoubleProperty anchorX;
    private DoubleProperty anchorY;

    public EdgeModel() {
    }

    public EdgeModel(NodeModel start, NodeModel end) {
        this.start = start;
        this.end = end;
        anchorX = new SimpleDoubleProperty((start.getX() + end.getX()) / 2);
        anchorY = new SimpleDoubleProperty((start.getY() + end.getY()) / 2);

        if (start == end) {
            int pad = 40;
            anchorX = new SimpleDoubleProperty((start.getX()));
            if (start.getY() > 4.0 / 3.0 * pad)
                anchorY = new SimpleDoubleProperty((start.getY() - pad));
            else
                anchorY = new SimpleDoubleProperty((start.getY() + pad));
        }
        token = new SimpleStringProperty("");
        function = new SimpleStringProperty("");
        graph = new SimpleBooleanProperty(false);
        global = new SimpleBooleanProperty(false);
    }

    public NodeModel getStart() {
        return start;
    }

    public void setStart(NodeModel start) {
        this.start = start;
    }

    public NodeModel getEnd() {
        return end;
    }

    public void setEnd(NodeModel end) {
        this.end = end;
    }

    public String getToken() {
        return token.get();
    }

    public StringProperty tokenProperty() {
        return token;
    }

    public void setToken(String token) {
        this.token.set(token);
    }

    public String getFunction() {
        return function.get();
    }

    public StringProperty functionProperty() {
        return function;
    }

    public void setFunction(String function) {
        this.function.set(function);
    }

    public boolean isGraph() {
        return graph.get();
    }

    public BooleanProperty graphProperty() {
        return graph;
    }

    public void setGraph(boolean graph) {
        this.graph.set(graph);
    }

    public boolean isGlobal() {
        return global.get();
    }

    public BooleanProperty globalProperty() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global.set(global);
    }

    public double getAnchorX() {
        return anchorX.get();
    }

    public DoubleProperty anchorXProperty() {
        return anchorX;
    }

    public void setAnchorX(double anchorX) {
        this.anchorX.set(anchorX);
    }

    public double getAnchorY() {
        return anchorY.get();
    }

    public DoubleProperty anchorYProperty() {
        return anchorY;
    }

    public void setAnchorY(double anchorY) {
        this.anchorY.set(anchorY);
    }
}
