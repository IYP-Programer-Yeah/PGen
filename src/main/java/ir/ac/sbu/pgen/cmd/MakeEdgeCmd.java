package ir.ac.sbu.pgen.cmd;

import ir.ac.sbu.pgen.model.EdgeModel;
import ir.ac.sbu.pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class MakeEdgeCmd implements Command {
    NodeModel start, end;
    EdgeModel edge;

    public MakeEdgeCmd(NodeModel start, NodeModel end) {
        this.start = start;
        this.end = end;
        edge = new EdgeModel(start, end);
    }

    @Override
    public void apply() {
        start.getAdjacent().add(edge);
    }

    @Override

    public void rollBack() {
        start.getAdjacent().remove(edge);
    }
}