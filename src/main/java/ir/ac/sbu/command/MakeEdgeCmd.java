package ir.ac.sbu.command;

import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.NodeModel;

public class MakeEdgeCmd implements Command {
    private NodeModel start;
    private EdgeModel edge;

    public MakeEdgeCmd(NodeModel start, NodeModel end) {
        this.start = start;
        this.edge = new EdgeModel(start, end);
    }

    @Override
    public void apply() {
        start.getAdjacentList().add(edge);
    }

    @Override

    public void rollBack() {
        start.getAdjacentList().remove(edge);
    }
}
