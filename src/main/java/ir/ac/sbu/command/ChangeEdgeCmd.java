package ir.ac.sbu.command;

import ir.ac.sbu.model.EdgeModel;

public class ChangeEdgeCmd implements ir.ac.sbu.command.Command {
    EdgeModel edgeModel;
    String token;
    String func;
    Boolean graph;
    Boolean global;


    String _token;
    String _func;
    Boolean _graph;
    Boolean _global;

    public ChangeEdgeCmd(EdgeModel edgeModel, String token, String func, Boolean graph, Boolean global) {
        this.edgeModel = edgeModel;
        this.token = token;
        this.func = func;
        this.global = global;
        this.graph = graph;
    }

    @Override
    public void apply() {
        _token = edgeModel.getToken();
        _func = edgeModel.getFunction();
        _graph = edgeModel.isGraph();
        _global = edgeModel.isGlobal();


        edgeModel.setFunction(func);
        edgeModel.setToken(token);
        edgeModel.setGlobal(global);
        edgeModel.setGraph(graph);

    }

    @Override
    public void rollBack() {

        edgeModel.setFunction(_func);
        edgeModel.setToken(_token);
        edgeModel.setGlobal(_graph);
        edgeModel.setGraph(_graph);

    }
}
