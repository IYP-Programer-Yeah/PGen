package ir.ac.sbu.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.ac.sbu.service.jsonmodel.GraphJSON;
import ir.ac.sbu.utility.DialogUtility;
import ir.ac.sbu.utility.GenerateUID;
import javafx.scene.control.ListView;
import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SaveLoadService {
    private File file;

    public SaveLoadService(File selectedDirectory) {
        file = selectedDirectory;
    }

    public void save(List<GraphModel> graphs) {
        List<GraphJSON> graphJSONs = graphs.stream().map(GraphJSON::new).collect(Collectors.toList());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String out = gson.toJson(graphJSONs);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(out);
        } catch (FileNotFoundException e) {
            DialogUtility.showErrorDialog(e.getMessage());
        }

    }

    public void load(ListView<GraphModel> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<GraphJSON> graphJSONs = null;
        try {
            list.getItems().clear();
            graphJSONs = Arrays.asList(gson.fromJson(new FileReader(file), GraphJSON[].class));
            graphJSONs.forEach(graph ->
            {
                GraphModel graphModel = new GraphModel(graph.getName());
                Map<Integer, NodeModel> nodes = new HashMap<>();
                graph.getNodes().forEach(nodeJSON -> {
                    NodeModel node = new NodeModel(nodeJSON.getX(), nodeJSON.getY(), graphModel, GenerateUID.createID());
                    node.setId(nodeJSON.getId());
                    node.setFinalNode(nodeJSON.isFinal());
                    nodes.put(node.getId(), node);
                    graphModel.getNodes().add(node);
                });
                if (graph.getStart() != -1)
                    if (graphModel.getNodes().stream().anyMatch(nodeJSON -> nodeJSON.getId() == graph.getStart()))
                        graphModel.setStart(graphModel.getNodes().stream().filter(nodeJSON -> nodeJSON.getId() == graph.getStart()).findFirst().get());
                graph.getEdges().forEach(edgeJSON ->
                {
                    NodeModel start = nodes.get(edgeJSON.getStart());
                    NodeModel end = nodes.get(edgeJSON.getEnd());
                    EdgeModel edge = new EdgeModel(start, end);
                    edge.setFunction(edgeJSON.getFunc());
                    edge.setToken(edgeJSON.getToken());
                    edge.anchorXProperty().setValue(edgeJSON.getAnchorX());
                    edge.anchorYProperty().setValue(edgeJSON.getAnchorY());
                    edge.setGraph(edgeJSON.isGraph());
                    edge.setGlobal(edgeJSON.isGlobal());
                    start.getAdjacent().add(edge);

                });
                list.getItems().add(graphModel);
            });
            GenerateUID.setIdCounter(list.getItems().stream().
                    flatMap(graphModel -> graphModel.getNodes().stream()).map(NodeModel::getId).max(Integer::compareTo).get() + 1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
