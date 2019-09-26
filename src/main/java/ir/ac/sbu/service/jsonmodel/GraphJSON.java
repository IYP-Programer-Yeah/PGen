package ir.ac.sbu.service.jsonmodel;

import ir.ac.sbu.model.GraphModel;

import java.util.List;
import java.util.stream.Collectors;

public class GraphJSON {
    private String name;
    private List<NodeJSON> nodes;
    private List<EdgeJSON> edges;
    private int start = -1;

    public GraphJSON() {
    }

    public GraphJSON(GraphModel g) {
        name = g.getName();
        nodes = g.getNodes().stream().map(NodeJSON::new).collect(Collectors.toList());
        edges = g.getEdges().stream().map(EdgeJSON::new).collect(Collectors.toList());
        if (g.getStart() != null)
            start = g.getStart().getId();
    }

    public String getName() {
        return name;
    }

    public List<NodeJSON> getNodes() {
        return nodes;
    }

    public List<EdgeJSON> getEdges() {
        return edges;
    }

    public int getStart() {
        return start;
    }
}
