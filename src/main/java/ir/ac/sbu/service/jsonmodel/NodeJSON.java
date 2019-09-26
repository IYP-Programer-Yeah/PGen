package ir.ac.sbu.service.jsonmodel;

import ir.ac.sbu.model.NodeModel;

public class NodeJSON {
    private double x;
    private double y;
    private int id;
    private boolean isFinal;

    public NodeJSON() {

    }

    public NodeJSON(NodeModel n) {
        x = n.getX();
        y = n.getY();
        id = n.getId();
        isFinal = n.isFinalNode();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
