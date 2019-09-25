package ir.ac.sbu.service;

import ir.ac.sbu.model.NodeModel;

public class NodeJSON {
    double x, y;
    int id;
    boolean isFinal;

    public NodeJSON(NodeModel n) {
        x = n.getX();
        y = n.getY();
        id = n.getId();
        isFinal = n.getFinal();
    }

    public NodeJSON() {

    }
}
