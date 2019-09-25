package ir.ac.sbu.command;

import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;

public class MakeNodeCmd implements Command {
    GraphModel graphModel;
    NodeModel node;

    public MakeNodeCmd(GraphModel graphModel, double x, double y) {
        this.graphModel = graphModel;
        node = new NodeModel(x, y, graphModel);
    }

    @Override
    public void apply() {
        graphModel.getNodes().add(node);
    }

    @Override
    public void rollBack() {
        graphModel.getNodes().remove(node);
    }
}
