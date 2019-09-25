package ir.ac.sbu.command;

import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.NodeModel;

public class DeleteEdgeCmd implements Command {

    EdgeModel edge;

    public DeleteEdgeCmd(EdgeModel edge) {
        this.edge = edge;
    }

    @Override
    public void apply() {
        NodeModel node = edge.getStart();
        node.getAdjacent().remove(edge);

    }

    @Override
    public void rollBack() {
        NodeModel node = edge.getStart();
        node.getAdjacent().add(edge);
    }
}
