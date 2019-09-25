package ir.ac.sbu.service;

import ir.ac.sbu.model.GraphModel;

import java.util.List;
import java.util.stream.Collectors;

public class GraphJSON {
    String name;
    List<NodeJSON> nodes;
    List<EdgeJSON> edges;

    Integer start = -1;

    public GraphJSON() {
    }

    public GraphJSON(GraphModel g) {
        name = g.getName();
        nodes = g.getNodes().stream().map(NodeJSON::new).collect(Collectors.toList());
        edges = g.getEdges().stream().map(EdgeJSON::new).collect(Collectors.toList());
        if (g.getStart() != null)
            start = g.getStart().getId();
    }

}
