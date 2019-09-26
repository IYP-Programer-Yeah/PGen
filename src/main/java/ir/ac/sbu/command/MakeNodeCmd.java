package ir.ac.sbu.command;

import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;
import ir.ac.sbu.utility.GenerateUID;

public class MakeNodeCmd implements Command {
    private GraphModel graphModel;
    private NodeModel node;

    public MakeNodeCmd(GraphModel graphModel, double x, double y) {
        this.graphModel = graphModel;
        this.node = new NodeModel(x, y, graphModel, GenerateUID.createID());
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
