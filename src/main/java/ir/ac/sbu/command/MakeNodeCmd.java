package ir.ac.sbu.pgen.cmd;

import ir.ac.sbu.pgen.model.GraphModel;
import ir.ac.sbu.pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
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
