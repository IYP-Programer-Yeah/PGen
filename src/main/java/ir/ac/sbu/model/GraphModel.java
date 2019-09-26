package ir.ac.sbu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GraphModel {
    private StringProperty name;
    private List<NodeModel> nodes;
    private NodeModel start;

    public GraphModel() {
    }

    public GraphModel(String name) {
        this.name = new SimpleStringProperty(name);
        nodes = new ArrayList<>();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setNodes(List<NodeModel> nodes) {
        this.nodes = nodes;
    }

    public NodeModel getStart() {
        return start;
    }

    public void setStart(NodeModel start) {
        if (this.start != null)
            this.start.setStartNode(false);
        this.start = start;
        start.setStartNode(true);
    }

    public List<NodeModel> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    public List<EdgeModel> getEdges() {
        return nodes.stream().map(NodeModel::getAdjacent).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
