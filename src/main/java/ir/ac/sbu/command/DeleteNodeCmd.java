package ir.ac.sbu.command;

import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteNodeCmd implements Command {
    private GraphModel graphModel;
    private NodeModel nodeModel;
    private List<EdgeModel> removedEdges;

    public DeleteNodeCmd(NodeModel nodeModel) {
        this.graphModel = nodeModel.getGraph();
        this.nodeModel = nodeModel;
    }

    @Override
    public void apply() {
        graphModel.getNodes().remove(nodeModel);
        removedEdges = graphModel.getEdges().stream().filter(model -> model.getEnd().equals(nodeModel)).collect(Collectors.toList());
        graphModel.getNodes().forEach(node -> node.getAdjacent().removeIf(model -> model.getEnd().equals(nodeModel)));
    }

    @Override
    public void rollBack() {
        graphModel.getNodes().add(nodeModel);
        removedEdges.forEach(edge -> edge.getStart().getAdjacent().add(edge));
    }
}
